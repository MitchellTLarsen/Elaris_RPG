package net.elarisrpg.quest;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface Quest {
    Identifier getId();
    boolean isComplete(ServerPlayerEntity player);
    void onTalkTo(ServerPlayerEntity player, Identifier npcId);
}
