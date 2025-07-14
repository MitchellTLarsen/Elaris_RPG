package net.elarisrpg.client.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.elarisrpg.data.LevelData;
import net.elarisrpg.data.PlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class LevelScreen extends LightweightGuiDescription {

    public LevelScreen() {
        PlayerEntity player = MinecraftClient.getInstance().player;

        // Measure the pixel width of the title
        String desiredTitle = "Character Information";
        int titleWidth = MinecraftClient.getInstance()
                .textRenderer
                .getWidth(Text.literal(desiredTitle));

        int desiredWidth = titleWidth + 40;

        WBox root = new WBox(Axis.VERTICAL);
        setRootPanel(root);
        root.setInsets(new Insets(8));
        root.setSpacing(4);
        root.setSize(desiredWidth, 0);

        WLabel title = new WLabel(Text.literal(desiredTitle));
        title.setHorizontalAlignment(HorizontalAlignment.LEFT);
        root.add(title);

        if (player != null) {
            LevelData info = PlayerData.get(player).getLevelData();

            WLabel levelLabel = new WLabel(Text.literal("Level: " + info.getLevel()));
            levelLabel.setColor(0x00FF00);
            root.add(levelLabel);

            WLabel xpLabel = new WLabel(Text.literal("XP: " + info.getXp() + " / " + info.xpToNextLevel()));
            xpLabel.setColor(0xFFFF00);
            root.add(xpLabel);

            WLabel skillLabel = new WLabel(Text.literal("Skill Points: " + info.getSkillPoints()));
            skillLabel.setColor(0x66CCFF);
            root.add(skillLabel);
        } else {
            WLabel errorLabel = new WLabel(Text.literal("Player not found"));
            root.add(errorLabel);
        }

        root.validate(this);
    }
}