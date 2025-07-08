package net.elaris.classes;

import net.elaris.PlayerClassData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerClassDataManager {
    private static final Map<UUID, net.elaris.PlayerClassData> PLAYER_CLASS_DATA = new HashMap<>();

    public static PlayerClassData get(PlayerEntity player) {
        return PLAYER_CLASS_DATA.computeIfAbsent(player.getUuid(), uuid -> new PlayerClassData());
    }

    public static void save(PlayerEntity player, NbtCompound nbt) {
        get(player).writeNbt(nbt);
    }

    public static void load(PlayerEntity player, NbtCompound nbt) {
        get(player).readNbt(nbt);
    }

    public static void clear(PlayerEntity player) {
        PLAYER_CLASS_DATA.remove(player.getUuid());
    }
}
