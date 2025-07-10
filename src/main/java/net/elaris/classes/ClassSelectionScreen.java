package net.elaris.classes;

import net.elaris.data.PlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import java.util.List;

public class ClassSelectionScreen extends Screen {

    private List<PlayerClass> classes;
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

        PlayerEntity player = MinecraftClient.getInstance().player;

        PlayerData data = PlayerData.get(player);
        data.getClassData().setPlayerClass(selectedClass.getName());

        for (var item : selectedClass.getStartingItems()) {
            player.giveItemStack(item.copy());
        }

        close();
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Render class description panel
        if (selectedClass != null) {
            int panelX = width / 2 + 20;
            int panelY = 40;

            context.drawText(textRenderer, Text.literal(selectedClass.getName()), panelX, panelY, 0xFFFFFF, false);
            context.drawText(textRenderer, Text.literal(selectedClass.getDescription()), panelX, panelY + 20, 0xAAAAAA, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

}
