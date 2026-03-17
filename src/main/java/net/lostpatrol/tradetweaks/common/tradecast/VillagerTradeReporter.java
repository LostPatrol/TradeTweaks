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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

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
        NeoForge.EVENT_BUS.addListener(VillagerTradeReporter::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(VillagerTradeReporter::onVillagerDeath);
    }

    // Immediately report when villager profession changes
    // TODO
    // forge/minecraft does not provide VillagerProfessionChangeEvent. this causes problems

    // Store trade lists in files to persist across world sessions
    // TODO
    // Is it necessary?

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (ClientConfig.tempMode == ClientConfig.ReportMode.OFF) {
            return;
        }

        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        if (player.level().getGameTime() % ServerConfig.tempIntervalTicks != 0) {
            return;
        }

        List<AbstractVillager> villagers = player.level().getEntitiesOfClass(
                AbstractVillager.class,
                new AABB(player.blockPosition()).inflate(ServerConfig.tempRadiusBlocks),
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

        PlayerTradeData playerData = getPlayerData(player.getUUID());
        for (AbstractVillager villager : villagers) {
            if (villager instanceof Villager v) {
                MerchantOffers currentOffers = v.getOffers();
                UUID villagerId = v.getUUID();

                if (!hasOffersChanged(playerData, villagerId, currentOffers)) {
                    continue;
                }

                reportTrades(v, player, false);
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

        boolean hasMending = false;
        net.minecraft.core.Registry<net.minecraft.world.item.enchantment.Enchantment> enchantmentRegistry =
                player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Holder<net.minecraft.world.item.enchantment.Enchantment> mendingHolder =
                enchantmentRegistry.getHolder(Enchantments.MENDING).orElse(null);
        for (MerchantOffer offer : tempOffers) {
            ItemStack buying1 = offer.getCostA();
            ItemStack buying2 = offer.getCostB();
            ItemStack selling = offer.getResult();

            if (mendingHolder != null
                    && selling.getItem() == Items.ENCHANTED_BOOK
                    && EnchantmentHelper.getEnchantmentsForCrafting(selling).getLevel(mendingHolder) > 0) {
                hasMending = true;
            }

            MutableComponent message = buildTradeMessage(buying1, buying2, selling, offer.isOutOfStock());
            player.displayClientMessage(message, false);
        }


        if (!usingWand && hasMending) {
            VillagerUtil.highlightVillagerWithEntity(villager, 600);
        }
    }

    private static MutableComponent buildTradeMessage(ItemStack buying1, ItemStack buying2, ItemStack selling, boolean outOfStock) {
        Component item1 = formatItemStack(buying1);
        Component item2 = buying2.isEmpty() ? Component.empty() : formatItemStack(buying2);
        Component result = formatItemStack(selling);

        MutableComponent baseMessage;

        if (buying2.isEmpty()) {
            baseMessage = Component.translatable("tradetweaks.tradecast.trade.format_single",
                    item1, buying1.getCount(),
                    result, selling.getCount());
        } else {
            baseMessage = Component.translatable("tradetweaks.tradecast.trade.format",
                    item1, buying1.getCount(),
                    item2, buying2.getCount(),
                    result, selling.getCount());
        }

        return outOfStock
                ? baseMessage.append(Component.literal(" ").append(Component.translatable("tradetweaks.tradecast.trade.out_of_stock").withStyle(ChatFormatting.RED)))
                : baseMessage;
    }

    // broadcast format and style
    private static Component formatItemStack(ItemStack stack) {
        MutableComponent icon = net.lostpatrol.tradetweaks.client.render.ItemRenderer.createIconComponent(stack);

        if (stack.getItem() == Items.EMERALD) {
            return icon.append(stack.getDisplayName().copy().withStyle(ChatFormatting.GREEN));
        }

        if (stack.getItem() == Items.ENCHANTED_BOOK) {
            ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
            if (!enchantments.isEmpty()) {
                var entry = enchantments.entrySet().iterator().next();

                return icon.append(Component.literal("[")
                        .append(entry.getKey().value().description())
                        .append(" " + toRoman(entry.getIntValue()))
                        .append("]")
                        .withStyle(ChatFormatting.YELLOW));
            }
        }
        return icon.append(stack.getDisplayName());
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


