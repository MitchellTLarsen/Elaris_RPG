package net.elarisrpg.quest;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class QuestRegistry {
    private static final Map<Identifier, Quest> QUESTS = new HashMap<>();

    public static void register(Quest quest) {
        QUESTS.put(quest.getId(), quest);
    }

    public static Quest get(Identifier id) {
        return QUESTS.get(id);
    }

    public static void init() {
        register(new TalkToQuest(new Identifier("elarisrpg", "talk_to_kayleen"), new Identifier("elarisrpg", "kayleen")));
    }
}
