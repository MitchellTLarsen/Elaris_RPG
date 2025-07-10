package net.elarisrpg.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class PlayerClassData {
    private String playerClass = "";

    public String getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("ElarisClass", playerClass);
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("ElarisClass")) {
            this.playerClass = nbt.getString("ElarisClass");
        }
    }

    public boolean hasChosenClass(PlayerEntity player) {
        String playerClass = PlayerData.get(player).getClassData().getPlayerClass();
        return playerClass != null && !playerClass.isEmpty();
    }

    public void reset() {
        this.playerClass = "";
    }
}
