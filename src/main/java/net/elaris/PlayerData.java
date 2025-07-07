package net.elaris;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {

    private static final HashMap<UUID, LevelInfo> levels = new HashMap<>();

    public static LevelInfo get(@NotNull PlayerEntity player) {
        return levels.computeIfAbsent(player.getUuid(), id -> new LevelInfo());
    }

    public static void set(@NotNull PlayerEntity player, LevelInfo info) {
        levels.put(player.getUuid(), info);
    }

    public static void remove(@NotNull PlayerEntity player) {
        levels.remove(player.getUuid());
    }

}
