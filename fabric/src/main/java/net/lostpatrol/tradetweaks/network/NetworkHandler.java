/** Sends the seven existing FriendlyByteBuf packets through Fabric 1.20.1 channels. */
package net.lostpatrol.tradetweaks.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.lostpatrol.tradetweaks.network.handler.*;
import net.lostpatrol.tradetweaks.network.packet.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class NetworkHandler {
    /** Cyclic wand mode requests. */
    public static final ResourceLocation MODE_SWITCH = id("wand_mode_switch");
    /** Explicit wand wheel selections. */
    public static final ResourceLocation MODE_SET = id("wand_mode_set");
    /** Server-computed workstation highlights. */
    public static final ResourceLocation HIGHLIGHT = id("block_highlight");
    /** Client requests to replace a selected trade. */
    public static final ResourceLocation TRADE_REPLACE = id("trade_replace");
    /** Server-provided candidate trade lists. */
    public static final ResourceLocation TRADE_OPEN = id("open_trade_selection");
    /** Client session-release messages. */
    public static final ResourceLocation TRADE_CLOSE = id("close_trade_selection");
    /** Hero totem activation animations. */
    public static final ResourceLocation ITEM_ACTIVATION = id("item_activation");

    /** Creates a channel identifier in the mod namespace. */
    private static ResourceLocation id(String path) { return new ResourceLocation("tradetweaks", path); }

    /** Registers the four server-bound packets before players connect. */
    public static void register() {
        receive(MODE_SWITCH, PacketWandModeSwitch::new, HandlerWandModeSwitch::handle);
        receive(MODE_SET, PacketWandModeSet::new, HandlerWandModeSet::handle);
        receive(TRADE_REPLACE, PacketTradeReplace::new, HandlerTradeReplace::handle);
        receive(TRADE_CLOSE, PacketCloseTradeSelection::new, HandlerCloseTradeSelection::handle);
    }

    /** Decodes before Fabric releases the buffer, then schedules the shared handler. */
    private static <T> void receive(ResourceLocation id, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, PayloadContext> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, listener, buffer, response) -> {
            T packet = decoder.apply(buffer);
            handler.accept(packet, new PayloadContext(player, server::execute));
        });
    }

    /** Serializes a server packet using the existing shared codec. */
    private static <T> void send(ServerPlayer player, ResourceLocation id, T packet, BiConsumer<T, FriendlyByteBuf> encoder) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        encoder.accept(packet, buffer);
        ServerPlayNetworking.send(player, id, buffer);
    }

    /** Serializes a client packet using the existing shared codec. */
    private static <T> void send(ResourceLocation id, T packet, BiConsumer<T, FriendlyByteBuf> encoder) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        encoder.accept(packet, buffer);
        ClientPlayNetworking.send(id, buffer);
    }

    /** Sends a workstation outline to the requesting player. */
    public static void sendBlockHighlightToPlayer(ServerPlayer player, PacketBlockHighlight packet) { send(player, HIGHLIGHT, packet, PacketBlockHighlight::encode); }
    /** Requests a cyclic wand mode change. */
    public static void sendWandModeSwitchToServer(PacketWandModeSwitch packet) { send(MODE_SWITCH, packet, PacketWandModeSwitch::encode); }
    /** Requests the selected wheel mode. */
    public static void sendWandModeSetToServer(PacketWandModeSet packet) { send(MODE_SET, packet, PacketWandModeSet::encode); }
    /** Opens the authenticated server trade-selection session. */
    public static void sendOpenTradeSelectionToPlayer(ServerPlayer player, PacketOpenTradeSelection packet) { send(player, TRADE_OPEN, packet, PacketOpenTradeSelection::encode); }
    /** Requests a candidate replacement from the current session. */
    public static void sendTradeReplaceToServer(PacketTradeReplace packet) { send(TRADE_REPLACE, packet, PacketTradeReplace::encode); }
    /** Releases the player's trade-selection lock. */
    public static void sendCloseTradeSelectionToServer(PacketCloseTradeSelection packet) { send(TRADE_CLOSE, packet, PacketCloseTradeSelection::encode); }
    /** Plays the hero-totem activation animation. */
    public static void sendItemActivationToPlayer(ServerPlayer player, PacketItemActivation packet) { send(player, ITEM_ACTIVATION, packet, PacketItemActivation::encode); }
}
