package net.elaris.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.elaris.LevelData;
import net.elaris.PlayerClassData;
import net.elaris.PlayerData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class ResetLevelCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("elarisreset")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(
                                CommandManager.argument("target", StringArgumentType.word())
                                        .executes(context -> reset(context, StringArgumentType.getString(context, "target")))
                        )
                        .then(
                                CommandManager.argument("players", EntityArgumentType.players())
                                        .executes(context -> resetMultiple(context, EntityArgumentType.getPlayers(context, "players")))
                        )
        );
    }

    private static int reset(CommandContext<ServerCommandSource> context, String target) {
        ServerCommandSource source = context.getSource();

        if (target.equalsIgnoreCase("all")) {
            Collection<? extends PlayerEntity> players = source.getServer().getPlayerManager().getPlayerList();
            for (PlayerEntity player : players) {
                PlayerClassData classData = PlayerData.get(player).getClassData();
                classData.reset();

                LevelData levelData = PlayerData.get(player).getLevelData();
                levelData.reset(player);
            }

            source.sendFeedback(
                    () -> Text.literal("[Elaris RPG] All players' levels have been reset."),
                    false
            );
            return 1;
        } else {
            PlayerEntity player = source.getServer().getPlayerManager().getPlayer(target);
            if (player == null) {
                source.sendError(Text.literal("[Elaris RPG] Player not found: " + target));
                return 0;
            }

            PlayerClassData classData = PlayerData.get(player).getClassData();
            classData.reset();

            LevelData levelData = PlayerData.get(player).getLevelData();
            levelData.reset(player);

            source.sendFeedback(
                    () -> Text.literal("[Elaris RPG] Reset levels for " + player.getEntityName()),
                    false
            );
            return 1;
        }
    }

    private static int resetMultiple(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> players) {
        ServerCommandSource source = context.getSource();

        for (PlayerEntity player : players) {
            PlayerClassData classData = PlayerData.get(player).getClassData();
            classData.reset();

            LevelData levelData = PlayerData.get(player).getLevelData();
            levelData.reset(player);
        }

        source.sendFeedback(
                () -> Text.literal("[Elaris RPG] Reset levels for " + players.size() + " players."),
                false
        );

        return 1;
    }
}
