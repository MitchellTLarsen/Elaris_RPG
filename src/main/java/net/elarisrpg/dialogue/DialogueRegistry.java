package net.elarisrpg.dialogue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogueRegistry {

    private static final Map<String, List<String>> DIALOGUE_MAP = new HashMap<>();

    static {
        DIALOGUE_MAP.put("villager_trade", List.of(
                "Greetings, traveler!",
                "Would you like to trade with me?"
        ));
        DIALOGUE_MAP.put("blacksmith_intro", List.of(
                "Name's Gorvan. I shape iron with the dawn fire.",
                "Got coin? I've got steel."
        ));
        // Add more entries as needed
    }

    public static List<String> getDialogue(String key) {
        return DIALOGUE_MAP.getOrDefault(key, List.of("..."));
    }
}
