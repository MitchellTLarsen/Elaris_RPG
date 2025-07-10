package net.elarisrpg.mixin;

// Import our custom networking class for sending packets from server → client
import net.elarisrpg.ElarisServerNetworking;

// Import Minecraft classes used in the mixin
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

// SpongePowered Mixin annotations
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This Mixin modifies the class LivingEntity.
 *
 * In Minecraft, all mobs, animals, and players inherit from LivingEntity.
 *
 * We use a mixin so we can intercept whenever any mob takes damage on the server side.
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    /**
     * Inject into the method LivingEntity#damage
     *
     * Signature of the vanilla method:
     *
     *   public boolean damage(DamageSource source, float amount)
     *
     * We inject at "HEAD" which means:
     * → before any code in the method runs.
     *
     * Fabric automatically supplies the original method's arguments:
     * - DamageSource source → what caused the damage (e.g. sword, lava)
     * - float amount → how much damage was attempted
     *
     * CallbackInfoReturnable<Boolean> ci:
     * → lets us potentially cancel the method (not needed here).
     */
    @Inject(method = "damage", at = @At("HEAD"))
    private void elaris$onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        // "this" is originally a raw Object in a mixin.
        // We cast it to LivingEntity so we can call its methods.
        LivingEntity entity = (LivingEntity)(Object)this;

        // We want to run this logic ONLY on the server side.
        // Check if this entity's world is a ServerWorld.
        if (entity.getWorld() instanceof ServerWorld) {

            // Check if the attacker causing the damage is a ServerPlayerEntity.
            // (e.g. a player hit a mob with a sword)
            if (source.getAttacker() instanceof ServerPlayerEntity player) {

                // Send a custom network packet to the client
                // → so the client knows which mob was hit and how much damage was dealt.
                //
                // We send:
                // - player → the player who hit the mob
                // - entity.getId() → the unique numeric ID of the entity that was hit
                // - amount → the amount of damage dealt
                //
                // This allows the client to display things like:
                // - health bars
                // - damage numbers
                ElarisServerNetworking.sendMobHitPacket(
                        player,
                        entity.getId(),
                        amount
                );
            }
        }
    }
}
