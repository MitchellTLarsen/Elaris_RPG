package net.elarisrpg;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

public class ElarisServerNetworking {

    public static final Identifier MOB_HIT_PACKET = new Identifier("elarisrpg", "mob_hit");

    public static void sendMobHitPacket(ServerPlayerEntity player, int entityId, float damageAmount) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeFloat(damageAmount);
        ServerPlayNetworking.send(player, MOB_HIT_PACKET, buf);
    }
}
