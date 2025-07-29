package net.lostpatrol.tradetweaks.common.item.custom;

import net.lostpatrol.tradetweaks.network.packet.PacketBlockHighlight;
import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSwitch;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

import java.util.Optional;
import java.util.Set;

import net.lostpatrol.tradetweaks.util.Util;
import net.minecraft.world.phys.AABB;

public class EmeraldWand extends Item {
    private static final String MODE_TAG = "wand_mode";

    private static final int DURATION_TICKS = 300;
    private static final int COOLDOWN_TICKS = 10;
    private static final int TRACK_RADIUS = 64;

    private static final float R = 1.0F;
    private static final float G = 0.0F;
    private static final float B = 0.0F;
//    private static final float A = 0.0F;

    private static final Set<WandMode> ENTITY_MODES = Set.of(
            WandMode.RESET_MODE,
            WandMode.REFRESH_MODE,
            WandMode.TRACKING_BLOCK_MODE
    );

    private static final Set<WandMode> BLOCK_MODES = Set.of(
            WandMode.TRACKING_VILLAGER_MODE
    );

    private static final Component RESET_SUCCESS = Component.translatable("tradetweaks.emerald_wand.success.reset").withStyle(ChatFormatting.DARK_GREEN);
    private static final Component RESET_FAIL = Component.translatable("tradetweaks.emerald_wand.fail.villager_not_occupied").withStyle(ChatFormatting.RED);
    private static final Component REFRESH_SUCCESS = Component.translatable("tradetweaks.emerald_wand.success.refresh").withStyle(ChatFormatting.DARK_GREEN);
    private static final Component REFRESH_FAIL = Component.translatable("tradetweaks.emerald_wand.fail.villager_occupied").withStyle(ChatFormatting.RED);

