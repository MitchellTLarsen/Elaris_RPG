package net.elarisrpg;

import net.elarisrpg.client.DialogueManager;
import net.elarisrpg.client.gui.DialogueScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
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
    public static void sendOpenDialogPacket(ServerPlayerEntity player, String... lines) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(lines.length);
        for (String s : lines) {
            buf.writeString(s);
        }
        ServerPlayNetworking.send(player, OPEN_DIALOG_PACKET, buf);
    }

    // Register all S2C packets
    public static void registerS2CPackets() {
        // Register open dialog packet
        ClientPlayNetworking.registerGlobalReceiver(OPEN_DIALOG_PACKET, (client, handler, buf, responseSender) -> {
            int lineCount = buf.readVarInt();
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < lineCount; i++) {
                lines.add(buf.readString());
            }

            client.execute(() -> {
                if (client.player == null) return;

                // Attempt to get last interacted villager
                var villager = lastVillagerInteracted;

                if (villager != null) {
                    DialogueManager.startDialogue(villager, client.player);
                }

                client.setScreen(new DialogueScreen(
                        Text.literal("Kayleen"),
                        lines,
                        new Identifier("minecraft", "textures/entity/villager/villager.png")
                ));
            });
        });
    }

    // Register any C2S packets (none for dialog at the moment)
    public static void registerC2SPackets() {
        // For future use
    }
}
