package net.elarisrpg;

// Imports from your mod
import net.elarisrpg.client.gui.ClassSelectionScreen;
import net.elarisrpg.client.ElarisHud;
import net.elarisrpg.client.HitMobTracker;
import net.elarisrpg.client.gui.LevelScreen;
import net.elarisrpg.client.overlay.XpBarOverlay;
import net.elarisrpg.client.render.MobOutlineRenderer;
import net.elarisrpg.client.render.MobHealthRender;

// Fabric API imports
import net.elarisrpg.data.PlayerData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

// Minecraft classes
import net.minecraft.entity.LivingEntity;

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
        XpBarOverlay.register();

        // ----------------------------------------------------------------------------------------------------
        // LISTEN FOR PLAYER DATA SYNC FROM SERVER
        // ----------------------------------------------------------------------------------------------------

        ClientPlayNetworking.registerGlobalReceiver(
                ElarisNetworking.PLAYER_DATA_SYNC_PACKET,
                (client, handler, buf, responseSender) -> {
                    int level = buf.readInt();
                    int xp = buf.readInt();
                    String playerClass = buf.readString();

                    client.execute(() -> {
                        if (client.player != null) {
                            var data = PlayerData.get(client.player);
                            data.getLevelData().setLevel(level);
                            data.getLevelData().setXp(xp);
                            data.getClassData().setPlayerClass(playerClass);
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
                assert client.world != null;
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

            if (classData.hasChosenClass()) {
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
                ElarisNetworking.MOB_HIT_PACKET,
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
