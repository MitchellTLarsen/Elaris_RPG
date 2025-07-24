package net.elarisrpg.client.gui;

import net.elarisrpg.classes.classskill.SkillTree;
import net.elarisrpg.data.PlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class SkillTreePanel {

    private final SkillTree skillTree;

    public SkillTreePanel() {
        var player = MinecraftClient.getInstance().player;
        this.skillTree = player != null ? PlayerData.get(player).getSkillTree() : null;
    }

    public void init(Consumer<ClickableWidget> addWidget) {
        if (skillTree == null) return;

        addWidget.accept(ButtonWidget.builder(Text.literal("Skill Tree UI Placeholder"), btn -> {})
                .position(100, 80).size(200, 20).build());

        // Add more complex widgets or rebuild the tree as needed
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(MinecraftClient.getInstance().textRenderer, "Skill Tree", 100, 40, 0xFFFFFF, false);
    }
}
