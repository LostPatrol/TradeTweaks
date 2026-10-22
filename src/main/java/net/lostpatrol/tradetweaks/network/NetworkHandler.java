/** Registers the native Forge packet protocol and send operations. */
package net.lostpatrol.tradetweaks.network;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.lostpatrol.tradetweaks.network.handler.HandlerCloseTradeSelection;
import net.lostpatrol.tradetweaks.network.handler.HandlerOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.handler.HandlerTradeReplace;
import net.lostpatrol.tradetweaks.network.handler.HandlerWandModeSet;
import net.lostpatrol.tradetweaks.network.handler.HandlerWandModeSwitch;
import net.lostpatrol.tradetweaks.network.packet.PacketBlockHighlight;
import net.lostpatrol.tradetweaks.network.packet.PacketCloseTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.PacketItemActivation;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSet;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSwitch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static SimpleChannel INSTANCE;

    private static final String PROTOCOL_VERSION = "4";

    public static SimpleChannel getChannel() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Channel not initialized. Call register() first!");
        }
        return INSTANCE;
    }

    /** Registers the original seven packet IDs in their existing order. */
    public static void register() {
        if (INSTANCE != null) return;
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(TradeTweaks.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        int id = 0;
        INSTANCE.registerMessage(
                id++,
                PacketWandModeSwitch.class,
                PacketWandModeSwitch::encode,
                PacketWandModeSwitch::new,
                handler(HandlerWandModeSwitch::handle, false)
        );
        INSTANCE.registerMessage(
                id++,
                PacketBlockHighlight.class,
                PacketBlockHighlight::encode,
                PacketBlockHighlight::new,
                handler(HandlerBlockHighlight::handle, true)
        );
        INSTANCE.registerMessage(
                id++,
                PacketTradeReplace.class,
                PacketTradeReplace::encode,
                PacketTradeReplace::new,
                handler(HandlerTradeReplace::handle, false)
        );
        INSTANCE.registerMessage(
                id++,
                PacketCloseTradeSelection.class,
                PacketCloseTradeSelection::encode,
                PacketCloseTradeSelection::new,
                handler(HandlerCloseTradeSelection::handle, false)
        );
        INSTANCE.registerMessage(
                id++,
                PacketOpenTradeSelection.class,
                PacketOpenTradeSelection::encode,
                PacketOpenTradeSelection::new,
                handler(HandlerOpenTradeSelection::handle, true)
        );
        INSTANCE.registerMessage(
                id++,
                PacketItemActivation.class,
                PacketItemActivation::encode,
                PacketItemActivation::new,
                handler(PacketItemActivation::handle, true)
        );
        INSTANCE.registerMessage(
                id++,
                PacketWandModeSet.class,
                PacketWandModeSet::encode,
                PacketWandModeSet::new,
                handler(HandlerWandModeSet::handle, false)
        );
    }

    public static void sendBlockHighlightToPlayer(ServerPlayer player, PacketBlockHighlight packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    /** Adapts Forge packet acknowledgement, direction and execution to shared handlers. */
    private static <T> java.util.function.BiConsumer<T, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context>> handler(
            java.util.function.BiConsumer<T, PayloadContext> callback, boolean clientOnly) {
        return (packet, supplier) -> {
            var context = supplier.get();
            if (!clientOnly || context.getDirection().getReceptionSide().isClient()) {
                callback.accept(packet, new PayloadContext(context.getSender(), work -> context.enqueueWork(work)));
            }
            context.setPacketHandled(true);
        };
    }

    public static void sendWandModeSwitchToServer(PacketWandModeSwitch packet){
        getChannel().sendToServer(packet);
    }

    public static void sendWandModeSetToServer(PacketWandModeSet packet){
        getChannel().sendToServer(packet);
    }

    public static void sendOpenTradeSelectionToPlayer(ServerPlayer player, PacketOpenTradeSelection packet){
        INSTANCE.send(PacketDistributor.PLAYER.with(()->player), packet);
    }

    public static void sendTradeReplaceToServer(PacketTradeReplace packet){
        getChannel().sendToServer(packet);
    }

    public static void sendCloseTradeSelectionToServer(PacketCloseTradeSelection packet) {
        getChannel().sendToServer(packet);
    }

    public static void sendItemActivationToPlayer(ServerPlayer player, PacketItemActivation packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

}

