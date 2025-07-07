package net.elaris;

import net.minecraft.nbt.NbtCompound;

public class LevelInfo {

    private int level = 1;
    private int xp = 0;

    public void addXp(int amount) {
        this.xp += amount;
        while (xp >= xpToNextLevel()) {
            xp -= xpToNextLevel();
            level++;
            System.out.println(ElarisRPG.MOD_ID + " Player leveled up to " + level);
        }
    }

    public int getXp() {
        return xp;
    }

    public int xpToNextLevel() {
        return level * 100;
    }

    public int getLevel() {
        return level;
    }

    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("Level", level);
        tag.putInt("XP", xp);
        return tag;
    }

    public void fromNbt (NbtCompound tag) {
        this.level = tag.getInt("Level");
        this.xp = tag.getInt("XP");
    }

    public void reset() {
        this.level = 1;
        this.xp = 0;
    }
}
