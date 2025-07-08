package net.elaris.command;

import com.mojang.brigadier.CommandDispatcher;
import net.elaris.PlayerData;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ResetClassCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("elarisresetclass")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();

                            if (source.getEntity() instanceof ServerPlayerEntity player) {
                                PlayerData.get(player).getClassData().reset();

                                PlayerData.set(player, PlayerData.get(player));

                                player.sendMessage(
                                        Text.literal("[Elaris RPG] Your class has been reset."),
                                        false
                                );
                                return 1;
                            } else {
                                source.sendError(Text.literal("This command can only be run by a player."));
                                return 0;
                            }
                        })
        );
    }
}
