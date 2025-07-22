package net.elarisrpg.quest;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class TalkToQuest implements Quest {
    private final Identifier id;
    private final Identifier targetNpc;

    public TalkToQuest(Identifier id, Identifier targetNpc) {
        this.id = id;
        this.targetNpc = targetNpc;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public boolean isComplete(ServerPlayerEntity player) {
        return QuestManager.get(player).hasTalkedToNpc(targetNpc);
    }

    @Override
    public void onTalkTo(ServerPlayerEntity player, Identifier npcId) {
        if (npcId.equals(targetNpc)) {
            QuestManager.get(player).markNpcTalkedTo(npcId);
        }
    }
}
