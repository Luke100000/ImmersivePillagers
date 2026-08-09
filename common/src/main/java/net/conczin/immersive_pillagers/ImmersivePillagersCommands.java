package net.conczin.immersive_pillagers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ImmersivePillagersCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(ImmersivePillagers.MOD_ID)
                .then(Commands.literal("list")
                        .requires(p -> p.hasPermission(2))
                        .executes(c -> PillagerManager.listHordes(c.getSource())))
                .then(Commands.literal("clear")
                        .requires(p -> p.hasPermission(2))
                        .executes(c -> PillagerManager.clearHordes(c.getSource())))
                .then(Commands.literal("summon")
                        .requires(p -> p.hasPermission(2))
                        .then(Commands.argument("wave", StringArgumentType.word())
                                .executes(c -> ImmersivePillagersCommands.summon(c, c.getArgument("wave", String.class))
                                )
                        )
                )
        );
    }

    private static int summon(CommandContext<CommandSourceStack> context, String wave) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return PillagerManager.spawnHorde(context.getSource(), wave);
    }

    private static void sendMessage(CommandContext<CommandSourceStack> context, String message) {
        sendMessage(context, Component.literal(message));
    }

    private static void sendMessage(CommandContext<CommandSourceStack> context, Component message) {
        context.getSource().sendSuccess(() -> message, false);
    }
}
