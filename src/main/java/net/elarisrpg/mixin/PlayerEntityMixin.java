package net.elarisrpg.mixin;

import net.elarisrpg.ElarisNetworking;
import net.elarisrpg.ElarisRPG;
import net.elarisrpg.data.PlayerData;
import net.elarisrpg.quest.PlayerQuestData;
import net.elarisrpg.quest.QuestManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles saving, loading, and syncing of Elaris RPG custom data.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void elaris$onSpawn(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        PlayerData data = PlayerData.get(player);

        var buf = PacketByteBufs.create();
        buf.writeInt(data.getLevelData().getLevel());
        buf.writeInt(data.getLevelData().getXp());
        buf.writeString(data.getClassData().getPlayerClass());

        ServerPlayNetworking.send((ServerPlayerEntity) player, ElarisNetworking.PLAYER_DATA_SYNC_PACKET, buf);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void elaris$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        // Write main RPG data
        PlayerData data = PlayerData.get(player);
        NbtCompound elarisNbt = new NbtCompound();
        data.writeNbt(elarisNbt);
        nbt.put("ElarisRPG", elarisNbt);

        // ✅ Write quest data
        PlayerQuestData questData = QuestManager.get((ServerPlayerEntity) player);
        NbtCompound questTag = questData.toNbt();
        nbt.put("ElarisRPG_Quests", questTag);

        ElarisRPG.LOGGER.info("Saving ElarisRPG data for {}: {}", player.getEntityName(), elarisNbt);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void elaris$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        if (nbt.contains("ElarisRPG")) {
            PlayerData data = PlayerData.get(player);
            data.readNbt(nbt.getCompound("ElarisRPG"));
        }

        // ✅ Load quest data
        if (nbt.contains("ElarisRPG_Quests")) {
            PlayerQuestData questData = QuestManager.get((ServerPlayerEntity) player);
            questData.fromNbt(nbt.getCompound("ElarisRPG_Quests"));
        }
    }
}
