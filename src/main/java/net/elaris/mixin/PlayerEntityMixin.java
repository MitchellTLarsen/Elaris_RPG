package net.elaris.mixin;

// Import our own mod classes
import net.elaris.*;

// Fabric networking utilities
import net.elaris.data.PlayerData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

// Minecraft classes for players and NBT
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

// Mixin imports
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin modifies ServerPlayerEntity.
 *
 * ServerPlayerEntity represents:
 *   - a player connected to a dedicated server
 *   - manages server-side logic for players
 *
 * We use this mixin for:
 *   - saving our mod data
 *   - loading our mod data
 *   - sending player data from server → client when a player joins
 */
@Mixin(ServerPlayerEntity.class)
public abstract class PlayerEntityMixin {

    /**
     * Inject into:
     *
     *    public void onSpawn()
     *
     * Called:
     * - when a player joins the server
     * - after respawning
     *
     * We inject at "TAIL" so we run **after** vanilla code.
     *
     * Our goal:
     * → send the player's Elaris data from server → client
     * as soon as they join the game.
     */
    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void elaris$onSpawn(CallbackInfo ci) {
        // Convert "this" object from raw Object → PlayerEntity
        PlayerEntity player = (PlayerEntity)(Object)this;

        // Retrieve the mod's player data object
        PlayerData data = PlayerData.get(player);

        // Create a new buffer to store our packet data
        var buf = PacketByteBufs.create();

        // Write level info into the packet
        buf.writeInt(data.getLevelData().getLevel());
        buf.writeInt(data.getLevelData().getXp());

        // Write class name into the packet
        buf.writeString(data.getClassData().getPlayerClass());

        // Send the packet from server → client
        ServerPlayNetworking.send(
                (ServerPlayerEntity) player,
                ElarisNetworking.PLAYER_DATA_SYNC_PACKET,
                buf
        );
    }

    /**
     * Inject into:
     *
     *    writeCustomDataToNbt(NbtCompound nbt)
     *
     * Vanilla purpose:
     * - saves data into the player's .dat file on disk.
     *
     * We inject at "TAIL" so our data is saved **after** vanilla data.
     *
     * Our goal:
     * → Save our custom Elaris player data to the player's NBT file.
     */
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void elaris$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        // Fetch the player's custom Elaris data
        PlayerData data = PlayerData.get(player);

        // Create a new nested compound tag for our mod
        NbtCompound elarisNbt = new NbtCompound();

        // Write the player's mod data into this compound
        data.writeNbt(elarisNbt);

        // Store it under a single key in the player NBT file
        nbt.put("ElarisRPG", elarisNbt);

        // Optional logging so we can see data is being saved
        ElarisRPG.LOGGER.info(
                "Saving ElarisRPG data for {}: {}",
                player.getEntityName(),
                elarisNbt
        );
    }

    /**
     * Inject into:
     *
     *    readCustomDataFromNbt(NbtCompound nbt)
     *
     * Vanilla purpose:
     * - loads data from the player's .dat file on disk.
     *
     * We inject at "TAIL" so we run **after** vanilla loads its data.
     *
     * Our goal:
     * → Load our custom Elaris player data from disk back into memory.
     */
    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void elaris$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        // Check if our custom data exists
        if (nbt.contains("ElarisRPG")) {
            // Fetch our mod's PlayerData object
            PlayerData data = PlayerData.get(player);

            // Read our saved data back into memory
            data.readNbt(nbt.getCompound("ElarisRPG"));
        }
    }
}
