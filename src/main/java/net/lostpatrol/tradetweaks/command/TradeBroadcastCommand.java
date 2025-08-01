package net.lostpatrol.tradetweaks.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.lostpatrol.tradetweaks.common.tradecast.VillagerTradeReporter;
import net.lostpatrol.tradetweaks.config.ClientConfig;
import net.lostpatrol.tradetweaks.config.ServerConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;


// modify the config file only after command executed successfully
public class TradeBroadcastCommand {
    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(TradeBroadcastCommand::registerCommands);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("tradecast")
                        .executes(ctx -> showSettings(ctx.getSource()))

                        .then(Commands.literal("mode")
                                .then(Commands.literal("off")
                                        .executes(ctx -> setPlayerMode(ctx.getSource(), ClientConfig.ReportMode.OFF)))
                                .then(Commands.literal("librarian")
                                        .executes(ctx -> setPlayerMode(ctx.getSource(), ClientConfig.ReportMode.LIBRARIAN_ONLY)))
                                .then(Commands.literal("all")
                                        .executes(ctx -> setPlayerMode(ctx.getSource(), ClientConfig.ReportMode.ALL_VILLAGERS))))

                        .then(Commands.literal("time")
                                .requires(source -> source.hasPermission(2)) // OP
                                .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 10))
                                        .executes(ctx -> setServerInterval(
                                                ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "seconds")))))

                        .then(Commands.literal("radius")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("blocks", IntegerArgumentType.integer(1, 32))
                                        .executes((ctx -> setServerRadius(
                                                ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "blocks"))))))

                        .then(Commands.literal("refresh")
                                .executes(ctx -> refresh(ctx.getSource())))

                        .then(Commands.literal("render")
                                .then(Commands.argument("flag", BoolArgumentType.bool())
                                        .executes((ctx -> setRenderMode(
                                                ctx.getSource(),
                                                BoolArgumentType.getBool(ctx, "flag")
                                        )))))
        );
    }

    private static int showSettings(CommandSourceStack source) {
        MutableComponent message = Component.translatable("tradetweaks.tradecast.settings.header").append("\n");
        if (source.getPlayer() != null) {
            String modeKey = "tradetweaks.tradecast.mode." + ClientConfig.getMode().toString().toLowerCase();
            message.append(Component.translatable("tradetweaks.tradecast.settings.mode",
                    Component.translatable(modeKey)).append("\n"));
            message.append((Component.translatable("tradetweaks.tradecast.settings.render",
                    String.valueOf(ClientConfig.getRenderMode())).append("\n")));
        }
        message.append(Component.translatable("tradetweaks.tradecast.settings.interval",
                ServerConfig.getCheckInterval())).append("\n");
        message.append(Component.translatable("tradetweaks.tradecast.settings.radius",
                ServerConfig.getCheckRadius()));
        source.sendSuccess(() -> message, false);
        return 1;
    }

    private static int setServerInterval(CommandSourceStack source, int seconds) {
        ServerConfig.setCheckInterval(seconds);
        source.sendSuccess(() ->
                Component.translatable("tradetweaks.tradecast.interval.set", seconds), false);
        ServerConfig.tempIntervalTicks = seconds * 20;
        return 1;
    }

    private static int setServerRadius(CommandSourceStack source, int blocks) {
        ServerConfig.setCheckRadius(blocks);
        source.sendSuccess(() ->
                Component.translatable("tradetweaks.tradecast.radius.set", blocks), false);
        ServerConfig.tempRadiusBlocks = blocks;
        return 1;
    }

    private static int setPlayerMode(CommandSourceStack source, ClientConfig.ReportMode mode) {
        if (source.getPlayer()!=null) {
            ClientConfig.setMode(mode);
            ClientConfig.tempMode = mode;
            String modeKey = "tradetweaks.tradecast.mode." + mode.toString().toLowerCase();
            source.sendSuccess(() ->
                    Component.translatable("tradetweaks.tradecast.mode.set",
                            Component.translatable(modeKey)), false);
        } else {
            source.sendFailure(Component.translatable("tradetweaks.tradecast.mode.error"));
        }
        return 1;
    }

    private static int refresh(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.translatable("tradetweaks.tradecast.refresh.fail.not_player"));
            return 0;
        }
        VillagerTradeReporter.PlayerTradeData playerData = VillagerTradeReporter.playerTradeDataMap.get(player.getUUID());
        if (playerData == null) {
            source.sendFailure(Component.translatable("tradetweaks.tradecast.refresh.fail.no_data"));
            return 0;
        }
        playerData.reportedVillagers.clear();
        playerData.lastKnownOffers.clear();
        source.sendSuccess(() ->
                Component.translatable("tradetweaks.tradecast.refresh.success"), false);
        return 1;
    }

    private static int setRenderMode(CommandSourceStack source, boolean flag) {
        if (source.getPlayer()!=null) {
            ClientConfig.setRenderMode(flag);
            ClientConfig.tempRenderFlag = flag;
            source.sendSuccess(() ->
                            Component.translatable("tradetweaks.tradecast.render.set", String.valueOf(flag))
                    , false);
        } else {
            source.sendFailure(Component.translatable("tradetweaks.tradecast.render.error"));
        }

        return 1;
    }
}
