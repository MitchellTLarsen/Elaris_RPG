package net.elaris;

import net.elaris.client.gui.LevelScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ElarisRPGClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Correctly register keybinds
        ElarisRPGKeyBinds.register();

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
            if (player != null) {
                LevelInfo info = PlayerData.get(player);

                float progress = (float) info.getXp() / info.xpToNextLevel();

                int barWidth = 182;
                int filled = (int)(progress * barWidth);

                int x = (mc.getWindow().getScaledWidth() / 2) - (barWidth / 2);
                int y = mc.getWindow().getScaledHeight() - 50;

                drawContext.fill(x, y, x + barWidth, y + 5, 0xFF555555);
                drawContext.fill(x, y, x + filled, y + 5, 0xFF00FF00);

                drawContext.drawText(
                        mc.textRenderer,
                        Text.literal("Level " + info.getLevel()),
                        x,
                        y - 10,
                        0xFFFFFF,
                        true
                );
            }
        });
    }
}
