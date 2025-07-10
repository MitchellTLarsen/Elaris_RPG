package net.elarisrpg.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class LevelData {

    private int level = 1;
    private int xp = 0;
    private int skillPoints = 0;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    /**
     * How much XP to reach the next level.
     */
    public int xpToNextLevel() {
        return level * 100;
    }

    /**
     * Add XP and handle level-ups.
     */
    public void addXp(PlayerEntity player, int amount) {
        this.xp += amount;

        while (xp >= xpToNextLevel()) {
            xp -= xpToNextLevel();
            level++;
            skillPoints += 1;
        }
    }

    /**
     * Resets this LevelData to defaults.
     */
    public void reset(PlayerEntity player) {
        this.level = 1;
        this.xp = 0;
        this.skillPoints = 0;
    }

    /**
     * Write this LevelData to NBT.
     */
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("Level", level);
        nbt.putInt("XP", xp);
        nbt.putInt("SkillPoints", skillPoints);
    }

    /**
     * Read this LevelData from NBT.
     */
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("Level")) {
            this.level = nbt.getInt("Level");
        }
        if (nbt.contains("XP")) {
            this.xp = nbt.getInt("XP");
        }
        if (nbt.contains("SkillPoints")) {
            this.skillPoints = nbt.getInt("SkillPoints");
        }
    }
}
