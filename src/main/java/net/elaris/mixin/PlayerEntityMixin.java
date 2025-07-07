package net.elaris.mixin;

import net.elaris.PlayerData;
import net.elaris.LevelInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeLevelData(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        LevelInfo info = PlayerData.get(player);
        nbt.put("ElarisLevelData", info.toNbt());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readLevelData(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (nbt.contains("ElarisLevelData")) {
            LevelInfo info = new LevelInfo();
            info.fromNbt(nbt.getCompound("ElarisLevelData"));
            PlayerData.set(player, info);
        }
    }
}