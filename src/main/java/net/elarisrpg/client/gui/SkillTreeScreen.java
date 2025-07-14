package net.elarisrpg.client.gui;

import net.elarisrpg.classes.classskill.Skill;
import net.elarisrpg.classes.classskill.SkillRow;
import net.elarisrpg.classes.classskill.SkillTree;
import net.elarisrpg.data.PlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SkillTreeScreen extends Screen {

    private final PlayerData playerData;
    private final SkillTree skillTree;

    // Each row's horizontal scroll offset in pixels
    private final Map<Integer, Float> scrollOffsets = new HashMap<>();
    private final Map<Integer, Boolean> draggingScrollBar = new HashMap<>();
    private final Map<Integer, Float> dragStartX = new HashMap<>();

    public SkillTreeScreen(PlayerData playerData) {
        super(Text.literal("Skill Tree"));
        this.playerData = playerData;
        this.skillTree = playerData.getSkillTree();
    }

    @Override
    protected void init() {
        int y = 30;
        int rowSpacing = 100;
        int x = 20;

        int totalSkillPoints = playerData.getTotalSkillPoints();
        int totalAllocated = skillTree.getRows().stream()
                .mapToInt(SkillRow::getRowPointsAllocated)
                .sum();

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Close"),
                btn -> close()
        ).position(width - 140, height - 40).size(120, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Skill Points Available: " + (totalSkillPoints - totalAllocated)),
                btn -> {}
        ).position(x, y).size(250, 20).build());

        y += 30;

        List<SkillRow> rows = skillTree.getRows();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            SkillRow row = rows.get(rowIndex);
            int currentY = y + rowIndex * rowSpacing;

            int rowPoints = row.getRowPointsAllocated();

            // Row label
            addDrawableChild(ButtonWidget.builder(
                    Text.literal("Row " + (rowIndex + 1) + " (" + rowPoints + " pts)"),
                    btn -> {}
            ).position(x, currentY).size(150, 20).build());

            addDrawableChild(ButtonWidget.builder(
                    Text.literal("-"),
                    btn -> {
                        if (row.getRowPointsAllocated() > 0) {
                            row.setRowPointsAllocated(row.getRowPointsAllocated() - 1);
                            resetLockedSkills(row);
                            this.init(MinecraftClient.getInstance(), this.width, this.height);
                        }
                    }
            ).position(x + 160, currentY).size(20, 20).build());

            addDrawableChild(ButtonWidget.builder(
                    Text.literal("+"),
                    btn -> {
                        int currentTotal = skillTree.getRows().stream()
                                .mapToInt(SkillRow::getRowPointsAllocated)
                                .sum();
                        if (currentTotal < totalSkillPoints) {
                            row.setRowPointsAllocated(row.getRowPointsAllocated() + 1);
                            this.init(MinecraftClient.getInstance(), this.width, this.height);
                        }
                    }
            ).position(x + 185, currentY).size(20, 20).build());

            addDrawableChild(ButtonWidget.builder(
                    Text.literal("Reset"),
                    btn -> {
                        row.setRowPointsAllocated(0);
                        row.getSkills().forEach(skill -> skill.setPointsAllocated(0));
                        this.init(MinecraftClient.getInstance(), this.width, this.height);
                    }
            ).position(x + 210, currentY).size(60, 20).build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        int y = 60;
        int rowSpacing = 100;
        int x = 300;

        List<SkillRow> rows = skillTree.getRows();

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            SkillRow row = rows.get(rowIndex);
            int currentY = y + rowIndex * rowSpacing;

            renderScrollableSkillRow(context, row, rowIndex, x, currentY, 350, 30);
        }
    }

    private void renderScrollableSkillRow(
            DrawContext context,
            SkillRow row,
            int rowIndex,
            int x,
            int y,
            int width,
            int height
    ) {
        List<Skill> skills = row.getSkills();

        float scrollX = scrollOffsets.getOrDefault(rowIndex, 0f);
        int skillSpacing = 160;

        int virtualWidth = skills.size() * skillSpacing;

        // Draw background box
        context.fill(x - 2, y - 2, x + width + 2, y + height + 22, 0xFF202020);

        // Enable scissor box (clipping)
        context.enableScissor(x, y, x + width, y + height);

        int skillX = x - (int) scrollX;

        for (int skillIndex = 0; skillIndex < skills.size(); skillIndex++) {
            Skill skill = skills.get(skillIndex);
            boolean unlocked = isSkillUnlocked(row, skills, skillIndex);
            int pointsAllocated = skill.getPointsAllocated();

            int buttonY = y;

            ButtonWidget minus = ButtonWidget.builder(
                    Text.literal("-"),
                    btn -> {
                        if (pointsAllocated > 0) {
                            skill.setPointsAllocated(pointsAllocated - 1);
                            this.init(MinecraftClient.getInstance(), this.width, this.height);
                        }
                    }
            ).position(skillX, buttonY).size(20, 20).build();
            minus.active = unlocked;
            addDrawableChild(minus);

            ButtonWidget label = ButtonWidget.builder(
                    Text.literal(skill.getName() + " (" + pointsAllocated + "/" + skill.getMaxPoints() + ")"),
                    btn -> {}
            ).position(skillX + 25, buttonY).size(90, 20).build();
            label.active = unlocked;
            label.setTooltip(Tooltip.of(Text.literal(skill.getDescription())));
            addDrawableChild(label);

            ButtonWidget plus = ButtonWidget.builder(
                    Text.literal("+"),
                    btn -> {
                        int totalSpent = row.getSkills().stream()
                                .mapToInt(Skill::getPointsAllocated)
                                .sum();

                        if (unlocked &&
                                totalSpent < row.getRowPointsAllocated() &&
                                pointsAllocated < skill.getMaxPoints()) {
                            skill.setPointsAllocated(pointsAllocated + 1);
                            this.init(MinecraftClient.getInstance(), this.width, this.height);
                        }
                    }
            ).position(skillX + 120, buttonY).size(20, 20).build();
            plus.active = unlocked;
            addDrawableChild(plus);

            skillX += skillSpacing;
        }

        context.disableScissor();

        drawScrollBar(context, rowIndex, x, y + height + 4, width, 4, virtualWidth, scrollX);
    }

    private void drawScrollBar(
            DrawContext context,
            int rowIndex,
            int x,
            int y,
            int width,
            int height,
            int virtualWidth,
            float scrollX
    ) {
        if (virtualWidth <= width) return;

        context.fill(x, y, x + width, y + height, 0xFF555555);

        float ratio = width / (float) virtualWidth;
        int handleWidth = (int) (width * ratio);
        int handleX = (int) ((scrollX / (virtualWidth - width)) * (width - handleWidth));

        context.fill(x + handleX, y, x + handleX + handleWidth, y + height, 0xFFFFFFFF);

        addDrawableChild(ButtonWidget.builder(
                Text.literal(""),
                btn -> {
                    draggingScrollBar.put(rowIndex, true);
                    dragStartX.put(rowIndex, (float) MinecraftClient.getInstance().mouse.getX());
                }
        ).position(x + handleX, y).size(handleWidth, height).build());
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (var entry : draggingScrollBar.entrySet()) {
            int rowIndex = entry.getKey();
            if (entry.getValue()) {
                float startX = dragStartX.get(rowIndex);
                float delta = (float) mouseX - startX;
                int skillSpacing = 160;
                List<Skill> skills = skillTree.getRows().get(rowIndex).getSkills();
                int virtualWidth = skills.size() * skillSpacing;
                int visibleWidth = 350;

                float newScroll = scrollOffsets.getOrDefault(rowIndex, 0f) + delta;
                newScroll = Math.max(0, newScroll);
                newScroll = Math.min(newScroll, virtualWidth - visibleWidth);

                scrollOffsets.put(rowIndex, newScroll);
                dragStartX.put(rowIndex, (float) mouseX);
                this.init(MinecraftClient.getInstance(), this.width, this.height);
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingScrollBar.clear();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isSkillUnlocked(SkillRow row, List<Skill> skills, int index) {
        if (index == 0) return true;
        Skill previous = skills.get(index - 1);
        return previous.getPointsAllocated() >= 3;
    }

    private void resetLockedSkills(SkillRow row) {
        List<Skill> skills = row.getSkills();

        for (int i = 1; i < skills.size(); i++) {
            Skill prev = skills.get(i - 1);
            if (prev.getPointsAllocated() < 3) {
                skills.get(i).setPointsAllocated(0);
            }
        }
    }
}