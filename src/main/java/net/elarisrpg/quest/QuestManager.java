package net.elarisrpg.quest;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestManager {
    private static final Map<UUID, PlayerQuestData> playerData = new HashMap<>();

    // Always present during play
    public static PlayerQuestData get(ServerPlayerEntity player) {
        return playerData.computeIfAbsent(player.getUuid(), uuid -> new PlayerQuestData());
    }

    // Called on logout to clean up memory
    public static void remove(ServerPlayerEntity player) {
        playerData.remove(player.getUuid());
    }
}
