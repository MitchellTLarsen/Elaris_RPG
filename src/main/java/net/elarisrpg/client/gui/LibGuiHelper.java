package net.elarisrpg.client.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.client.MinecraftClient;

import java.util.function.Supplier;

public class LibGuiHelper  extends CottonClientScreen {

    private final Supplier<? extends GuiDescription> guiSupplier;

    public LibGuiHelper(Supplier<? extends GuiDescription> guiSupplier) {
        super(guiSupplier.get());
        this.guiSupplier = guiSupplier;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        // Rebuild GUI description
        this.description = guiSupplier.get();
        this.init(client, width, height);
    }
}
