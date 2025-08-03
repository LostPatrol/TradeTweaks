package net.lostpatrol.tradetweaks.common.wand;

import net.lostpatrol.tradetweaks.network.NetworkHandler;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSwitch;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

import java.util.Set;

import net.lostpatrol.tradetweaks.common.wand.handler.*;

public class EmeraldWand extends Item {
    private static final String MODE_TAG = "wand_mode";

    public static final int COOLDOWN_TICKS = 10;

    public enum WandMode {
        RESET_MODE("reset"),
        TRACKING_BLOCK_MODE("tracking_block"),
        TRACKING_VILLAGER_MODE("tracking_villager"),
        REFRESH_MODE("refresh"),
        UPGRADE_MODE("upgrade"),
        SELECT_MODE("select");

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

    private static final Set<WandMode> ENTITY_MODES = Set.of(
            WandMode.RESET_MODE,
            WandMode.REFRESH_MODE,
            WandMode.TRACKING_BLOCK_MODE,
            WandMode.UPGRADE_MODE,
            WandMode.SELECT_MODE
    );

    private static final Set<WandMode> BLOCK_MODES = Set.of(
            WandMode.TRACKING_VILLAGER_MODE
    );


    public void switchMode(ItemStack stack, Player player, boolean forward) {
        if (player.level().isClientSide) {
            NetworkHandler.sendWandModeSwitchToServer(new PacketWandModeSwitch(forward));
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
            case RESET_MODE -> HandlerVillagerReset.handle(player, villager);
            case REFRESH_MODE -> HandlerVillagerRefresh.handle(player, villager);
            case TRACKING_BLOCK_MODE -> HandlerTrackBlock.handle(player, villager);
            case UPGRADE_MODE -> HandlerUpgradeVillager.handle(player, villager);
            case SELECT_MODE -> HandlerTradeSelector.handle(player, villager);
            default -> InteractionResult.FAIL;
        };
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
            return HandlerTrackVillager.handle(player, pos, (ServerLevel) world);
        }
        return InteractionResult.PASS;
    }
}
