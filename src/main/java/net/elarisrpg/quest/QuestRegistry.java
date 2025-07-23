package net.elarisrpg.quest;

import net.minecraft.util.Identifier;

import java.util.Collection;
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

    public static Collection<Quest> getAll() {
        return QUESTS.values();
    }

    public static void init() {
        QuestRegistry.register(new TalkToQuest(
                new Identifier("elarisrpg", "talk_to_kayleen"),
                "Speak with Kayleen",
                new Identifier("elarisrpg", "kayleen"),
                false
        ));

        QuestRegistry.register(new KillMobQuest(
                new Identifier("elarisrpg", "kill_slimes"),
                "Cull the Slimes",
                new Identifier("minecraft", "slime"),
                5,
                true
        ));
    }
}
