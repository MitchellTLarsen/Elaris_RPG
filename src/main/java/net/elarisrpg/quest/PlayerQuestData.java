package net.elarisrpg.quest;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class PlayerQuestData {
    private final Set<Identifier> talkedToNpcs = new HashSet<>();

    public void markNpcTalkedTo(Identifier npcId) {
        talkedToNpcs.add(npcId);
    }

    public boolean hasTalkedToNpc(Identifier npcId) {
        return talkedToNpcs.contains(npcId);
    }

    public Set<Identifier> getTalkedToNpcs() {
        return talkedToNpcs;
    }

    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        NbtList list = new NbtList();
        for (Identifier id : talkedToNpcs) {
            list.add(NbtString.of(id.toString()));
        }
        tag.put("TalkedToNpcs", list);
        return tag;
    }

    public void fromNbt(NbtCompound tag) {
        talkedToNpcs.clear();
        if (tag.contains("TalkedToNpcs", NbtElement.LIST_TYPE)) {
            NbtList list = tag.getList("TalkedToNpcs", NbtElement.STRING_TYPE);
            for (int i = 0; i < list.size(); i++) {
                talkedToNpcs.add(new Identifier(list.getString(i)));
            }
        }
    }
}
