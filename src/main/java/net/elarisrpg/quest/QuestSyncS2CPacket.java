package net.elarisrpg.quest;

import net.elarisrpg.quest.ClientQuestCache;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class QuestSyncS2CPacket {
    public static final Identifier ID = new Identifier("elarisrpg", "sync_quests");

    public static PacketByteBuf createBuf(Set<Identifier> active, Set<Identifier> completed) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(active.size());
        for (Identifier id : active) buf.writeIdentifier(id);

        buf.writeVarInt(completed.size());
        for (Identifier id : completed) buf.writeIdentifier(id);

        return buf;
    }

    public static void handle(PacketByteBuf buf) {
        int activeSize = buf.readVarInt();
        Set<Identifier> active = new HashSet<>();
        for (int i = 0; i < activeSize; i++) active.add(buf.readIdentifier());

        int completedSize = buf.readVarInt();
        Set<Identifier> completed = new HashSet<>();
        for (int i = 0; i < completedSize; i++) completed.add(buf.readIdentifier());

        ClientQuestCache.setQuests(active, completed);
    }
}
