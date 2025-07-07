package net.elaris.client.gui;

import net.elaris.LevelInfo;
import net.elaris.PlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class LevelScreen extends Screen {

    public LevelScreen() {
        super(Text.literal("Elaris RPG Level Screen"));
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        this.renderBackground(drawContext);

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player != null) {
            LevelInfo info = PlayerData.get(player);

            int centerX = this.width / 2;
            int centerY = this.height / 2;

            drawContext.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "Elaris RPG",
                    centerX,
                    centerY - 40,
                    0xFFFFFF
            );

            drawContext.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "Level: " + info.getLevel(),
                    centerX,
                    centerY - 20,
                    0x00FF00
            );

            drawContext.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "XP: " + info.getXp() + " / " + info.xpToNextLevel(),
                    centerX,
                    centerY,
                    0xFFFF00
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
        if (net.elaris.ElarisRPGKeyBinds.OPEN_LEVEL_SCREEN.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
