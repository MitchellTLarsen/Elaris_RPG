package net.elaris.classes;

import net.minecraft.item.ItemStack;

import java.util.List;

public class PlayerClass {
    private final String name;
    private final String description;
    private final List<ItemStack> startingItems;

    public PlayerClass(String name, String description, List<ItemStack> startingItems) {
        this.name = name;
        this.description = description;
        this.startingItems = startingItems;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ItemStack> getStartingItems() {
        return startingItems;
    }
}
