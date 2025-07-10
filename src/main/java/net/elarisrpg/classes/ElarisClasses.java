package net.elarisrpg.classes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ElarisClasses {

    private static final Map<Identifier, PlayerClass> CLASSES = new LinkedHashMap<>();

    static {
        register(new PlayerClass(
                new Identifier("elarisrpg", "warrior"),
                "Warrior",
                "A hardened fighter skilled in melee combat.",
                List.of(
                        new ItemStack(Items.IRON_SWORD),
                        new ItemStack(Items.IRON_CHESTPLATE)
                )
        ));

        register(new PlayerClass(
                new Identifier("elarisrpg", "elementalist"),
                "Elementalist",
                "A spellcaster who wields elemental forces.",
                List.of(
                        new ItemStack(Items.BLAZE_ROD),
                        new ItemStack(Items.LEATHER_HELMET)
                )
        ));

        register(new PlayerClass(
                new Identifier("elarisrpg", "ranger"),
                "Ranger",
                "A skilled archer who excels at ranged combat.",
                List.of(
                        new ItemStack(Items.BOW),
                        new ItemStack(Items.LEATHER_CHESTPLATE),
                        new ItemStack(Items.ARROW, 16)
                )
        ));

        register(new PlayerClass(
                new Identifier("elarisrpg", "rogue"),
                "Rogue",
                "A stealthy fighter who strikes swiftly and disappears.",
                List.of(
                        new ItemStack(Items.STONE_SWORD),
                        new ItemStack(Items.LEATHER_BOOTS)
                )
        ));
    }

    private static void register(PlayerClass playerClass) {
        CLASSES.put(playerClass.getId(), playerClass);
    }

    public static Collection<PlayerClass> getAll() {
        return CLASSES.values();
    }

    public static PlayerClass getById(Identifier id) {
        return CLASSES.get(id);
    }

    public static PlayerClass getByName(String name) {
        return CLASSES.values().stream()
                .filter(pc -> pc.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
