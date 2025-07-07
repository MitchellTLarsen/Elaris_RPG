package net.elaris;

import net.elaris.client.gui.LevelScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElarisRPGClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Correctly register keybinds
        ElarisRPGKeyBinds.register();
        net.elaris.client.render.MobHealthRender.register();

        // Use the keybind from the keybinds class
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (net.elaris.ElarisRPGKeyBinds.OPEN_LEVEL_SCREEN.wasPressed()) {
                if (client.currentScreen instanceof LevelScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new LevelScreen());
                }
            }
        });

        // Draw custom XP bar overlay
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerEntity player = mc.player;

            if (player != null && !player.isCreative() && !player.isSpectator()) {
                LevelInfo info = PlayerData.get(player);

                float progress = (float) info.getXp() / info.xpToNextLevel();

                Identifier VANILLA_ICONS = new Identifier("minecraft", "textures/gui/icons.png");

                int barWidth = 182;
                int barHeight = 5;

                int filled = (int) (progress * barWidth);

                int screenWidth = mc.getWindow().getScaledWidth();
                int screenHeight = mc.getWindow().getScaledHeight();

                int barX = (screenWidth - barWidth) / 2;
                int barY = screenHeight - 28;

                // Draw vanilla XP bar background
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
        });
    }
}
