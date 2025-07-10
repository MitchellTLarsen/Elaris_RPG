package net.elarisrpg.data;

import net.minecraft.nbt.NbtCompound;

public class PlayerClassData implements NbtSerializable {

    private String playerClass = "";

    public String getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
    }

    public void reset() {
        playerClass = "";
    }

    public boolean hasChosenClass() {
        return playerClass != null && !playerClass.isEmpty();
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("ElarisClass", playerClass);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        this.playerClass = nbt.getString("ElarisClass");
    }
}