    private static Component TRACK_BLOCK_SUCCESS(BlockPos pos) {
        return Component.translatable("tradetweaks.emerald_wand.success.track_block", pos.getX(), pos.getY(), pos.getZ())
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, pos.getX() + " " + pos.getY() + " " + pos.getZ()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("tradetweaks.emerald_wand.text.copy"))));
    }

    private static final Component TRACK_BLOCK_FAIL = Component.translatable("tradetweaks.emerald_wand.fail.villager_not_occupied").withStyle(ChatFormatting.RED);
    private static final Component TRACK_BLOCK_FAIL2 = Component.translatable("tradetweaks.emerald_wand.fail.block_not_exist").withStyle(ChatFormatting.RED);

    private static Component TRACK_VILLAGER_SUCCESS(Villager v) {
        return Component.translatable("tradetweaks.emerald_wand.success.track_villager", v.getBlockX(), v.getBlockY(), v.getBlockZ())
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, v.getBlockX() + " " + v.getBlockY() + " " + v.getBlockZ()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("tradetweaks.emerald_wand.text.copy"))));
    }

    private static final Component TRACK_VILLAGER_FAIL = Component.translatable("tradetweaks.emerald_wand.fail.block_not_occupied").withStyle(ChatFormatting.RED);


    public void switchMode(ItemStack stack, Player player, boolean forward) {
        if (player.level().isClientSide) {
            NetworkHandler.getChannel().sendToServer(
                    new PacketWandModeSwitch(forward)
            );
        }

        WandMode current = getMode(stack);
        WandMode newMode = forward ? current.next() : current.previous();
        player.displayClientMessage(
                Component.translatable("tradetweaks.emerald_wand.mode." + newMode.getTranslateKey()),
                true
        );
    }

    public static WandMode getMode(ItemStack stack) {
        if (!stack.hasTag() || stack.getTag() == null) {
            return WandMode.RESET_MODE;
        }
        if (!stack.getTag().contains(MODE_TAG)) {
            return WandMode.RESET_MODE;
        }
        return WandMode.valueOf(stack.getTag().getString(MODE_TAG));
    }

    public static void setMode(ItemStack stack, WandMode mode) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(MODE_TAG, mode.name());
    }

    public enum WandMode {
        RESET_MODE("reset"),
        TRACKING_BLOCK_MODE("tracking_block"),
        TRACKING_VILLAGER_MODE("tracking_villager"),
        REFRESH_MODE("refresh");

        private final String name;

        WandMode(String name) {
            this.name = name;
        }

        public String getTranslateKey() {
            return name;
        }

        public WandMode next() {
            WandMode[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }

        public WandMode previous() {
            WandMode[] values = values();
            int index = this.ordinal() - 1;
            return index < 0 ? values[values.length - 1] : values[index];
        }
    }

    public EmeraldWand() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    @Nonnull
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity entity, @Nonnull InteractionHand hand) {
        if (player.level().isClientSide){
            return InteractionResult.PASS;
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }
        if (!(entity instanceof Villager villager)){
            return InteractionResult.FAIL;
        }

        WandMode mode = getMode(stack);
        if (ENTITY_MODES.contains(mode)){
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            player.swing(InteractionHand.MAIN_HAND);
        }

        return switch (mode) {
            case RESET_MODE -> handleVillagerReset(player, villager);
            case REFRESH_MODE -> handleVillagerRefresh(player, villager);
            case TRACKING_BLOCK_MODE -> handleTrackBlock(player, villager);
            default -> InteractionResult.FAIL;
        };
    }

    private InteractionResult handleVillagerReset(Player player, Villager villager) {
        if (Util.isUnemployed(villager) || Util.isNitwit(villager)) {
            player.displayClientMessage(RESET_FAIL, true);
            return InteractionResult.SUCCESS;
        }

        villager.releasePoi(MemoryModuleType.JOB_SITE);
        villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);

        villager.setVillagerData(villager.getVillagerData()
                .setProfession(VillagerProfession.NONE)
                .setLevel(1));
        villager.setVillagerXp(0);
        villager.clearRestriction();
        villager.getOffers().clear();

        Brain<Villager> brain = villager.getBrain();

        brain.eraseMemory(MemoryModuleType.JOB_SITE);
        brain.eraseMemory(MemoryModuleType.LAST_WORKED_AT_POI);
        brain.eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        brain.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);

        villager.getNavigation().stop();

        brain.setActiveActivityIfPossible(Activity.IDLE);

        player.displayClientMessage(RESET_SUCCESS, true);

        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleVillagerRefresh(Player player, Villager villager) {
        if (!Util.isUnemployed(villager) || Util.isNitwit(villager)) {
            player.displayClientMessage(REFRESH_FAIL, true);
            return InteractionResult.SUCCESS;
        }

        villager.releasePoi(MemoryModuleType.JOB_SITE);
        villager.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);

        Brain<Villager> brain = villager.getBrain();

        brain.eraseMemory(MemoryModuleType.LAST_WORKED_AT_POI);
        brain.eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
        villager.getNavigation().stop();

        brain.setActiveActivityIfPossible(Activity.IDLE);

        player.displayClientMessage(REFRESH_SUCCESS, true);

        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleTrackBlock(Player player, Villager villager) {
        if (Util.isUnemployed(villager) || Util.isNitwit(villager)) {
            player.displayClientMessage(TRACK_BLOCK_FAIL, true);
            return InteractionResult.SUCCESS;
        }
        Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isPresent()) {
            BlockPos pos = jobSite.get().pos();
            player.sendSystemMessage(TRACK_BLOCK_SUCCESS(pos));
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendBlockHighlightToPlayer(
                        serverPlayer,
                        new PacketBlockHighlight(pos, DURATION_TICKS, R, G, B)
                );
            }
        }
        else {
            player.displayClientMessage(TRACK_BLOCK_FAIL2, true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @Nonnull
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();

        if (world.isClientSide || player == null) {
            return InteractionResult.PASS;
        }

        WandMode mode = getMode(stack);
        if (mode == WandMode.TRACKING_VILLAGER_MODE) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            player.swing(InteractionHand.MAIN_HAND);
            return handleTrackVillager(player, pos, (ServerLevel) world);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult handleTrackVillager(Player player, BlockPos pos, ServerLevel level) {
        Optional<Villager> optionalVillager = level.getEntitiesOfClass(
                        Villager.class,
                        new AABB(pos).inflate(TRACK_RADIUS)
                ).stream()
                .filter(villager -> isWorkstationForVillager(villager, pos))
                .findFirst();

        if (optionalVillager.isEmpty()) {
            player.displayClientMessage(TRACK_VILLAGER_FAIL, true);
            return InteractionResult.SUCCESS;
        }

        Villager villager = optionalVillager.get();
        Util.highlightVillagerWithEntity(villager, DURATION_TICKS);

        player.sendSystemMessage(TRACK_VILLAGER_SUCCESS(villager));
        return InteractionResult.SUCCESS;
    }

    private boolean isWorkstationForVillager(Villager villager, BlockPos pos) {
        return villager.getBrain().getMemory(MemoryModuleType.JOB_SITE)
                .map(globalPos -> globalPos.pos().equals(pos))
                .orElse(false);
    }
}
