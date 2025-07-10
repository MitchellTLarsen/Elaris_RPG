package net.elarisrpg.data;

import net.minecraft.nbt.NbtCompound;

public interface NbtSerializable {
    void writeNbt(NbtCompound nbt);
    void readNbt(NbtCompound nbt);
}
