package net.elaris.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class RaycastUtils {

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
            Vec3d toEntity = mob.getPos().add(0, mob.getHeight() * 0.5, 0).subtract(eyePos);
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

    static boolean isBoss(LivingEntity mob) {
        return mob.getType() == EntityType.ENDER_DRAGON
                || mob.getType() == EntityType.WITHER
                || mob.getType() == EntityType.WARDEN;
    }

}
