package net.lostpatrol.tradetweaks.network;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.lostpatrol.tradetweaks.network.handler.HandlerBlockHighlight;
import net.lostpatrol.tradetweaks.network.handler.HandlerOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.handler.HandlerTradeReplace;
import net.lostpatrol.tradetweaks.network.handler.HandlerWandModeSwitch;
import net.lostpatrol.tradetweaks.network.packet.PacketBlockHighlight;
import net.lostpatrol.tradetweaks.network.packet.PacketOpenTradeSelection;
import net.lostpatrol.tradetweaks.network.packet.PacketTradeReplace;
import net.lostpatrol.tradetweaks.network.packet.PacketWandModeSwitch;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static SimpleChannel INSTANCE;

    private static final String PROTOCOL_VERSION = "1.0";

    public static SimpleChannel getChannel() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Channel not initialized. Call register() first!");
        }
        return INSTANCE;
    }

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
                HandlerWandModeSwitch::handle
        );
        INSTANCE.registerMessage(
                id++,
                PacketBlockHighlight.class,
                PacketBlockHighlight::encode,
                PacketBlockHighlight::new,
                HandlerBlockHighlight::handle
        );
        INSTANCE.registerMessage(
                id++,
                PacketTradeReplace.class,
                PacketTradeReplace::encode,
                PacketTradeReplace::new,
                HandlerTradeReplace::handle
        );
        INSTANCE.registerMessage(
                id++,
                PacketOpenTradeSelection.class,
                PacketOpenTradeSelection::encode,
                PacketOpenTradeSelection::new,
                HandlerOpenTradeSelection::handle
        );
    }

    public static void sendBlockHighlightToPlayer(ServerPlayer player, PacketBlockHighlight packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendWandModeSwitchToServer(PacketWandModeSwitch packet){
        getChannel().sendToServer(packet);
    }

    public static void sendOpenTradeSelectionToPlayer(ServerPlayer player, PacketOpenTradeSelection packet){
        INSTANCE.send(PacketDistributor.PLAYER.with(()->player), packet);
    }

    public static void sendTradeReplaceToServer(PacketTradeReplace packet){
        getChannel().sendToServer(packet);
    }

}

