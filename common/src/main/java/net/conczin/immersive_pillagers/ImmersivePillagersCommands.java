package net.conczin.immersive_pillagers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ImmersivePillagersCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(ImmersivePillagers.MOD_ID)
                .then(Commands.literal("help")
                        .executes(ImmersivePillagersCommands::displayHelp))
                .then(Commands.literal("summon")
                        .requires(p -> p.hasPermission(2))
                        .then(Commands.argument("wave", StringArgumentType.string())
                                .executes(c -> ImmersivePillagersCommands.summon(c, c.getArgument("wave", String.class))
                                )
                        )
                )
        );
    }

    private static int displayHelp(CommandContext<CommandSourceStack> context) {
        sendMessage(context, "Debug commands");
        return 0;
    }

    private static int summon(CommandContext<CommandSourceStack> context, String wave) {
        sendMessage(context, "Summoning " + wave);
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            PillagerManager.spawnHorde(player, wave);
        }
        return 0;
    }

    private static void sendMessage(CommandContext<CommandSourceStack> context, String message) {
        sendMessage(context, Component.literal(message));
    }

    private static void sendMessage(CommandContext<CommandSourceStack> context, Component message) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            player.sendSystemMessage(message);
        }
    }
}
