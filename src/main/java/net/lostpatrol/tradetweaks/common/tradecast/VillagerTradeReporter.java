package net.lostpatrol.tradetweaks.common.tradecast;

import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.lostpatrol.tradetweaks.util.VillagerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.*;

import static net.lostpatrol.tradetweaks.util.DeepCopy.deepCopyOffers;
import static net.lostpatrol.tradetweaks.util.DisplayRoman.toRoman;

public class VillagerTradeReporter {

    public final static int DURATION_TICKS = 300;

    public static class PlayerTradeData {
        public final Set<UUID> reportedVillagers = new HashSet<>();
        public final Map<UUID, MerchantOffers> lastKnownOffers = new HashMap<>();
    }

    public static final Map<UUID, PlayerTradeData> playerTradeDataMap = new HashMap<>();

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(VillagerTradeReporter::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(VillagerTradeReporter::onVillagerDeath);
    }

    // Immediately report when villager profession changes
    // TODO
    // forge/minecraft does not provide VillagerProfessionChangeEvent. this causes problems

    // Store trade lists in files to persist across world sessions
    // TODO
    // Is it necessary?

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (ClientConfig.tempMode == ClientConfig.ReportMode.OFF) {
            return;
        }

        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        if (event.player.level().getGameTime() % ServerConfig.tempIntervalTicks != 0) {
            return;
        }

        List<AbstractVillager> villagers = event.player.level().getEntitiesOfClass(
                AbstractVillager.class,
                new AABB(event.player.blockPosition()).inflate(ServerConfig.tempRadiusBlocks),
                villager -> {
                    if (!(villager instanceof Villager)) {
                        return false;
                    }
                    if (!villager.isAlive()) {
                        return false;
                    }
                    if (ClientConfig.tempMode == ClientConfig.ReportMode.LIBRARIAN_ONLY) {
                        return ((Villager) villager).getVillagerData().getProfession() == VillagerProfession.LIBRARIAN;
                    }
                    return true;
                }
        );

