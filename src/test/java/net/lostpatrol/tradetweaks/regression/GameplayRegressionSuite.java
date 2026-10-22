/** Runs focused gameplay checks against real entities on a loaded dedicated server. */
package net.lostpatrol.tradetweaks.regression;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.lostpatrol.tradetweaks.common.item.ModItems;
import net.lostpatrol.tradetweaks.common.item.villager.VillagerConversionMemory;
import net.lostpatrol.tradetweaks.common.tradeselect.TradeSelectionSessionManager;
import net.lostpatrol.tradetweaks.common.wand.EmeraldWand;
import net.lostpatrol.tradetweaks.common.wand.handler.HandlerUpgradeVillager;
import net.lostpatrol.tradetweaks.platform.Platform;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class GameplayRegressionSuite {
    // One Minecraft day, used to verify the daily restock reset.
    private static final long DAY_TICKS = 24_000L;
    private int assertions;

    /** Adds a loader callback check while retaining the same real server fixture. */
    public static void run(MinecraftServer server, BiConsumer<ServerPlayer, Villager> loaderCheck, Consumer<ServerPlayer> raidTick) throws Exception {
        Path result = Path.of(System.getProperty("tradetweaks.regressionResult", "build/gameplay-result.txt"));
        try {
            GameplayRegressionSuite suite = new GameplayRegressionSuite();
            suite.checkGameplay(server, loaderCheck, raidTick);
            Files.createDirectories(result.toAbsolutePath().getParent());
            Files.writeString(result, "PASS assertions=" + suite.assertions + System.lineSeparator());
            System.out.println("TRADETWEAKS GAMEPLAY PASS assertions=" + suite.assertions);
        } catch (Exception | AssertionError failure) {
            failure.printStackTrace();
            try {
                Files.createDirectories(result.toAbsolutePath().getParent());
                Files.writeString(result, "FAIL " + failure + System.lineSeparator());
            } catch (Exception outputFailure) {
                outputFailure.printStackTrace();
            }
            throw failure;
        }
    }

    /** Exercises independent business rules without client or socket substitutes. */
    private void checkGameplay(MinecraftServer server, BiConsumer<ServerPlayer, Villager> loaderCheck, Consumer<ServerPlayer> raidTick) throws Exception {
        ServerLevel level = server.overworld();
        QuietPlayer owner = new QuietPlayer(server, level, "RegressionOwner");
        QuietPlayer other = new QuietPlayer(server, level, "RegressionOther");
        registerPlayer(server, owner);
        registerPlayer(server, other);
        Villager villager = new Villager(EntityType.VILLAGER, level);
        villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.FARMER));
        villager.setNoAi(true);
        BlockPos origin = level.getSharedSpawnPos();
        villager.setPos(origin.getX(), origin.getY() + 2, origin.getZ());
        owner.setPos(villager.getX(), villager.getY(), villager.getZ());
        other.setPos(villager.getX(), villager.getY(), villager.getZ());
        level.addFreshEntity(villager);
        try {
            checkRestorationRecipe(server);
            checkSelection(server, owner, other, villager);
            checkUpgrade(owner, villager);
            checkRestock(owner, villager);
            checkConversion(level, villager);
            checkRaidReward(owner, raidTick);
            loaderCheck.accept(owner, villager);
        } finally {
            TradeSelectionSessionManager.onPlayerLoggedOut(owner);
            TradeSelectionSessionManager.onPlayerLoggedOut(other);
            unregisterPlayer(server, owner);
            unregisterPlayer(server, other);
            villager.discard();
        }
    }

    /** Verifies actual loaded potion ingredient predicates across every supported form. */
    private void checkRestorationRecipe(MinecraftServer server) {
        var holder = server.getRecipeManager().byKey(new ResourceLocation("tradetweaks", "restoration_bottle"));
        require(holder.isPresent(), "restoration bottle recipe loads on this loader");
        var ingredients = holder.orElseThrow().getIngredients();
        require(ingredients.size() == 3, "restoration bottle retains all three ingredients");
        var potionIngredient = ingredients.get(2);
        for (var item : java.util.List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION)) {
            for (var potion : java.util.List.of(Potions.WEAKNESS, Potions.LONG_WEAKNESS, Potions.WATER, Potions.HEALING)) {
                ItemStack stack = new ItemStack(item);
                PotionUtils.setPotion(stack, potion);
                boolean expected = potion.equals(Potions.WEAKNESS) || potion.equals(Potions.LONG_WEAKNESS);
                require(potionIngredient.test(stack) == expected, "restoration ingredient accepts only weakness potions for " + item + ":" + potion);
            }
        }
    }

    /** Verifies real stat award dispatch, including the loader's registered end-tick adapter. */
    private void checkRaidReward(QuietPlayer player, Consumer<ServerPlayer> raidTick) {
        player.getInventory().clearContent();
        // Set effect state directly because this harness has no client effect packet sink.
        player.getActiveEffectsMap().put(MobEffects.HERO_OF_THE_VILLAGE,
                new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 600, 1));
        raidTick.accept(player);
        player.awardStat(Stats.CUSTOM.get(Stats.RAID_WIN), 1);
        raidTick.accept(player);
        int rewards = player.getInventory().countItem(ModItems.TOTEM_OF_VILLAGE_HERO.get());
        require(rewards >= 4 && rewards <= 6, "one raid win dispatch grants the configured hero-tier reward once");
        player.awardStat(Stats.CUSTOM.get(Stats.JUMP), 1);
        raidTick.accept(player);
        require(player.getInventory().countItem(ModItems.TOTEM_OF_VILLAGE_HERO.get()) == rewards, "unrelated stat awards cannot duplicate raid rewards");
    }

    /** Verifies locking, successful replacement, rejection and abandoned-session cleanup. */
    private void checkSelection(MinecraftServer server, QuietPlayer owner, QuietPlayer other, Villager villager) throws Exception {
        ItemStack wand = new ItemStack(ModItems.EMERALD_WAND.get());
        wand.getOrCreateTag().putBoolean("upgrade", true);
        EmeraldWand.setMode(wand, EmeraldWand.WandMode.SELECT_MODE);
        owner.setItemInHand(InteractionHand.MAIN_HAND, wand);
        var session = TradeSelectionSessionManager.createSession(owner, villager);
        require(session != null && TradeSelectionSessionManager.isSelectionLocked(villager), "selection owns villager lock");
        checkSelectionPacket(owner.serverLevel(), session);
        require(TradeSelectionSessionManager.createSession(other, villager) == null, "second player cannot acquire lock");
        int pool = session.getCandidatePoolIndices()[0];
        require(!session.getCandidatePools().get(pool).isEmpty(), "registered farmer listings generate candidates");
        var expected = session.getCandidatePools().get(pool).get(0);
        TradeSelectionSessionManager.replaceTrade(owner, session.getSessionId(), 0, 0);
        require(villager.getOffers().get(0) == expected, "selected registered offer replaces original");
        require(!TradeSelectionSessionManager.isSelectionLocked(villager) && !villager.isTrading(), "replacement releases lock");

        session = TradeSelectionSessionManager.createSession(owner, villager);
        var unchanged = villager.getOffers().get(0);
        owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        TradeSelectionSessionManager.replaceTrade(owner, session.getSessionId(), 0, 0);
        require(villager.getOffers().get(0) == unchanged && !villager.isTrading(), "missing selector rejects mutation and releases lock");

        session = TradeSelectionSessionManager.createSession(owner, villager);
        TradeSelectionSessionManager.closeSession(owner, UUID.randomUUID());
        require(TradeSelectionSessionManager.isSelectionLocked(villager), "unrelated close cannot release live session");
        TradeSelectionSessionManager.closeSession(owner, session.getSessionId());
        require(!villager.isTrading(), "correct close releases villager");
        TradeSelectionSessionManager.createSession(owner, villager);
        TradeSelectionSessionManager.onPlayerLoggedOut(owner);
        require(!villager.isTrading(), "logout releases villager");

        TradeSelectionSessionManager.createSession(owner, villager);
        Method cleanup = TradeSelectionSessionManager.class.getDeclaredMethod("cleanupSessions", MinecraftServer.class, long.class);
        cleanup.setAccessible(true);
        cleanup.invoke(null, server, Long.MAX_VALUE);
        require(!villager.isTrading(), "expired session cleanup releases villager");
    }

    /** Roundtrips actual candidate data through the shared production packet codec. */
    private void checkSelectionPacket(ServerLevel level, PacketOpenTradeSelection original) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            original.encode(buffer);
            PacketOpenTradeSelection decoded = new PacketOpenTradeSelection(buffer);
            require(decoded.getSessionId().equals(original.getSessionId()) && decoded.getVillagerId() == original.getVillagerId(), "selection packet preserves session and entity identity");
            require(java.util.Arrays.equals(decoded.getCandidatePoolIndices(), original.getCandidatePoolIndices()) && decoded.getCandidatePools().size() == original.getCandidatePools().size(), "selection packet preserves trade-level pool mapping");
            for (int pool = 0; pool < original.getCandidatePools().size(); pool++) {
                MerchantOffers before = original.getCandidatePools().get(pool);
                MerchantOffers after = decoded.getCandidatePools().get(pool);
                require(after.size() == before.size(), "selection packet preserves candidate count");
                for (int offer = 0; offer < before.size(); offer++) {
                    require(ItemStack.matches(before.get(offer).getResult(), after.get(offer).getResult())
                            && ItemStack.matches(before.get(offer).getBaseCostA(), after.get(offer).getBaseCostA())
                            && ItemStack.matches(before.get(offer).getCostB(), after.get(offer).getCostB()), "selection packet preserves candidate item data and payment");
                }
            }
            require(buffer.readableBytes() == 0, "selection packet consumes the complete encoded payload");
        } finally {
            buffer.release();
        }
    }

    /** Verifies cost priority, split stacks and exact insufficient-funds behavior. */
    private void checkUpgrade(QuietPlayer owner, Villager villager) throws Exception {
        owner.getInventory().clearContent();
        owner.getInventory().setItem(0, new ItemStack(Items.EMERALD_BLOCK));
        owner.getInventory().setItem(1, new ItemStack(Items.EMERALD, 4));
        owner.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.EMERALD, 5));
        int before = villager.getVillagerData().getLevel();
        HandlerUpgradeVillager.handle(owner, villager);
        require(villager.getVillagerData().getLevel() == before + 1, "paid upgrade advances career");
        require(owner.getInventory().getItem(0).isEmpty() && countEmeralds(owner) == 9, "first affordable cost wins without consuming alternative");
        HandlerUpgradeVillager.handle(owner, villager);
        require(countEmeralds(owner) == 0, "upgrade aggregates and consumes split main/offhand payment");
        before = villager.getVillagerData().getLevel();
        HandlerUpgradeVillager.handle(owner, villager);
        require(villager.getVillagerData().getLevel() == before, "unpaid upgrade does not change career");
    }

    /** Verifies actual item consumption and restored stock across day boundaries. */
    private void checkRestock(QuietPlayer owner, Villager villager) {
        owner.setShiftKeyDown(true);
        ItemStack writs = new ItemStack(ModItems.RESTOCK_WRIT.get(), 10);
        owner.setItemInHand(InteractionHand.MAIN_HAND, writs);
        exhaust(villager.getOffers());
        writs.getItem().interactLivingEntity(writs, owner, villager, InteractionHand.MAIN_HAND);
        require(writs.getCount() == 9 && !villager.getOffers().get(0).isOutOfStock(), "first daily restock costs one writ and restores stock");
        exhaust(villager.getOffers());
        writs.getItem().interactLivingEntity(writs, owner, villager, InteractionHand.MAIN_HAND);
        require(writs.getCount() == 7, "second same-day restock costs two writs");
        exhaust(villager.getOffers());
        writs.setCount(2);
        writs.getItem().interactLivingEntity(writs, owner, villager, InteractionHand.MAIN_HAND);
        require(writs.getCount() == 2 && villager.getOffers().get(0).isOutOfStock(), "insufficient escalating payment leaves stock unchanged");
        ServerLevel level = owner.serverLevel();
        level.setDayTime(level.getDayTime() + DAY_TICKS);
        writs.getItem().interactLivingEntity(writs, owner, villager, InteractionHand.MAIN_HAND);
        require(writs.getCount() == 1 && !villager.getOffers().get(0).isOutOfStock(), "new day resets restock cost");
    }

    /** Verifies the loader's actual NBT persistence and vanilla curing hook. */
    private void checkConversion(ServerLevel level, Villager original) throws Exception {
        GlobalPos workstation = GlobalPos.of(level.dimension(), original.blockPosition().below());
        original.getBrain().setMemory(MemoryModuleType.JOB_SITE, workstation);
        ZombieVillager zombie = new ZombieVillager(EntityType.ZOMBIE_VILLAGER, level);
        VillagerConversionMemory.storeJobSite(zombie, VillagerConversionMemory.encodeJobSite(original).orElseThrow());
        CompoundTag saved = new CompoundTag();
        zombie.saveWithoutId(saved);
        ZombieVillager reloaded = new ZombieVillager(EntityType.ZOMBIE_VILLAGER, level);
        reloaded.load(saved);
        reloaded.setVillagerData(original.getVillagerData());
        reloaded.setPos(original.getX() + 5, original.getY(), original.getZ());
        require(Platform.persistentData(reloaded).contains("tradetweaks:job_site"), "zombie workstation survives entity NBT roundtrip");
        level.addFreshEntity(reloaded);
        AABB conversionArea = reloaded.getBoundingBox().inflate(1);
        Method finish = ZombieVillager.class.getDeclaredMethod("finishConversion", ServerLevel.class);
        finish.setAccessible(true);
        finish.invoke(reloaded, level);
        var converted = level.getEntitiesOfClass(Villager.class, conversionArea);
        require(converted.size() == 1, "vanilla zombie curing creates a villager");
        Villager cured = converted.get(0);
        try {
            require(cured.getBrain().getMemory(MemoryModuleType.JOB_SITE).orElseThrow().equals(workstation), "actual vanilla curing hook restores workstation");
        } finally {
            cured.discard();
        }
    }

    /** Counts all emerald payment slots exposed by the platform. */
    private int countEmeralds(QuietPlayer player) {
        int count = 0;
        var inventory = Platform.inventory(player);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.is(Items.EMERALD)) count += stack.getCount();
        }
        return count;
    }

    /** Exhausts stock through vanilla offer behavior. */
    private void exhaust(MerchantOffers offers) {
        offers.forEach(offer -> { while (!offer.isOutOfStock()) offer.increaseUses(); });
    }

    /** Records a meaningful assertion or fails this server run. */
    private void require(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
        assertions++;
        System.out.println("GAMEPLAY CHECK " + description);
    }

    /** Makes test players discoverable to the production session cleanup logic. */
    @SuppressWarnings("unchecked")
    private void registerPlayer(MinecraftServer server, ServerPlayer player) throws Exception {
        Field field = net.minecraft.server.players.PlayerList.class.getDeclaredField("playersByUUID");
        field.setAccessible(true);
        ((Map<UUID, ServerPlayer>) field.get(server.getPlayerList())).put(player.getUUID(), player);
    }

    /** Removes test players without a network disconnect. */
    @SuppressWarnings("unchecked")
    private void unregisterPlayer(MinecraftServer server, ServerPlayer player) throws Exception {
        Field field = net.minecraft.server.players.PlayerList.class.getDeclaredField("playersByUUID");
        field.setAccessible(true);
        ((Map<UUID, ServerPlayer>) field.get(server.getPlayerList())).remove(player.getUUID());
    }

    /** Keeps real server-player state while suppressing packets to a nonexistent client. */
    private static final class QuietPlayer extends ServerPlayer {
        /** Constructs a player using the dedicated server's real registries. */
        private QuietPlayer(MinecraftServer server, ServerLevel level, String name) {
            super(server, level, new GameProfile(UUID.randomUUID(), name));
        }
        /** Retains real cooldown behavior without requiring client notification packets. */
        @Override protected ItemCooldowns createItemCooldowns() { return new ItemCooldowns(); }
        /** Exercises paid gameplay regardless of the GameTest world's creative default. */
        @Override public boolean isCreative() { return false; }
        /** Keeps interaction eligibility equivalent to an ordinary survival player. */
        @Override public boolean isSpectator() { return false; }
        /** Discards only client-facing status messages. */
        @Override public void displayClientMessage(Component message, boolean overlay) { }
        /** Discards the optional visual swing packet. */
        @Override public void swing(InteractionHand hand, boolean updateSelf) { }
        /** Preserves the real raid award path while suppressing unrelated item bookkeeping. */
        @Override public void awardStat(Stat<?> stat, int amount) {
            if (stat.equals(Stats.CUSTOM.get(Stats.RAID_WIN)) || stat.equals(Stats.CUSTOM.get(Stats.JUMP))) {
                super.awardStat(stat, amount);
            }
        }
    }
}
