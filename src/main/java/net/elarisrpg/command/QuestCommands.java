package net.elarisrpg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.elarisrpg.quest.PlayerQuestData;
import net.elarisrpg.quest.QuestManager;
import net.elarisrpg.quest.QuestRegistry;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class QuestCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("quest")
                        .then(literal("give")
                                .then(argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                                        .then(argument("quest", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = getPlayer(ctx);
                                                    Identifier id = new Identifier("elarisrpg", StringArgumentType.getString(ctx, "quest"));

                                                    if (QuestRegistry.getAll().stream().noneMatch(q -> q.getId().equals(id))) {
                                                        ctx.getSource().sendFeedback(() -> Text.literal("§cQuest not found: " + id), false);
                                                        return 0;
                                                    }

                                                    PlayerQuestData data = QuestManager.get(player);
                                                    data.startQuest(id);
                                                    ctx.getSource().sendFeedback(() ->
                                                            Text.literal("§aGave quest " + id + " to " + player.getName().getString()), false);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(literal("remove")
                                .then(argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                                        .then(argument("quest", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = getPlayer(ctx);
                                                    Identifier id = new Identifier("elarisrpg", StringArgumentType.getString(ctx, "quest"));

                                                    QuestManager.get(player).removeQuest(id);
                                                    ctx.getSource().sendFeedback(() ->
                                                            Text.literal("§eRemoved quest " + id + " from " + player.getName().getString()), false);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(literal("reset")
                                .then(argument("player", net.minecraft.command.argument.EntityArgumentType.player())
                                        .then(argument("quest", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    ServerPlayerEntity player = getPlayer(ctx);
                                                    Identifier id = new Identifier("elarisrpg", StringArgumentType.getString(ctx, "quest"));

                                                    QuestManager.get(player).resetQuest(id);
                                                    ctx.getSource().sendFeedback(() ->
                                                            Text.literal("§6Reset quest " + id + " for " + player.getName().getString()), false);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(literal("list")
                                .then(argument("player", EntityArgumentType.player())
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = getPlayer(ctx);
                                            PlayerQuestData data = QuestManager.get(player);

                                            String active = data.getActiveQuests().stream()
                                                    .map(Identifier::toString)
                                                    .collect(Collectors.joining(", "));
                                            String completed = data.getCompletedQuests().stream()
                                                    .map(Identifier::toString)
                                                    .collect(Collectors.joining(", "));

                                            ctx.getSource().sendFeedback(
                                                    () -> Text.literal("§bActive quests for " + player.getName().getString() + ": " +
                                                            (active.isEmpty() ? "§7<none>" : active)),
                                                    false);

                                            ctx.getSource().sendFeedback(
                                                    () -> Text.literal("§aCompleted quests: " +
                                                            (completed.isEmpty() ? "§7<none>" : completed)),
                                                    false);

                                            return 1;
                                        })
                                )
                        )
        );
    }

    private static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return net.minecraft.command.argument.EntityArgumentType.getPlayer(ctx, "player");
    }
}
