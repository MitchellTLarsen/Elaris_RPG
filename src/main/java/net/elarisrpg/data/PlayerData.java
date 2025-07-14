package net.elarisrpg.data;

import net.elarisrpg.classes.classskill.Skill;
import net.elarisrpg.classes.classskill.SkillRow;
import net.elarisrpg.classes.classskill.SkillTree;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData implements NbtSerializable {

    private static final HashMap<UUID, PlayerData> dataMap = new HashMap<>();

    private final LevelData levelData = new LevelData();
    private final PlayerClassData classData = new PlayerClassData();
    private final SkillTree skillTree;

    public PlayerData() {
        this.skillTree = createDefaultSkillTree();
    }

    public LevelData getLevelData() {
        return levelData;
    }

    public PlayerClassData getClassData() {
        return classData;
    }

    public SkillTree getSkillTree() {
        return skillTree;
    }

    public int getTotalSkillPoints() {
        return levelData.getSkillPoints();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound levelNbt = new NbtCompound();
        levelData.writeNbt(levelNbt);
        nbt.put("ElarisLevelData", levelNbt);

        NbtCompound classNbt = new NbtCompound();
        classData.writeNbt(classNbt);
        nbt.put("ElarisClassData", classNbt);

        NbtCompound skillTreeNbt = skillTree.writeNbt();
        nbt.put("ElarisSkillTree", skillTreeNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("ElarisLevelData")) {
            levelData.readNbt(nbt.getCompound("ElarisLevelData"));
        }
        if (nbt.contains("ElarisClassData")) {
            classData.readNbt(nbt.getCompound("ElarisClassData"));
        }
        if (nbt.contains("ElarisSkillTree")) {
            skillTree.readNbt(nbt.getCompound("ElarisSkillTree"));
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

    private SkillTree createDefaultSkillTree() {
        var rows = new java.util.ArrayList<SkillRow>();

        for (int r = 1; r <= 4; r++) {
            var skills = new java.util.ArrayList<Skill>();
            for (int s = 1; s <= 5; s++) {
                skills.add(new Skill(
                        "Skill " + s,
                        "Description for skill " + s + " in row " + r,
                        5
                ));
            }
            rows.add(new SkillRow(skills));
        }
        return new SkillTree(rows);
    }
}
