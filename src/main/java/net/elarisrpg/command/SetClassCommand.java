package net.elarisrpg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.elarisrpg.data.PlayerData;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class SetClassCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("elarisSetClass")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(
                                CommandManager.argument("players", EntityArgumentType.players())
                                        .then(
                                                CommandManager.argument("className", StringArgumentType.string())
                                                        .executes(context -> {
                                                            Collection<ServerPlayerEntity> players =
                                                                    EntityArgumentType.getPlayers(context, "players");
                                                            String className = StringArgumentType.getString(context, "className");

                                                            for (PlayerEntity player : players) {
                                                                PlayerData data = PlayerData.get(player);
                                                                data.getClassData().setPlayerClass(className);
                                                                player.sendMessage(
                                                                        Text.literal("[Elaris] Your class is now " + className),
                                                                        false
                                                                );
                                                            }

                                                            context.getSource().sendFeedback(
                                                                    () -> Text.literal("[Elaris] Set class for " + players.size() + " players."),
                                                                    false
                                                            );
                                                            return 1;
                                                        })
                                        )
                        )
        );
    }
}
