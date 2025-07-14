package net.elarisrpg.client.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.elarisrpg.classes.ElarisClasses;
import net.elarisrpg.classes.PlayerClass;
import net.elarisrpg.ElarisNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class ClassSelectionScreen extends LightweightGuiDescription {

    private PlayerClass selectedClass;

    private final WLabel classNameLabel = new WLabel(Text.literal(""));
    private final WLabel descriptionLabel = new WLabel(Text.literal(""));
    private WBox iconBox = new WBox(Axis.VERTICAL);
    private final WButton confirmButton = new WButton(Text.literal("Confirm"));
    private WBox rightBox;
    private WBox buttonBox;
    private WBox root;

    private int uiWidth;

    public ClassSelectionScreen() {
        buildUi();
    }

    private void buildUi() {
        MinecraftClient client = MinecraftClient.getInstance();

        int windowWidth = client.getWindow().getScaledWidth();
        int windowHeight = client.getWindow().getScaledHeight();

        uiWidth = (int) (windowWidth * 0.7);
        int rightPanelWidth = uiWidth / 2;

        // Root panel is now vertical
        root = new WBox(Axis.VERTICAL);
        setRootPanel(root);
        root.setInsets(new Insets(8));
        root.setSpacing(12);
        root.setSize(uiWidth, 0);

        // Horizontal row for main content
        WBox contentRow = new WBox(Axis.HORIZONTAL);
        contentRow.setSpacing(12);
        root.add(contentRow);

        // LEFT: class buttons
        buttonBox = new WBox(Axis.VERTICAL);
        buttonBox.setSpacing(4);

        // Needs fixing
        for (PlayerClass pc : ElarisClasses.getAll()) {
            WButton clickable = new WButton(new ItemIcon(pc.getIconItem()));
            buttonBox.add(clickable);
        }

        contentRow.add(buttonBox);

        // RIGHT: details
        rightBox = new WBox(Axis.VERTICAL);
        rightBox.setSpacing(8);
        rightBox.setInsets(new Insets(8));
        rightBox.setSize(rightPanelWidth, 0);

        classNameLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
        rightBox.add(classNameLabel);

        rightBox.add(iconBox);

        descriptionLabel.setHorizontalAlignment(HorizontalAlignment.LEFT);
        rightBox.add(descriptionLabel);

        contentRow.add(rightBox);

        // Bottom bar for confirm button
        confirmButton.setOnClick(this::confirmSelection);
        confirmButton.setEnabled(false);

        WBox bottomBar = new WBox(Axis.HORIZONTAL);
        bottomBar.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        bottomBar.add(confirmButton);
        root.add(bottomBar);

        root.validate(this);
    }

    private void selectClass(PlayerClass pc) {
        this.selectedClass = pc;

        classNameLabel.setText(Text.literal(pc.getName()));
        descriptionLabel.setText(Text.literal(pc.getDescription()));

        // Create new iconBox
        WBox newIconBox = new WBox(Axis.HORIZONTAL);
        for (ItemStack stack : pc.getStartingItems()) {
            WItem item = new WItem(stack);
            newIconBox.add(item);
        }

        rightBox.remove(iconBox);
        rightBox.add(newIconBox);
        iconBox = newIconBox;

        confirmButton.setEnabled(true);

        getRootPanel().validate(this);
    }

    private void confirmSelection() {
        if (selectedClass == null) return;

        var buf = PacketByteBufs.create();
        buf.writeString(selectedClass.getName());
        ClientPlayNetworking.send(ElarisNetworking.CHOOSE_CLASS_PACKET, buf);

        MinecraftClient.getInstance().setScreen(null);
    }

    private void recalculateLayout(int windowWidth, int windowHeight) {
        uiWidth = (int) (windowWidth * 0.7);
        int rightPanelWidth = uiWidth / 2;

        root.setSize(uiWidth, 0);
        rightBox.setSize(rightPanelWidth, 0);

        getRootPanel().validate(this);
    }
}
