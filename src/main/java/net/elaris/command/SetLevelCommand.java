package net.elaris.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.elaris.data.PlayerData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class SetLevelCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("elarisSetLevel")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(
                                CommandManager.argument("players", EntityArgumentType.players())
                                        .then(
                                                CommandManager.argument("level", IntegerArgumentType.integer(1))
                                                        .executes(context -> {
                                                            Collection<ServerPlayerEntity> players =
                                                                    EntityArgumentType.getPlayers(context, "players");
                                                            int level = IntegerArgumentType.getInteger(context, "level");

                                                            for (PlayerEntity player : players) {
                                                                PlayerData data = PlayerData.get(player);
                                                                data.getLevelData().setLevel(level);

                                                                // Calculate and set skill points
                                                                int skillPoints = Math.max(0, level - 1);
                                                                data.getLevelData().setSkillPoints(skillPoints);
                                                                data.getLevelData().setXp(0);

                                                                player.sendMessage(
                                                                        Text.literal("[Elaris] Your level is now " + level +
                                                                                " with " + skillPoints + " skill points."),
                                                                        false
                                                                );
                                                            }

                                                            context.getSource().sendFeedback(
                                                                    () -> Text.literal("[Elaris] Set level for " + players.size() + " players."),
                                                                    false
                                                            );
                                                            return 1;
                                                        })
                                        )
                        )
        );
    }
}
