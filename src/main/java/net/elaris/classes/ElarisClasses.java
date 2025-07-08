package net.elaris.classes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ElarisClasses {

    private static final Map<String, PlayerClass> CLASSES = new LinkedHashMap<>();

    static {
        register(new PlayerClass(
                "Acolyte",
                "An acolyte who channels dark powers. Skilled in curses and magic.",
                List.of(new ItemStack(Items.IRON_SWORD), new ItemStack(Items.LEATHER_CHESTPLATE))
        ));

        register(new PlayerClass(
                "Necromancer",
                "Master of undead arts. Summons minions to fight.",
                List.of(new ItemStack(Items.BONE), new ItemStack(Items.LEATHER_HELMET))
        ));

        // Add more classes as needed...
    }

    private static void register(PlayerClass playerClass) {
        CLASSES.put(playerClass.getName(), playerClass);
    }

    public static Collection<PlayerClass> getAll() {
        return CLASSES.values();
    }

    public static PlayerClass getByName(String name) {
        return CLASSES.get(name);
    }

}
