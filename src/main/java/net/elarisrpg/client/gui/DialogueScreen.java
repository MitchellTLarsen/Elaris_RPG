package net.elarisrpg.client.gui;

import net.elarisrpg.client.DialogueManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class DialogueScreen extends Screen {

    private final Text npcName;
    private final List<String> dialogueLines;
    private final Identifier backgroundTexture;

    private final int boxWidth = 300;
    private final int boxHeight = 100;

    public DialogueScreen(Text npcName, List<String> dialogueLines, Identifier backgroundTexture) {
        super(Text.literal("Dialogue"));
        this.npcName = npcName;
        this.dialogueLines = dialogueLines;
        this.backgroundTexture = backgroundTexture;
    }

    @Override
    protected void init() {
        super.init();

        int margin = 20;
        int boxX = (this.width - boxWidth) / 2;
        int boxY = this.height - boxHeight - margin;

        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 10;

        // Buttons go to the right of the dialogue box
        int buttonX = boxX + boxWidth + spacing;
        int buttonY = boxY;

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Okay!"),
                button -> close()
        ).position(buttonX, buttonY).size(buttonWidth, buttonHeight).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Not right now"),
                button -> close()
        ).position(buttonX, buttonY + buttonHeight + spacing).size(buttonWidth, buttonHeight).build());
    }

    @Override
    public void renderBackground(DrawContext context) {}

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        int margin = 20;
        int boxX = (this.width - boxWidth) / 2;
        int boxY = this.height - boxHeight - margin;

        // Dialog background
        context.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xBB000000);

        // NPC name
        int nameY = boxY + 10;
        drawCenteredText(context, npcName.getString(), this.width / 2, nameY, 0xFFFFFF);

        // Underline under NPC name
        int nameWidth = textRenderer.getWidth(npcName);
        int underlineY = nameY + 12;
        context.fill(
                this.width / 2 - nameWidth / 2,
                underlineY,
                this.width / 2 + nameWidth / 2,
                underlineY + 1,
                0xFFFFFFFF
        );

        // Dialogue text lines
        int textY = underlineY + 10;
        for (String line : dialogueLines) {
            drawCenteredText(context, line, this.width / 2, textY, 0xFFFFFF);
            textY += 12;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawCenteredText(DrawContext context, String text, int x, int y, int color) {
        context.drawTextWithShadow(this.textRenderer, text, x - textRenderer.getWidth(text) / 2, y, color);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        DialogueManager.endDialogue();
        super.close();
    }
}
