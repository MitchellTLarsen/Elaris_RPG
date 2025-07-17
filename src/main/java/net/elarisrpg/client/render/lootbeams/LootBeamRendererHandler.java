package net.elarisrpg.client.render.lootbeams;

import net.elarisrpg.util.RaycastUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public class LootBeamRendererHandler {
    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            var world = client.world;
            if (world == null) return;

            float tickDelta = context.tickDelta();

            var vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
            MatrixStack matrices = context.matrixStack();

            // Find the item the player is looking at, if any
            assert client.player != null;
            var lookedAtItem = RaycastUtils.findLookedAtItem(client.player, 8.0);

            for (var entity : world.getEntities()) {
                if (entity instanceof ItemEntity itemEntity) {

                    ItemStack stack = itemEntity.getStack();

                    LootRarity rarity = LootBeamManager.getRarity(stack);
                    var beamColor = LootBeamManager.getColorForItem(stack);

                    double interpolatedX = itemEntity.prevX + (itemEntity.getX() - itemEntity.prevX) * tickDelta;
                    double interpolatedY = itemEntity.prevY + (itemEntity.getY() - itemEntity.prevY) * tickDelta;
                    double interpolatedZ = itemEntity.prevZ + (itemEntity.getZ() - itemEntity.prevZ) * tickDelta;

                    double groundY = Math.floor(interpolatedY);

                    var camPos = context.camera().getPos();

                    matrices.push();

                    matrices.translate(
                            interpolatedX - camPos.x,
                            groundY - camPos.y,
                            interpolatedZ - camPos.z
                    );

                    LootBeamRenderer.renderCustomLootBeam(
                            matrices,
                            vertexConsumers,
                            0, 0, 0,
                            tickDelta,
                            2.0f,
                            0.1f,
                            itemEntity.age + tickDelta,
                            beamColor,
                            context.camera().getYaw()
                    );

                    // --- RENDER NAME IF PLAYER LOOKING AT THIS ITEM ---
                    if (lookedAtItem == itemEntity) {
                        var textRenderer = client.textRenderer;
                        String itemName = stack.getName().getString();

                        matrices.push();

                        // Position name above beam
                        matrices.translate(0, 1, 0);

                        // Rotate to face camera
                        matrices.multiply(context.camera().getRotation());

                        // Scale down text
                        float scale = 0.025f;
                        matrices.scale(-scale, -scale, scale);

                        float textWidth = textRenderer.getWidth(itemName);
                        textRenderer.draw(
                                itemName,
                                -textWidth / 2f,
                                0,
                                beamColor, // Color text to match rarity
                                false,
                                matrices.peek().getPositionMatrix(),
                                vertexConsumers,
                                net.minecraft.client.font.TextRenderer.TextLayerType.NORMAL,
                                0,
                                0x00F000F0
                        );

                        matrices.pop();
                    }

                    matrices.pop();
                }
            }

            vertexConsumers.draw();
        });
    }
}
