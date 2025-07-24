package net.elarisrpg.client.gui;

import net.elarisrpg.data.PlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class CharacterPanel {

    public void init(Consumer<ClickableWidget> addWidget) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        var data = PlayerData.get(player);
        var levelData = data.getLevelData();
        var classData = data.getClassData();

        addWidget.accept(ButtonWidget.builder(Text.literal("Level: " + levelData.getLevel()), btn -> {})
                .position(100, 60).size(200, 20).build());

        addWidget.accept(ButtonWidget.builder(Text.literal("XP: " + levelData.getXp()), btn -> {})
                .position(100, 90).size(200, 20).build());

        addWidget.accept(ButtonWidget.builder(Text.literal("Skill Points: " + levelData.getSkillPoints()), btn -> {})
                .position(100, 120).size(200, 20).build());

        addWidget.accept(ButtonWidget.builder(Text.literal("Class: " + classData.getPlayerClass()), btn -> {})
                .position(100, 150).size(200, 20).build());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawText(MinecraftClient.getInstance().textRenderer, "Character Info", 100, 40, 0xFFFFFF, false);
    }
}
