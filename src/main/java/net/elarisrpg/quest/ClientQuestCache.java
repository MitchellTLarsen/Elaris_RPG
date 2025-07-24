package net.elarisrpg.quest;

import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public class ClientQuestCache {
    private static final Set<Identifier> activeQuests = new HashSet<>();
    private static final Set<Identifier> completedQuests = new HashSet<>();

    public static void setQuests(Set<Identifier> active, Set<Identifier> completed) {
        activeQuests.clear();
        completedQuests.clear();
        activeQuests.addAll(active);
        completedQuests.addAll(completed);
    }

    public static Set<Identifier> getActiveQuests() {
        return activeQuests;
    }

    public static Set<Identifier> getCompletedQuests() {
        return completedQuests;
    }
}
