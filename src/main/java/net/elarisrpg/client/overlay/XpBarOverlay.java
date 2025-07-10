package net.elarisrpg.client.overlay;

import net.elarisrpg.data.PlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class XpBarOverlay {
    private static final Identifier VANILLA_ICONS = new Identifier("minecraft", "textures/gui/icons.png");

    public static void register() {
        HudRenderCallback.EVENT.register(XpBarOverlay::onHudRender);
    }

    private static void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;

        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        var info = PlayerData.get(player).getLevelData();

        float progress = (float) info.getXp() / info.xpToNextLevel();

        int barWidth = 182;
        int barHeight = 5;

        int filled = (int) (progress * barWidth);

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int barX = (screenWidth - barWidth) / 2;
        int barY = screenHeight - 28;

        // Draw vanilla-style XP bar background
        drawContext.drawTexture(
                VANILLA_ICONS,
                barX,
                barY,
                0, 64,
                barWidth,
                barHeight,
                256, 256
        );

        if (filled > 0) {
            drawContext.drawTexture(
                    VANILLA_ICONS,
                    barX,
                    barY,
                    0, 69,
                    filled,
                    barHeight,
                    256, 256
            );
        }

        String levelStr = String.valueOf(info.getLevel());
        int levelTextWidth = mc.textRenderer.getWidth(levelStr);
        int levelX = (screenWidth - levelTextWidth) / 2;
        int levelY = barY - 9;

        drawContext.drawTextWithShadow(
                mc.textRenderer,
                Text.literal(levelStr),
                levelX,
                levelY,
                0x80FF20
        );
    }
}
