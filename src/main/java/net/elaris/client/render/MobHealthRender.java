package net.elaris.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.*;
import net.elaris.util.RaycastUtils;

public class MobHealthRender {

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            if (player == null) return;

            float tickDelta = context.tickDelta();

            LivingEntity mob = RaycastUtils.findLookedAtMob(player, 16.0);
            if (mob == null) return;

            float maxHealth = Math.max(1, mob.getMaxHealth());
            float health = Math.min(mob.getHealth(), maxHealth);
            float healthRatio = health / maxHealth;

            double interpolatedX = mob.prevX + (mob.getX() - mob.prevX) * tickDelta;
            double interpolatedY = mob.prevY + (mob.getY() - mob.prevY) * tickDelta;
            double interpolatedZ = mob.prevZ + (mob.getZ() - mob.prevZ) * tickDelta;

            Vec3d pos = new Vec3d(interpolatedX, interpolatedY + mob.getHeight() + 0.5, interpolatedZ);

            MatrixStack matrices = context.matrixStack();
            matrices.push();

            matrices.translate(
                    pos.x - context.camera().getPos().x,
                    pos.y - context.camera().getPos().y,
                    pos.z - context.camera().getPos().z
            );

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-context.camera().getYaw()));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(context.camera().getPitch()));

            float scale = 0.02f;
            matrices.scale(-scale, -scale, scale);

            VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            MatrixStack.Entry entry = matrices.peek();

            Text text = Text.literal(mob.getDisplayName().getString() +
                    " [" + (int)health + "/" + (int)maxHealth + "]");

            int textWidth = client.textRenderer.getWidth(text);
            int barWidth = Math.max(100, textWidth + 10);
            int barHeight = 5;

            int segments = 10;
            int segmentWidth = barWidth / segments;

            int x = -barWidth / 2;
            int y = 0;

            for (int i = 0; i < segments; i++) {
                int segX = x + i * segmentWidth;
                boolean filled = (i + 1) <= (healthRatio * segments);
                int color = filled ? 0xFFAA0000 : 0xFF333333;
                drawRect(immediate, entry, segX, y, segmentWidth - 1, barHeight, color);
                drawRectBorder(immediate, entry, segX, y, segmentWidth - 1, barHeight, 0xFFAAAAAA);
            }

            client.textRenderer.draw(
                    text,
                    -textWidth / 2f,
                    y - 10,
                    0xFFFFFF,
                    false,
                    matrices.peek().getPositionMatrix(),
                    immediate,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    15728880
            );

            immediate.draw();
            matrices.pop();
        });
    }

    private static void drawRect(VertexConsumerProvider.Immediate immediate, MatrixStack.Entry entry,
                                 int x, int y, int width, int height, int color) {
        float a = (float)((color >> 24) & 255) / 255.0F;
        float r = (float)((color >> 16) & 255) / 255.0F;
        float g = (float)((color >> 8) & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        VertexConsumer vc = immediate.getBuffer(RenderLayer.getGui());
        vc.vertex(entry.getPositionMatrix(), x, y, 0).color(r, g, b, a).next();
        vc.vertex(entry.getPositionMatrix(), x, y + height, 0).color(r, g, b, a).next();
        vc.vertex(entry.getPositionMatrix(), x + width, y + height, 0).color(r, g, b, a).next();
        vc.vertex(entry.getPositionMatrix(), x + width, y, 0).color(r, g, b, a).next();
    }

    private static void drawRectBorder(VertexConsumerProvider.Immediate immediate, MatrixStack.Entry entry,
                                       int x, int y, int width, int height, int color) {
        drawRect(immediate, entry, x, y, width, 1, color);
        drawRect(immediate, entry, x, y + height - 1, width, 1, color);
        drawRect(immediate, entry, x, y, 1, height, color);
        drawRect(immediate, entry, x + width - 1, y, 1, height, color);
    }
}
