package net.elarisrpg.util;

// Import Minecraft client and math utilities
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Utility class for raycasting logic.
 *
 * Purpose:
 * → Figure out which mob the player is looking at.
 *
 * Used for:
 * - rendering mob health bars only over the mob the player is aiming at
 * - detecting hits
 * - similar to how vanilla ray-tracing works
 */
public class RaycastUtils {

    /**
     * Find the living mob the player is currently looking at.
     *
     * @param player       the player entity
     * @param maxDistance  how far the ray should check for entities
     * @return             the mob entity the player is looking at, or null if none
     */
    public static LivingEntity findLookedAtMob(PlayerEntity player, double maxDistance) {
        // Get the client instance so we can access the world
        MinecraftClient client = MinecraftClient.getInstance();

        // Variable to track which mob is the best candidate
        LivingEntity closestMob = null;

        // Highest dot product found so far.
        // Dot product represents how closely the player's look direction aligns with the vector to the mob.
        double closestDot = -1.0;

        /**
         * Compute the player's "eye position."
         *
         * For example:
         * - a standing player is ~1.6 blocks tall
         * - so eyePos is above the feet position
         */
        Vec3d eyePos = player.getCameraPosVec(1.0f);

        /**
         * Player's facing direction as a normalized vector.
         *
         * For example:
         * (0, 0, -1) means looking forward along negative Z.
         */
        Vec3d lookVec = player.getRotationVec(1.0f);

        // Defensive check: ensure world exists
        assert client.world != null;

        /**
         * Get a list of all living entities nearby.
         *
         * - We search a cube centered on the player.
         * - The cube extends outwards by maxDistance in all directions.
         *
         * Filtering:
         * - exclude players
         * - exclude boss mobs like Ender Dragon, Warden, etc.
         *
         * The result is a list of all potential mobs that could be targeted.
         */
        List<LivingEntity> entities = client.world.getEntitiesByClass(
                LivingEntity.class,
                player.getBoundingBox().expand(maxDistance),
                entity -> !(entity instanceof PlayerEntity) && !isBoss(entity)
        );

        /**
         * Loop through each mob found nearby
         */
        for (LivingEntity mob : entities) {
            /**
             * Calculate the vector pointing from the player's eyes → mob center.
             *
             * We add half the mob's height so we target roughly chest height.
             */
            Vec3d toEntity = mob.getPos()
                    .add(0, mob.getHeight() * 0.5, 0)
                    .subtract(eyePos);

            double distance = toEntity.length();

            // Ignore mobs that are further away than maxDistance
            if (distance > maxDistance) continue;

            /**
             * Normalize the vector so it's a unit vector.
             */
            Vec3d toEntityNorm = toEntity.normalize();

            /**
             * Compute the dot product between:
             * - the player's look direction
             * - the normalized vector pointing to this mob
             *
             * dotProduct is:
             * - 1.0 → looking perfectly at the mob
             * - 0.0 → looking perpendicular to the mob
             * - negative → looking away from the mob
             */
            double dot = lookVec.dotProduct(toEntityNorm);

            /**
             * We only consider mobs that:
             * - are almost perfectly in our crosshair (dot > 0.99)
             * - OR closer to our center than previous candidates
             */
            if (dot > 0.99 && dot > closestDot) {
                closestDot = dot;
                closestMob = mob;
            }
        }

        // Return the mob we're looking at (or null if none)
        return closestMob;
    }

    /**
     * Check if a mob is a boss.
     *
     * Used to exclude big boss mobs from normal targeting logic.
     *
     * @param mob  the entity to check
     * @return     true if it's a known boss mob
     */
    static boolean isBoss(LivingEntity mob) {
        return mob.getType() == EntityType.ENDER_DRAGON
                || mob.getType() == EntityType.WITHER
                || mob.getType() == EntityType.WARDEN;
    }

}
