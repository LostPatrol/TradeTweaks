/** Registers internal commands used by clickable trade reports. */
package net.lostpatrol.tradetweaks.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class InternalCommand {
    /** Registers the shared command tree. */
    public static void registerCommands(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("tradetweaks_internal")
//                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("highlight")
                                .then(Commands.argument("UUID", StringArgumentType.string())
                                        .then(Commands.argument("duration", IntegerArgumentType.integer(20, 10000))
                                                .executes(ctx -> highlightVillager(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "UUID"),
                                                        IntegerArgumentType.getInteger(ctx, "duration")
                                                )))))
        );
    }

    private static int highlightVillager(CommandSourceStack source, String villagerID, int durationTicks) {
        ServerLevel level = source.getLevel();
        String effectCommand = String.format(
                "effect give %s minecraft:glowing %d",
                villagerID,
                durationTicks / 20
        );
        int result = level.getServer().getCommands().performPrefixedCommand(
//                source.withPermission(4).withSuppressedOutput(),
                source.withPermission(4),
                effectCommand
        );

//        if (result > 0) {
//            source.sendSuccess(() ->
//                    Component.translatable("tradetweaks.tradecast.highlight.success", false),
//                    true
//            );
//        }
        return result;
    }
}
