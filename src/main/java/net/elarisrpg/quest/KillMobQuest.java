package net.elarisrpg.quest;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class KillMobQuest extends Quest {
    private final Identifier targetMob;
    private final int requiredKills;

    public KillMobQuest(Identifier id, String title, Identifier targetMob, int requiredKills, boolean repeatable) {
        super(id, title, repeatable);
        this.targetMob = targetMob;
        this.requiredKills = requiredKills;
    }

    @Override
    public boolean isComplete(ServerPlayerEntity player) {
        return QuestManager.get(player).getKills(targetMob) >= requiredKills;
    }

    public void onMobKilled(ServerPlayerEntity player, Identifier mobId) {
        if (mobId.equals(targetMob)) {
            QuestManager.get(player).incrementKill(mobId);
        }
    }
}
