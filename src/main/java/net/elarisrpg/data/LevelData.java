package net.elarisrpg.data;

import net.elarisrpg.classes.SpellCoreManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class LevelData implements NbtSerializable {

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

    public int xpToNextLevel() {
        return level * 100;
    }

    public void addXp(PlayerEntity player, int amount) {
        xp += amount;
        while (xp >= xpToNextLevel()) {
            xp -= xpToNextLevel();
            level++;
            skillPoints += 1;
            System.out.println("[XP] Level up! Now level " + level + ", calling SpellCoreManager...");
            SpellCoreManager.updateSpellCore((ServerPlayerEntity) player, level);
        }
    }

    public void reset() {
        level = 1;
        xp = 0;
        skillPoints = 0;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putInt("Level", level);
        nbt.putInt("XP", xp);
        nbt.putInt("SkillPoints", skillPoints);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.level = nbt.getInt("Level");
        this.xp = nbt.getInt("XP");
        this.skillPoints = nbt.getInt("SkillPoints");
    }
}
