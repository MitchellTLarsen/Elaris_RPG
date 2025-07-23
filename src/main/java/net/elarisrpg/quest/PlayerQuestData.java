package net.elarisrpg.quest;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;

import java.util.*;

public class PlayerQuestData {
    private final Set<Identifier> talkedToNpcs = new HashSet<>();
    private final Map<Identifier, Integer> killedMobs = new HashMap<>();
    private final Set<Identifier> activeQuests = new HashSet<>();
    private final Set<Identifier> completedQuests = new HashSet<>();

    public void markNpcTalkedTo(Identifier npcId) {
        talkedToNpcs.add(npcId);
    }

    public boolean hasTalkedToNpc(Identifier npcId) {
        return talkedToNpcs.contains(npcId);
    }

    public void incrementKill(Identifier mobId) {
        killedMobs.put(mobId, getKills(mobId) + 1);
    }

    public int getKills(Identifier mobId) {
        return killedMobs.getOrDefault(mobId, 0);
    }

    public void startQuest(Identifier questId) {
        activeQuests.add(questId);
    }

    public void removeQuest(Identifier questId) {
        activeQuests.remove(questId);
    }

    public void resetQuest(Identifier questId) {
        activeQuests.remove(questId);
        completedQuests.remove(questId);
    }

    public void completeQuest(Identifier questId) {
        activeQuests.remove(questId);
        completedQuests.add(questId);
    }

    public boolean isCompleted(Identifier questId) {
        return completedQuests.contains(questId);
    }

    public Set<Identifier> getActiveQuests() {
        return activeQuests;
    }

    public Set<Identifier> getCompletedQuests() {
        return completedQuests;
    }

    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();

        // TalkedToNpcs
        NbtList npcList = new NbtList();
        for (Identifier id : talkedToNpcs) {
            npcList.add(NbtString.of(id.toString()));
        }
        tag.put("TalkedToNpcs", npcList);

        // KilledMobs
        NbtCompound kills = new NbtCompound();
        for (var entry : killedMobs.entrySet()) {
            kills.putInt(entry.getKey().toString(), entry.getValue());
        }
        tag.put("KilledMobs", kills);

        // ActiveQuests
        NbtList active = new NbtList();
        for (Identifier id : activeQuests) {
            active.add(NbtString.of(id.toString()));
        }
        tag.put("ActiveQuests", active);

        // CompletedQuests
        NbtList completed = new NbtList();
        for (Identifier id : completedQuests) {
            completed.add(NbtString.of(id.toString()));
        }
        tag.put("CompletedQuests", completed);

        return tag;
    }

    public void fromNbt(NbtCompound tag) {
        talkedToNpcs.clear();
        killedMobs.clear();
        activeQuests.clear();
        completedQuests.clear();

        if (tag.contains("TalkedToNpcs", NbtElement.LIST_TYPE)) {
            for (NbtElement el : tag.getList("TalkedToNpcs", NbtElement.STRING_TYPE)) {
                talkedToNpcs.add(new Identifier(el.asString()));
            }
        }

        if (tag.contains("KilledMobs", NbtElement.COMPOUND_TYPE)) {
            NbtCompound kills = tag.getCompound("KilledMobs");
            for (String key : kills.getKeys()) {
                killedMobs.put(new Identifier(key), kills.getInt(key));
            }
        }

        if (tag.contains("ActiveQuests", NbtElement.LIST_TYPE)) {
            for (NbtElement el : tag.getList("ActiveQuests", NbtElement.STRING_TYPE)) {
                activeQuests.add(new Identifier(el.asString()));
            }
        }

        if (tag.contains("CompletedQuests", NbtElement.LIST_TYPE)) {
            for (NbtElement el : tag.getList("CompletedQuests", NbtElement.STRING_TYPE)) {
                completedQuests.add(new Identifier(el.asString()));
            }
        }
    }
}
