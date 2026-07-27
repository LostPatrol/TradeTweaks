package net.lostpatrol.tradetweaks.network;

import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.lostpatrol.tradetweaks.network.handler.HandlerOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.handler.HandlerTradeReplace;
import net.lostpatrol.tradetweaks.network.handler.HandlerWandModeSet;
import net.lostpatrol.tradetweaks.network.handler.HandlerWandModeSwitch;
import net.lostpatrol.tradetweaks.network.packet.PacketBlockHighlight;
import net.lostpatrol.tradetweaks.network.packet.PacketItemActivation;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSet;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSwitch;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "3";

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(NetworkHandler::registerPayloads);
    }

    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToServer(PacketWandModeSwitch.TYPE, PacketWandModeSwitch.STREAM_CODEC, HandlerWandModeSwitch::handle);
        registrar.playToClient(PacketBlockHighlight.TYPE, PacketBlockHighlight.STREAM_CODEC, HandlerBlockHighlight::handle);
        registrar.playToServer(PacketTradeReplace.TYPE, PacketTradeReplace.STREAM_CODEC, HandlerTradeReplace::handle);
        registrar.playToClient(PacketOpenTradeSelection.TYPE, PacketOpenTradeSelection.STREAM_CODEC, HandlerOpenTradeSelection::handle);
        registrar.playToClient(PacketItemActivation.TYPE, PacketItemActivation.STREAM_CODEC, PacketItemActivation::handle);
        registrar.playToServer(PacketWandModeSet.TYPE, PacketWandModeSet.STREAM_CODEC, HandlerWandModeSet::handle);
    }

    public static void sendBlockHighlightToPlayer(ServerPlayer player, PacketBlockHighlight packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendWandModeSwitchToServer(PacketWandModeSwitch packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendWandModeSetToServer(PacketWandModeSet packet){
        PacketDistributor.sendToServer(packet);
    }

    public static void sendOpenTradeSelectionToPlayer(ServerPlayer player, PacketOpenTradeSelection packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendTradeReplaceToServer(PacketTradeReplace packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendItemActivationToPlayer(ServerPlayer player, PacketItemActivation packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}
