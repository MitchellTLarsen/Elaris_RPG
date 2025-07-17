package net.elarisrpg;

import net.elarisrpg.client.DialogueManager;
import net.elarisrpg.client.gui.DialogueScreen;
import net.elarisrpg.dialogue.DialogueRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class ElarisNetworking {

    public static final Identifier RESET_CLASS_PACKET = new Identifier("elarisrpg", "reset_class");
    public static final Identifier PLAYER_DATA_SYNC_PACKET = new Identifier("elarisrpg", "player_data_sync");
    public static final Identifier MOB_HIT_PACKET = new Identifier("elarisrpg", "mob_hit");

    public static final Identifier CHOOSE_CLASS_PACKET = new Identifier("elarisrpg", "choose_class");

    public static final Identifier OPEN_DIALOG_PACKET = new Identifier("elarisrpg", "open_dialog");

    public static VillagerEntity lastVillagerInteracted = null;

    // Send Mob Hit Packet
    public static void sendMobHitPacket(ServerPlayerEntity player, int entityId, float damageAmount) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeFloat(damageAmount);
        ServerPlayNetworking.send(player, MOB_HIT_PACKET, buf);
    }

    // Send Dialog Packet
    public static void sendOpenDialogPacket(ServerPlayerEntity player, int id, String entityName, String dialogueKey) {

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(id);  // NEW: send entity ID
        buf.writeString(entityName);
        buf.writeString(dialogueKey);

        ServerPlayNetworking.send(player, OPEN_DIALOG_PACKET, buf);
    }

    public static final Identifier DISABLE_VILLAGER_AI_PACKET = new Identifier("elarisrpg", "disable_villager_ai");

    public static void sendVillagerAiDisablePacket(int entityId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        ClientPlayNetworking.send(DISABLE_VILLAGER_AI_PACKET, buf);
    }

    public static final Identifier REENABLE_VILLAGER_AI_PACKET = new Identifier("elarisrpg", "reenable_villager_ai");

    public static void sendVillagerAiEnablePacket(int entityId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        ClientPlayNetworking.send(REENABLE_VILLAGER_AI_PACKET, buf);
    }

    // Register all S2C packets
    public static void registerS2CPackets() {
        // Register open dialog packet
        ClientPlayNetworking.registerGlobalReceiver(OPEN_DIALOG_PACKET, (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            String entityName = buf.readString();
            String dialogueKey = buf.readString();

            List<String> lines = DialogueRegistry.getDialogue(dialogueKey);

            client.execute(() -> {
                if (client.world != null) {
                    var entity = client.world.getEntityById(entityId);
                    if (entity instanceof VillagerEntity villager && client.player != null) {
                        ElarisNetworking.sendVillagerAiDisablePacket(villager.getId());
                        DialogueManager.startDialogue(villager);
                    }
                }

                client.setScreen(new DialogueScreen(
                        Text.literal(entityName),
                        lines,
                        new Identifier("minecraft", "textures/entity/villager/villager.png")
                ));
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(DISABLE_VILLAGER_AI_PACKET, (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readInt();

            server.execute(() -> {
                if (player.getWorld() != null) {
                    var entity = player.getWorld().getEntityById(entityId);
                    if (entity instanceof VillagerEntity villager) {
                        villager.setAiDisabled(true);
                        villager.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, player.getEyePos());
                    }
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(REENABLE_VILLAGER_AI_PACKET, (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readInt();

            server.execute(() -> {
                if (player.getWorld() != null) {
                    var entity = player.getWorld().getEntityById(entityId);
                    if (entity instanceof VillagerEntity villager) {
                        villager.setAiDisabled(false);
                    }
                }
            });
        });
    }

    // Register any C2S packets (none for dialog at the moment)
    public static void registerC2SPackets() {
        // For future use
    }
}
