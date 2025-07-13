package net.elarisrpg;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ElarisNetworking {
    public static final Identifier RESET_CLASS_PACKET = new Identifier("elarisrpg", "reset_class");
    public static final Identifier PLAYER_DATA_SYNC_PACKET = new Identifier("elarisrpg", "player_data_sync");
    public static final Identifier MOB_HIT_PACKET = new Identifier("elarisrpg", "mob_hit");
    public static final Identifier CHOOSE_CLASS_PACKET = new Identifier("elarisrpg", "choose_class");

    public static void sendMobHitPacket(ServerPlayerEntity player, int entityId, float damageAmount) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeFloat(damageAmount);
        ServerPlayNetworking.send(player, MOB_HIT_PACKET, buf);
    }
}
