package net.elarisrpg.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;


public class RaycastUtils {

    /**
     * Find the living mob the player is currently looking at.
     *
     * @param player       the player entity
     * @param maxDistance  how far the ray should check for entities
     * @return             the mob entity the player is looking at, or null if none
     */
    public static LivingEntity findLookedAtMob(PlayerEntity player, double maxDistance) {
        MinecraftClient client = MinecraftClient.getInstance();

        LivingEntity closestMob = null;

        double closestDot = -1.0;

        Vec3d eyePos = player.getCameraPosVec(1.0f);

        Vec3d lookVec = player.getRotationVec(1.0f);

        assert client.world != null;

        List<LivingEntity> entities = client.world.getEntitiesByClass(
                LivingEntity.class,
                player.getBoundingBox().expand(maxDistance),
                entity -> !(entity instanceof PlayerEntity) && !isBoss(entity)
        );

        for (LivingEntity mob : entities) {
            Vec3d toEntity = mob.getPos()
                    .add(0, mob.getHeight() * 0.5, 0)
                    .subtract(eyePos);

            double distance = toEntity.length();

            if (distance > maxDistance) continue;

            Vec3d toEntityNorm = toEntity.normalize();

            double dot = lookVec.dotProduct(toEntityNorm);

            if (dot > 0.99 && dot > closestDot) {
                closestDot = dot;
                closestMob = mob;
            }
        }

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

    public static ItemEntity findLookedAtItem(PlayerEntity player, double maxDistance) {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemEntity closestItem = null;
        double closestDot = -1.0;

        Vec3d eyePos = player.getCameraPosVec(1.0f);
        Vec3d lookVec = player.getRotationVec(1.0f);

        assert client.world != null;

        List<ItemEntity> items = client.world.getEntitiesByClass(
                ItemEntity.class,
                player.getBoundingBox().expand(maxDistance),
                entity -> true
        );

        for (ItemEntity item : items) {
            Vec3d toEntity = item.getPos()
                    .add(0, item.getHeight() * 0.5, 0)
                    .subtract(eyePos);

            double distance = toEntity.length();
            if (distance > maxDistance) continue;

            Vec3d toEntityNorm = toEntity.normalize();
            double dot = lookVec.dotProduct(toEntityNorm);

            if (dot > 0.99 && dot > closestDot) {
                closestDot = dot;
                closestItem = item;
            }
        }
        return closestItem;
    }

}