        PlayerTradeData playerData = getPlayerData(event.player.getUUID());
        for (AbstractVillager villager : villagers) {
            if (villager instanceof Villager v) {
                MerchantOffers currentOffers = v.getOffers();
                UUID villagerId = v.getUUID();

                if (!hasOffersChanged(playerData, villagerId, currentOffers)) {
                    continue;
                }

                reportTrades(v, event.player, false);
                playerData.lastKnownOffers.put(villagerId, deepCopyOffers(currentOffers));
            }
        }
    }



    private static boolean hasOffersChanged(PlayerTradeData playerData, UUID villagerId, MerchantOffers currentOffers) {
        if (!playerData.lastKnownOffers.containsKey(villagerId)) return true;

        MerchantOffers lastOffers = playerData.lastKnownOffers.get(villagerId);
        if (lastOffers.size() != currentOffers.size()) return true;

        for (int i = 0; i < currentOffers.size(); i++) {
            if (!compareOffers(lastOffers.get(i), currentOffers.get(i))) {
                return true;
            }
        }
        return false;
    }

    private static boolean compareOffers(MerchantOffer a, MerchantOffer b) {
        if (!ItemStack.matches(a.getBaseCostA(), b.getBaseCostA())) return false;
        if (!ItemStack.matches(a.getCostB(), b.getCostB())) return false;
        return ItemStack.matches(a.getResult(), b.getResult());
    }

    public static void reportTrades(Villager villager, Player player, boolean usingWand) {
        MerchantOffers offers = villager.getOffers();
        if (offers.isEmpty()) {
            return;
        }

        boolean hasMending = false;
        VillagerData data = villager.getVillagerData();
        Component professionName = VillagerUtil.getProfessionComponent(data.getProfession());
        Component levelName = VillagerUtil.getLevelComponent(data.getLevel());
        MutableComponent header = Component.translatable("tradetweaks.tradecast.report.header", professionName, levelName)
                .withStyle(ChatFormatting.BOLD);

        header.withStyle(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tradetweaks_internal highlight "+villager.getUUID()+" "+DURATION_TICKS))
                .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("tradetweaks.tradecast.highlight.tip").withStyle(ChatFormatting.YELLOW)
                ))
        );

        player.displayClientMessage(header, false);

        // calculate special price diff first
        MerchantOffers tempOffers = deepCopyOffers(offers);
        updateSpecialPrices(villager, tempOffers, player);

        for (MerchantOffer offer : tempOffers) {
            ItemStack buying1 = offer.getCostA();
            ItemStack buying2 = offer.getCostB();
            ItemStack selling = offer.getResult();

            if (selling.getItem() == Items.ENCHANTED_BOOK && EnchantmentHelper.getEnchantments(selling).containsKey(Enchantments.MENDING)) {
                hasMending = true;
            }

            MutableComponent message = buildTradeMessage(buying1, buying2, selling);
            player.displayClientMessage(message, false);
        }


        if (!usingWand && hasMending) {
            VillagerUtil.highlightVillagerWithEntity(villager, 600);
        }
    }

    private static MutableComponent buildTradeMessage(ItemStack buying1, ItemStack buying2, ItemStack selling) {
        Component item1 = formatItemStack(buying1);
        Component item2 = buying2.isEmpty() ? Component.empty() : formatItemStack(buying2);
        Component result = formatItemStack(selling);

        if (buying2.isEmpty()) {
            return Component.translatable("tradetweaks.tradecast.trade.format_single",
                    item1, buying1.getCount(),
                    result, selling.getCount());
        } else {
            return Component.translatable("tradetweaks.tradecast.trade.format",
                    item1, buying1.getCount(),
                    item2, buying2.getCount(),
                    result, selling.getCount());
        }
    }

    // broadcast format and style
    private static Component formatItemStack(ItemStack stack) {
        if (stack.getItem() == Items.EMERALD) {
            return stack.getDisplayName().copy().withStyle(ChatFormatting.GREEN);
        }

        if (stack.getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (!enchantments.isEmpty()) {
                Map.Entry<Enchantment, Integer> entry = enchantments.entrySet().iterator().next();

                return Component.literal("[")
                        .append(Component.translatable(entry.getKey().getDescriptionId()))
                        .append(" " + toRoman(entry.getValue()))
                        .append("]")
                        .withStyle(ChatFormatting.YELLOW);
            }
        }
//        return stack.getHoverName();
        return stack.getDisplayName();
    }

    private static void onVillagerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Villager villager)) return;

        UUID villagerId = villager.getUUID();
        for (PlayerTradeData data : playerTradeDataMap.values()) {
            data.lastKnownOffers.remove(villagerId);
            data.reportedVillagers.remove(villagerId);
        }
    }

    private static PlayerTradeData getPlayerData(UUID playerId) {
        return playerTradeDataMap.computeIfAbsent(playerId, k -> new PlayerTradeData());
    }

    private static void updateSpecialPrices(Villager villager, MerchantOffers tempOffers, Player player) {
        int i = villager.getPlayerReputation(player);
        if (i != 0) {
            for(MerchantOffer merchantoffer : tempOffers) {
                merchantoffer.addToSpecialPriceDiff(-Mth.floor((float)i * merchantoffer.getPriceMultiplier()));
            }
        }

        if (player.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
            MobEffectInstance mobeffectinstance = player.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
            int k = 0;
            if (mobeffectinstance != null) {
                k = mobeffectinstance.getAmplifier();
            }

            for(MerchantOffer merchantoffer1 : tempOffers) {
                double d0 = 0.3D + 0.0625D * (double)k;
                int j = (int)Math.floor(d0 * (double)merchantoffer1.getBaseCostA().getCount());
                merchantoffer1.addToSpecialPriceDiff(-Math.max(j, 1));
            }
        }
    }


}
