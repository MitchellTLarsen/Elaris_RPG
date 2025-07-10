package net.elaris.client.gui;

import net.elaris.data.LevelData;
import net.elaris.data.PlayerData;
import net.elaris.ElarisRPGKeyBinds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class LevelScreen extends Screen {

    private static final int BOX_WIDTH = 200;
    private static final int BOX_HEIGHT = 100;

    public LevelScreen() {
        super(Text.literal("Elaris RPG Level Screen"));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        int boxX = (this.width - BOX_WIDTH) / 2;
        int boxY = (this.height - BOX_HEIGHT) / 2;

        // Draw a semi-transparent gray box like vanilla menus
        int backgroundColor = 0xAA000000; // translucent black
        int borderColor = 0xFFAAAAAA; // light gray border

        // Draw outer border
        drawContext.fill(boxX - 2, boxY - 2, boxX + BOX_WIDTH + 2, boxY + BOX_HEIGHT + 2, borderColor);

        // Draw inner background
        drawContext.fill(boxX, boxY, boxX + BOX_WIDTH, boxY + BOX_HEIGHT, backgroundColor);

        if (player != null) {
            LevelData info = PlayerData.get(player).getLevelData();

            int centerX = this.width / 2;

            // Title
            drawContext.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "Character Information",
                    centerX,
                    boxY + 10,
                    0xFFFFFF
            );

            // Level
            drawContext.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "Level: " + info.getLevel(),
                    centerX,
                    boxY + 30,
                    0x00FF00
            );

            // XP
            drawContext.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "XP: " + info.getXp() + " / " + info.xpToNextLevel(),
                    centerX,
                    boxY + 50,
                    0xFFFF00
            );

            // Skill Points
            drawContext.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "Skill Points: " + info.getSkillPoints(),
                    centerX,
                    boxY + 70,
                    0x66CCFF
            );
        }

        super.render(drawContext, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ElarisRPGKeyBinds.OPEN_LEVEL_SCREEN.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
