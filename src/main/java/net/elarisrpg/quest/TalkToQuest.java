package net.elarisrpg.quest;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TalkToQuest extends Quest {
    private final Identifier targetNpc;

    public TalkToQuest(Identifier id, String title, Identifier targetNpc, boolean repeatable) {
        super(id, title, repeatable);
        this.targetNpc = targetNpc;
    }

    @Override
    public boolean isComplete(ServerPlayerEntity player) {
        return QuestManager.get(player).hasTalkedToNpc(targetNpc);
    }

    public void onTalkTo(ServerPlayerEntity player, Identifier npcId) {
        if (npcId.equals(targetNpc)) {
            QuestManager.get(player).markNpcTalkedTo(npcId);
        }
    }
}
