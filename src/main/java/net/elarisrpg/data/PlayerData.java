package net.elarisrpg.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData implements NbtSerializable {

    private static final HashMap<UUID, PlayerData> dataMap = new HashMap<>();

    private final LevelData levelData = new LevelData();
    private final PlayerClassData classData = new PlayerClassData();

    public LevelData getLevelData() {
        return levelData;
    }

    public PlayerClassData getClassData() {
        return classData;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound levelNbt = new NbtCompound();
        levelData.writeNbt(levelNbt);
        nbt.put("ElarisLevelData", levelNbt);

        NbtCompound classNbt = new NbtCompound();
        classData.writeNbt(classNbt);
        nbt.put("ElarisClassData", classNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("ElarisLevelData")) {
            levelData.readNbt(nbt.getCompound("ElarisLevelData"));
        }
        if (nbt.contains("ElarisClassData")) {
            classData.readNbt(nbt.getCompound("ElarisClassData"));
        }
    }

    public static PlayerData get(@NotNull PlayerEntity player) {
        return dataMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }

    public static void set(@NotNull PlayerEntity player, PlayerData data) {
        dataMap.put(player.getUuid(), data);
    }

    public static void remove(@NotNull PlayerEntity player) {
        dataMap.remove(player.getUuid());
    }
}
