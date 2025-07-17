package net.elarisrpg.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class DialogueHandler {
    public static void openVillagerDialogue(MinecraftClient client) {
        // Example NPC name
        Text npcName = Text.literal("Kayleen");

        // Example dialogue lines
        List<String> lines = List.of(
                "Do you want to learn how to use the loom?",
                "I can teach you for 10 emeralds."
        );

        // Example portrait texture for the villager
        Identifier portraitTexture = new Identifier("minecraft", "textures/entity/villager/villager.png");

        // Open the DialogueScreen
        client.setScreen(new DialogueScreen(npcName, lines, portraitTexture));
    }
}