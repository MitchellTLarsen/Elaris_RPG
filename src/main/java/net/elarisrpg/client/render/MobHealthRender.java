package net.elarisrpg.client.render;

import net.elarisrpg.client.HitMobTracker;
import net.elarisrpg.util.RaycastUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MobHealthRender {

    public static final Identifier BAR_SPRITESHEET = new Identifier("elarisrpg", "textures/gui/healthbars.png");

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            if (player == null) return;

            float tickDelta = context.tickDelta();

            Set<LivingEntity> mobsToRender = new HashSet<>();

            // Mob the player is looking at
            LivingEntity lookedAt = RaycastUtils.findLookedAtMob(player, 16.0);
            if (lookedAt != null) {
                mobsToRender.add(lookedAt);
            }

            // Recently hit mobs
            for (int entityId : HitMobTracker.getActiveMobs()) {
                if (client.world == null) continue;
                var entity = client.world.getEntityById(entityId);
                if (entity instanceof LivingEntity mob && mob.isAlive()) {
                    renderHealthBarAboveMob(context, mob, tickDelta);
                }
            }

            for (LivingEntity mob : mobsToRender) {
                renderHealthBarAboveMob(context, mob, tickDelta);
            }

            client.getBufferBuilders().getEntityVertexConsumers().draw();

            renderDamagePopups(context);
        });
    }

    private static void renderHealthBarAboveMob(WorldRenderContext context, LivingEntity mob, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();

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

        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        MatrixStack.Entry entry = matrices.peek();

        Text text = Text.literal(mob.getDisplayName().getString());
        int textWidth = client.textRenderer.getWidth(text);
        int barWidth = Math.max(64, textWidth + 10);
        int barHeight = 8;

        int x = -barWidth / 2;
        int y = 0;

        // Draw background bar slightly forward
        drawTexture(immediate, entry, BAR_SPRITESHEET, x, y,
                barWidth, barHeight,
                0, 5, 64, 64, 0.01f);

        int filledWidth = (int) (barWidth * healthRatio);
        if (filledWidth > 0) {
            drawTexture(immediate, entry, BAR_SPRITESHEET, x, y,
                    filledWidth, barHeight,
                    1, 5, 64, 64, 0f);
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
    }

    private static void renderDamagePopups(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        MatrixStack matrices = context.matrixStack();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        Iterator<DamagePopup> iter = DamagePopupManager.getPopups().iterator();
        while (iter.hasNext()) {
            DamagePopup popup = iter.next();
            popup.tick();

            if (popup.isExpired()) {
                iter.remove();
                continue;
            }

            Vec3d pos = popup.position;
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

            TextRenderer renderer = client.textRenderer;
            float fade = popup.getAlpha();
            int alpha = (int)(fade * 255);
            int fadedColor = (alpha << 24) | (popup.color & 0xFFFFFF);

            renderer.draw(
                    popup.text,
                    -renderer.getWidth(popup.text) / 2f,
                    0,
                    fadedColor,
                    false,
                    matrices.peek().getPositionMatrix(),
                    immediate,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    15728880
            );

            matrices.pop();
        }

        immediate.draw();
    }

    private static void drawTexture(
            VertexConsumerProvider.Immediate immediate,
            MatrixStack.Entry entry,
            Identifier texture,
            int x, int y,
            int width, int height,
            int rowIndex,
            int spriteHeight,
            int textureWidth,
            int textureHeight,
            float z
    ) {
        VertexConsumer vc = immediate.getBuffer(RenderLayer.getEntityTranslucent(texture));

        float u0 = 0.0f;
        float u1 = (float) width / (float) textureWidth;
        float v0 = (rowIndex * spriteHeight) / (float) textureHeight;
        float v1 = ((rowIndex + 1) * spriteHeight) / (float) textureHeight;

        vc.vertex(entry.getPositionMatrix(), x, y, z)
                .color(255, 255, 255, 255)
                .texture(u0, v0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(0, 0, 1)
                .next();

        vc.vertex(entry.getPositionMatrix(), x, y + height, z)
                .color(255, 255, 255, 255)
                .texture(u0, v1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(0, 0, 1)
                .next();

        vc.vertex(entry.getPositionMatrix(), x + width, y + height, z)
                .color(255, 255, 255, 255)
                .texture(u1, v1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(0, 0, 1)
                .next();

        vc.vertex(entry.getPositionMatrix(), x + width, y, z)
                .color(255, 255, 255, 255)
                .texture(u1, v0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(0, 0, 1)
                .next();
    }
}
