package net.elarisrpg.client.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class RPGScreen extends LightweightGuiDescription {

    private WWidget currentContent;

    public RPGScreen() {
        // Root panel
        WPlainPanel root = new WPlainPanel();
        root.setSize(320, 240);
        setRootPanel(root);

        // Tab buttons
        WButton characterTab = new WButton(new ItemIcon(new ItemStack(Items.IRON_SWORD)));
        WButton skillsTab = new WButton(new ItemIcon(new ItemStack(Items.EXPERIENCE_BOTTLE)));
        WButton questTab = new WButton(new ItemIcon(new ItemStack(Items.WRITTEN_BOOK)));

        root.add(characterTab, 10, 5, 20, 20);
        root.add(skillsTab, 40, 5, 20, 20);
        root.add(questTab, 70, 5, 20, 20);

        // Panels for each tab
        WPlainPanel characterPanel = new WPlainPanel();
        characterPanel.add(new WLabel(Text.literal("Character Info Placeholder")), 0, 0);

        WPlainPanel skillContent = new WPlainPanel();
        for (int i = 0; i < 20; i++) {
            WLabel label = new WLabel(Text.literal("Skill Node " + (i + 1)));
            skillContent.add(label, 0, i * 18);
        }
        skillContent.setSize(400, 400);
        WScrollPanel skillScroll = new WScrollPanel(skillContent);
        skillScroll.setSize(400, 400);

        WPlainPanel questPanel = new WPlainPanel();
        questPanel.add(new WLabel(Text.literal("Quest Log Placeholder")), 0, 0);

        // Content holder area (logical, just tracks position)
        int contentX = 10;
        int contentY = 35;

        // Add default content
        currentContent = characterPanel;
        root.add(currentContent, contentX, contentY);

        // Switching logic
        characterTab.setOnClick(() -> {
            root.remove(currentContent);
            currentContent = characterPanel;
            root.add(currentContent, contentX, contentY);
        });

        skillsTab.setOnClick(() -> {
            root.remove(currentContent);
            currentContent = skillScroll;
            root.add(currentContent, contentX, contentY);
        });

        questTab.setOnClick(() -> {
            root.remove(currentContent);
            currentContent = questPanel;
            root.add(currentContent, contentX, contentY);
        });

        root.validate(this);
    }
}

