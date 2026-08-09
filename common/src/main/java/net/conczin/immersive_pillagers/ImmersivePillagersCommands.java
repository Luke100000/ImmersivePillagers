package net.conczin.immersive_pillagers;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.conczin.immersive_pillagers.hordes.ActiveHorde;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ImmersivePillagersCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(ImmersivePillagers.MOD_ID)
                .then(Commands.literal("list")
                        .requires(p -> p.hasPermission(2))
                        .executes(c -> listHordes(c.getSource())))
                .then(Commands.literal("clear")
                        .requires(p -> p.hasPermission(2))
                        .executes(c -> clearHordes(c.getSource())))
                .then(Commands.literal("summon")
                        .requires(p -> p.hasPermission(2))
                        .then(Commands.argument("wave", StringArgumentType.word())
                                .suggests(ImmersivePillagersCommands::suggestHordes)
                                .executes(c -> ImmersivePillagersCommands.summon(c, c.getArgument("wave", String.class))
                                )
                        )
                )
        );
    }

    private static int summon(CommandContext<CommandSourceStack> context, String wave) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        if (!(player.level() instanceof ServerLevel level)) {
            source.sendFailure(Component.translatable("command.immersive_pillagers.summon.server_only"));
            return 0;
        }

        if (!PillagerManager.isHordeRegistered(wave)) {
            source.sendFailure(Component.translatable("command.immersive_pillagers.summon.unknown_horde", wave, availableHordes()));
            return 0;
        }

        Optional<ActiveHorde> activeHorde = PillagerManager.spawnHorde(wave, level, player.blockPosition());
        if (activeHorde.isEmpty()) {
            source.sendFailure(Component.translatable("command.immersive_pillagers.summon.no_safe_position", wave));
            return 0;
        }

        ActiveHorde spawned = activeHorde.get();
        PillagerManager.addActiveHorde(spawned);
        source.sendSuccess(() -> Component.translatable("command.immersive_pillagers.summon.success", spawned.type(), spawned.memberCount()), true);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestHordes(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(PillagerManager.getHordeNames(), builder);
    }

    private static int listHordes(CommandSourceStack source) {
        List<ActiveHorde> hordes = PillagerManager.getActiveHordes();
        if (hordes.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("command.immersive_pillagers.list.empty"), false);
            return 0;
        }

        String activeHordes = hordes.stream()
                .map(horde -> horde.type() + " " + horde.id().toString().substring(0, 8) + " (" + horde.memberCount() + " members)")
                .collect(Collectors.joining(", "));
        source.sendSuccess(() -> Component.translatable("command.immersive_pillagers.list.success", activeHordes), false);
        return hordes.size();
    }

    private static int clearHordes(CommandSourceStack source) {
        int clearedHordes = PillagerManager.clearHordes();
        source.sendSuccess(() -> Component.translatable("command.immersive_pillagers.clear.success", clearedHordes), true);
        return clearedHordes;
    }

    private static String availableHordes() {
        return String.join(", ", PillagerManager.getHordeNames());
    }
}
