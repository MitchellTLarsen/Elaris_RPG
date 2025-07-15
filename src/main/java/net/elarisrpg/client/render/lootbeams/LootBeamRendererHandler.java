package net.elarisrpg.client.render.lootbeams;

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

            for (var entity : world.getEntities()) {
                if (entity instanceof ItemEntity itemEntity) {

                    ItemStack stack = itemEntity.getStack();

                    // Determine rarity
                    LootRarity rarity = LootBeamManager.getRarity(stack);
                    var beamColor = LootBeamManager.getColorForItem(stack);

                    double interpolatedX = itemEntity.prevX + (itemEntity.getX() - itemEntity.prevX) * tickDelta;
                    double interpolatedY = itemEntity.prevY + (itemEntity.getY() - itemEntity.prevY) * tickDelta;
                    double interpolatedZ = itemEntity.prevZ + (itemEntity.getZ() - itemEntity.prevZ) * tickDelta;

                    double groundY = Math.floor(interpolatedY);
                    double height = (interpolatedY - groundY) + 5;

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

                    matrices.pop();
                }
            }

            // Draw once after all items
            vertexConsumers.draw();
        });
    }
}
