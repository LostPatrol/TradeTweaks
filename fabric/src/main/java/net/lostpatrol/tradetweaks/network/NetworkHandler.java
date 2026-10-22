/** Registers the seven shared payload codecs and Fabric server receivers. */
package net.lostpatrol.tradetweaks.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.lostpatrol.tradetweaks.network.handler.*;
import net.lostpatrol.tradetweaks.network.packet.*;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkHandler {
    /** Registers both codec directions before clients can connect. */
    public static void register() {
        PayloadTypeRegistry.playC2S().register(PacketWandModeSwitch.TYPE, PacketWandModeSwitch.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PacketWandModeSet.TYPE, PacketWandModeSet.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PacketTradeReplace.TYPE, PacketTradeReplace.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PacketCloseTradeSelection.TYPE, PacketCloseTradeSelection.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PacketBlockHighlight.TYPE, PacketBlockHighlight.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PacketOpenTradeSelection.TYPE, PacketOpenTradeSelection.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PacketItemActivation.TYPE, PacketItemActivation.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PacketWandModeSwitch.TYPE, (packet, ctx) -> HandlerWandModeSwitch.handle(packet, new PayloadContext(ctx.player(), ctx.server()::execute)));
        ServerPlayNetworking.registerGlobalReceiver(PacketWandModeSet.TYPE, (packet, ctx) -> HandlerWandModeSet.handle(packet, new PayloadContext(ctx.player(), ctx.server()::execute)));
        ServerPlayNetworking.registerGlobalReceiver(PacketTradeReplace.TYPE, (packet, ctx) -> HandlerTradeReplace.handle(packet, new PayloadContext(ctx.player(), ctx.server()::execute)));
        ServerPlayNetworking.registerGlobalReceiver(PacketCloseTradeSelection.TYPE, (packet, ctx) -> HandlerCloseTradeSelection.handle(packet, new PayloadContext(ctx.player(), ctx.server()::execute)));
    }

    /** Sends a workstation outline to its requesting player. */
    public static void sendBlockHighlightToPlayer(ServerPlayer player, PacketBlockHighlight packet) { ServerPlayNetworking.send(player, packet); }
    /** Sends a wand scroll change to the server. */
    public static void sendWandModeSwitchToServer(PacketWandModeSwitch packet) { ClientPlayNetworking.send(packet); }
    /** Sends the explicit wheel selection to the server. */
    public static void sendWandModeSetToServer(PacketWandModeSet packet) { ClientPlayNetworking.send(packet); }
    /** Opens an authenticated trade selection session on the client. */
    public static void sendOpenTradeSelectionToPlayer(ServerPlayer player, PacketOpenTradeSelection packet) { ServerPlayNetworking.send(player, packet); }
    /** Requests a replacement from the current server session. */
    public static void sendTradeReplaceToServer(PacketTradeReplace packet) { ClientPlayNetworking.send(packet); }
    /** Releases the server-owned trade selection session. */
    public static void sendCloseTradeSelectionToServer(PacketCloseTradeSelection packet) { ClientPlayNetworking.send(packet); }
    /** Plays the hero totem item activation effect. */
    public static void sendItemActivationToPlayer(ServerPlayer player, PacketItemActivation packet) { ServerPlayNetworking.send(player, packet); }
}
