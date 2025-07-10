package net.elaris;

// Imports from your mod
import net.elaris.classes.ClassSelectionScreen;
import net.elaris.client.ElarisHud;
import net.elaris.client.HitMobTracker;
import net.elaris.client.gui.LevelScreen;
import net.elaris.client.render.MobOutlineRenderer;
import net.elaris.client.render.MobHealthRender;

// Fabric API imports
import net.elaris.data.PlayerData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

// Minecraft classes
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElarisRPGClient implements ClientModInitializer {

    // Flag so we only try to open the class selection screen once per session
    private static boolean triedToOpenClassScreen = false;

    @Override
    public void onInitializeClient() {

        // Register custom keybinds and rendering overlays
        ElarisRPGKeyBinds.register();
        ElarisHud.register();
        MobHealthRender.register();
        MobOutlineRenderer.register();

        // ----------------------------------------------------------------------------------------------------
        // LISTEN FOR PLAYER DATA SYNC FROM SERVER
        // ----------------------------------------------------------------------------------------------------

        ClientPlayNetworking.registerGlobalReceiver(
                ElarisNetworking.PLAYER_DATA_SYNC_PACKET,
                (client, handler, buf, responseSender) -> {
                    int level = buf.readInt();
                    int xp = buf.readInt();
                    String cls = buf.readString();

                    client.execute(() -> {
                        if (client.player != null) {
                            var data = PlayerData.get(client.player);
                            data.getLevelData().setLevel(level);
                            data.getLevelData().setXp(xp);
                            data.getClassData().setPlayerClass(cls);
                        }
                    });
                }
        );

        // ----------------------------------------------------------------------------------------------------
        // LISTEN FOR RESET CLASS PACKET FROM SERVER
        // ----------------------------------------------------------------------------------------------------

        ClientPlayNetworking.registerGlobalReceiver(
                ElarisNetworking.RESET_CLASS_PACKET,
                (client, handler, buf, responseSender) -> client.execute(() -> {
                    if (client.player != null) {
                        PlayerData.get(client.player).getClassData().reset();
                        triedToOpenClassScreen = false;
                    }
                })
        );

        // ----------------------------------------------------------------------------------------------------
        // RENDER CUSTOM XP BAR
        // ----------------------------------------------------------------------------------------------------

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerEntity player = mc.player;

            if (player != null && !player.isCreative() && !player.isSpectator()) {
                var info = PlayerData.get(player).getLevelData();

                float progress = (float) info.getXp() / info.xpToNextLevel();

                Identifier VANILLA_ICONS = new Identifier("minecraft", "textures/gui/icons.png");

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
        });

        // ----------------------------------------------------------------------------------------------------
        // COMBINED CLIENT TICK EVENT (OPTIONAL IMPROVEMENT)
        // ----------------------------------------------------------------------------------------------------

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.isPaused()) {
                return;
            }

            // -----------------------------------------------------------
            // Process keybind to open/close Level Screen
            // -----------------------------------------------------------
            while (ElarisRPGKeyBinds.OPEN_LEVEL_SCREEN.wasPressed()) {
                if (client.currentScreen instanceof LevelScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new LevelScreen());
                }
            }

            // -----------------------------------------------------------
            // Process hit mob ticking logic
            // -----------------------------------------------------------
            HitMobTracker.tick();

            // -----------------------------------------------------------
            // Cleanup dead or missing mobs from hit mob tracker
            // -----------------------------------------------------------
            HitMobTracker.getActiveMobs().removeIf(id -> {
                var entity = client.world.getEntityById(id);
                return entity == null || !entity.isAlive();
            });

            // -----------------------------------------------------------
            // Check whether to open class selection screen
            // -----------------------------------------------------------
            var player = client.player;

            if (player == null || client.world == null) {
                triedToOpenClassScreen = false;
                return;
            }

            var classData = PlayerData.get(player).getClassData();

            if (classData.hasChosenClass(player)) {
                triedToOpenClassScreen = false;
                return;
            }

            if (!triedToOpenClassScreen && client.currentScreen == null) {
                client.setScreen(new ClassSelectionScreen());
                triedToOpenClassScreen = true;
            }
        });

        // ----------------------------------------------------------------------------------------------------
        // LISTEN FOR MOB HIT PACKET FROM SERVER
        // ----------------------------------------------------------------------------------------------------

        ClientPlayNetworking.registerGlobalReceiver(
                ElarisServerNetworking.MOB_HIT_PACKET,
                (client, handler, buf, responseSender) -> {
                    int entityId = buf.readInt();
                    float damage = buf.readFloat();

                    client.execute(() -> {
                        var entity = client.world.getEntityById(entityId);
                        if (entity instanceof LivingEntity living) {
                            HitMobTracker.markMobHit(entityId);
                        }
                    });
                }
        );
    }
}
