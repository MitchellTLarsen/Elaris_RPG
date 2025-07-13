package net.elarisrpg.client.gui;

import net.elarisrpg.classes.ElarisClasses;
import net.elarisrpg.classes.PlayerClass;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.List;

public class ClassSelectionScreen extends Screen {

    private final List<PlayerClass> classes;
    private PlayerClass selectedClass;

    public ClassSelectionScreen() {
        super(Text.literal("Select Your Class"));
        this.classes = List.copyOf(ElarisClasses.getAll());
    }

    @Override
    protected void init() {
        int y = 40;
        int x = 20;

        for (PlayerClass pc : classes) {
            addDrawableChild(ButtonWidget.builder(
                    Text.literal(pc.getName()),
                    btn -> selectClass(pc)
            ).position(x, y).size(120, 20).build());

            y += 24;
        }

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Confirm"),
                btn -> confirmSelection()
        ).position(width - 140, height - 40).size(120, 20).build());
    }

    private void selectClass(PlayerClass pc) {
        this.selectedClass = pc;
    }

    private void confirmSelection() {
        if (selectedClass == null) return;

        var buf = net.fabricmc.fabric.api.networking.v1.PacketByteBufs.create();
        buf.writeString(selectedClass.getName());

        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                net.elarisrpg.ElarisNetworking.CHOOSE_CLASS_PACKET,
                buf
        );

        close();
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        if (selectedClass != null) {
            int panelX = width / 2 + 20;
            int panelY = 40;

            // Draw class name
            context.drawText(
                    textRenderer,
                    Text.literal(selectedClass.getName()),
                    panelX,
                    panelY,
                    0xFFFFFF,
                    false
            );

            panelY += 20;

            // Draw starting equipment icons vertically
            int itemX = panelX;
            int itemY = panelY;

            for (ItemStack stack : selectedClass.getStartingItems()) {
                context.drawItem(stack, itemX, itemY);

                boolean isHovered =
                        mouseX >= itemX &&
                                mouseX < itemX + 16 &&
                                mouseY >= itemY &&
                                mouseY < itemY + 16;

                if (isHovered) {
                    context.drawItemTooltip(
                            textRenderer,
                            stack,
                            mouseX,
                            mouseY
                    );
                }

                itemY += 18;
            }

            // Description panel
            int descriptionX = panelX + 20;
            int maxDescriptionWidth = width - descriptionX - 20;

            List<OrderedText> wrappedLines = textRenderer.wrapLines(
                    Text.literal(selectedClass.getDescription()),
                    maxDescriptionWidth
            );

            int descY = panelY;
            for (OrderedText line : wrappedLines) {
                context.drawText(textRenderer, line, descriptionX, descY, 0xAAAAAA, false);
                descY += textRenderer.fontHeight + 2;
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }


}
