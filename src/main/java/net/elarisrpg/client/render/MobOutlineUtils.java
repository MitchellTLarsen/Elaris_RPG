package net.elarisrpg.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import net.minecraft.util.math.Vec3d;

public class MobOutlineUtils {

    public static void renderEntityOutline(LivingEntity entity, MatrixStack matrices, Vec3d cameraPos, float tickDelta) {
        matrices.push();

        double interpolatedX = entity.prevX + (entity.getX() - entity.prevX) * tickDelta;
        double interpolatedY = entity.prevY + (entity.getY() - entity.prevY) * tickDelta;
        double interpolatedZ = entity.prevZ + (entity.getZ() - entity.prevZ) * tickDelta;

        double x = interpolatedX - cameraPos.x;
        double y = interpolatedY - cameraPos.y;
        double z = interpolatedZ - cameraPos.z;

        matrices.translate(x, y, z);

        var box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
        var expansion = 0.05;
        box = box.expand(expansion);

        var immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        var buffer = immediate.getBuffer(RenderLayer.getLines());

        WorldRenderer.drawBox(
                matrices,
                buffer,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                1.0f, 0.0f, 0.0f, // red color
                1.0f
        );

        immediate.draw();

        matrices.pop();
    }
}
