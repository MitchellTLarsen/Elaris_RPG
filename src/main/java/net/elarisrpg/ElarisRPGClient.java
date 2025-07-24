package net.elarisrpg;

// Imports from your mod

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.elarisrpg.client.DialogueManager;
import net.elarisrpg.client.HitMobTracker;
import net.elarisrpg.client.gui.*;
import net.elarisrpg.client.render.*;
import net.elarisrpg.client.render.lootbeams.LootBeamRendererHandler;
import net.elarisrpg.data.PlayerData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import io.github.cottonmc.cotton.gui.client.LibGui;

public class ElarisRPGClient implements ClientModInitializer {

    // Flag so we only try to open the class selection screen once per session
    private static boolean triedToOpenClassScreen = false;

    @Override
    public void onInitializeClient() {

        // Register custom keybinds and rendering overlays
        ElarisRPGKeyBinds.register();
        //ElarisHud.register();
        MobHealthRender.register();
        //MobOutlineRenderer.register();
        //XpBarOverlay.register();
        LootBeamRendererHandler.register();
        ElarisNetworking.registerS2CPackets();

        // Register villager look-at logic
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var villager = DialogueManager.getActiveVillager();
            if (villager != null && client.player != null) {
                villager.getLookControl().lookAt(client.player, 30.0f, 30.0f);
            }
        });

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
            while (ElarisRPGKeyBinds.OPEN_CHARACTER_SCREEN.wasPressed()) {
                if (client.currentScreen instanceof CottonClientScreen RPGScreen &&
                        RPGScreen.getDescription() instanceof RPGScreen) {
                    client.setScreen(null); // Close if already open
                } else {
                    MinecraftClient.getInstance().setScreen(new CottonClientScreen(new RPGScreen())); // Open new RPG screen
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
            } else if (!triedToOpenClassScreen && client.currentScreen == null) {
                client.setScreen(new LibGuiHelper(ClassSelectionScreen::new));
                triedToOpenClassScreen = true;
            }

            DamagePopupManager.tick();

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
                        assert client.world != null;
                        var entity = client.world.getEntityById(entityId);
                        if (entity instanceof LivingEntity living) {
                            HitMobTracker.markMobHit(entityId);

                            Vec3d popupPos = living.getPos()
                                    .add(0, living.getHeight() + 0.5, 0);

                            DamagePopupManager.addPopup(new DamagePopup(
                                    Text.literal(String.format("-%.1f", damage)),
                                    popupPos,
                                    0xFFEE3333 // bright red
                            ));
                        }
                    });
                }
        );
    }
}
