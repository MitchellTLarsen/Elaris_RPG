package net.elarisrpg.classes;

import net.elarisrpg.classes.classskill.SkillTree;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class PlayerClass {
    private final Identifier id;
    private final String name;
    private final String description;
    private final List<ItemStack> startingItems;
    private final SkillTree skillTree;

    public PlayerClass(Identifier id, String name, String description, List<ItemStack> startingItems, SkillTree skillTree) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startingItems = startingItems;
        this.skillTree = skillTree;
    }

    public Identifier getId() {
        return id;
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
