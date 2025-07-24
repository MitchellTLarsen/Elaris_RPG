package net.elarisrpg.client.gui;

import net.elarisrpg.quest.ClientQuestCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.function.Consumer;

public class QuestLogPanel {

    public void init(Consumer<ClickableWidget> addWidget) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int y = 60;
        addWidget.accept(ButtonWidget.builder(Text.literal("Active Quests:"), btn -> {}).position(100, y).size(200, 20).build());
        y += 25;

        Set<Identifier> active = ClientQuestCache.getActiveQuests();
        for (Identifier id : active) {
            addWidget.accept(ButtonWidget.builder(Text.literal("- " + id.getPath()), btn -> {})
                    .position(100, y).size(200, 20).build());
            y += 25;
        }

        addWidget.accept(ButtonWidget.builder(Text.literal("Completed Quests:"), btn -> {}).position(100, y).size(200, 20).build());
        y += 25;

        Set<Identifier> completed = ClientQuestCache.getCompletedQuests();
        for (Identifier id : completed) {
            addWidget.accept(ButtonWidget.builder(Text.literal("- " + id.getPath()), btn -> {})
                    .position(100, y).size(200, 20).build());
            y += 25;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(MinecraftClient.getInstance().textRenderer, "Quest Log", 100, 40, 0xFFFFFF, false);
    }
}
