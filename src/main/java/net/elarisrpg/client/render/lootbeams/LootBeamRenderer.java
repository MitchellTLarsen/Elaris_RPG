package net.elarisrpg.client.render.lootbeams;

import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class LootBeamRenderer {

    public static final Identifier BEAM_TEXTURE = new Identifier("elarisrpg", "textures/gui/lootbeam.png");

    /**
     * Renders a vertical loot beam that always faces the player horizontally.
     *
     * @param matrices   Matrix stack
     * @param consumers  Vertex consumer provider
     * @param x          x offset (usually 0)
     * @param y          y offset (usually 0)
     * @param z          z offset (usually 0)
     * @param tickDelta  partial tick
     * @param height     height of the beam
     * @param radius     beam radius
     * @param age        age for animation
     * @param color      RGB color (e.g. 0xFF0000 for red)
     * @param cameraYaw  current camera yaw in degrees
     */
    public static void renderCustomLootBeam(
            MatrixStack matrices,
            VertexConsumerProvider consumers,
            double x,
            double y,
            double z,
            float tickDelta,
            float height,
            float radius,
            float age,
            int color,
            float cameraYaw
    ) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        matrices.push();

        matrices.translate(x, y, z);

        // Rotate the quad to always face the camera horizontally
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-cameraYaw));

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        VertexConsumer vc = consumers.getBuffer(RenderLayer.getEntityTranslucent(BEAM_TEXTURE));

        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;

        float alphaTop = 0.0f;
        float alphaBottom = 1.0f;

        // Draw vertical quad
        vc.vertex(matrix, -radius, 0, 0)
                .color(r, g, b, alphaBottom)
                .texture(minU, maxV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(0, 0, 1)
                .next();

        vc.vertex(matrix, -radius, height, 0)
                .color(r, g, b, alphaTop)
                .texture(minU, minV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(0, 0, 1)
                .next();

        vc.vertex(matrix, radius, height, 0)
                .color(r, g, b, alphaTop)
                .texture(maxU, minV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(0, 0, 1)
                .next();

        vc.vertex(matrix, radius, 0, 0)
                .color(r, g, b, alphaBottom)
                .texture(maxU, maxV)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(0x00F000F0)
                .normal(0, 0, 1)
                .next();

        matrices.pop();
    }
}
