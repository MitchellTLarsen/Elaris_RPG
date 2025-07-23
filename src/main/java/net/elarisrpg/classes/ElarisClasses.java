package net.elarisrpg.classes;

import net.elarisrpg.classes.classskill.Skill;
import net.elarisrpg.classes.classskill.SkillRow;
import net.elarisrpg.classes.classskill.SkillTree;
import net.elarisrpg.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

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
                ),
                createWarriorSkillTree(),
                new ItemStack(Items.IRON_SWORD)
        ));

        register(new PlayerClass(
                new Identifier("elarisrpg", "elementalist"),
                "Elementalist",
                "A spellcaster who wields elemental forces.",
                List.of(
                        new ItemStack(Registries.ITEM.get(new Identifier("wizards", "staff_fire"))),
                        new ItemStack(Items.LEATHER_HELMET)
                ),
                createElementalistSkillTree(),
                new ItemStack(Registries.ITEM.get(new Identifier("wizards", "staff_fire")))
        ));

        register(new PlayerClass(
                new Identifier("elarisrpg", "ranger"),
                "Ranger",
                "A skilled archer who excels at ranged combat.",
                List.of(
                        new ItemStack(Items.BOW),
                        new ItemStack(Items.LEATHER_CHESTPLATE),
                        new ItemStack(Items.ARROW, 16)
                ),
                createRangerSkillTree(),
                new ItemStack(Items.BOW)
        ));

        register(new PlayerClass(
                new Identifier("elarisrpg", "rogue"),
                "Rogue",
                "A stealthy fighter who strikes swiftly and disappears.",
                List.of(
                        new ItemStack(Items.STONE_SWORD),
                        new ItemStack(Items.LEATHER_BOOTS)
                ),
                createRougeSkillTree(),
                new ItemStack(Items.LEATHER_BOOTS)
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

    private static SkillTree createWarriorSkillTree() {
        List<SkillRow> rows = new ArrayList<>();
        for (int r = 1; r <= 4; r++) {
            List<Skill> skills = new ArrayList<>();
            for (int s = 1; s <= 5; s++) {
                skills.add(new Skill(
                        "Warrior Skill " + s,
                        "Warrior-specific description for skill " + s + " in row " + r,
                        5
                ));
            }
            rows.add(new SkillRow(skills));
        }
        return new SkillTree(rows);
    }

    private static SkillTree createRangerSkillTree() {
        List<SkillRow> rows = new ArrayList<>();
        for (int r = 1; r <= 4; r++) {
            List<Skill> skills = new ArrayList<>();
            for (int s = 1; s <= 5; s++) {
                skills.add(new Skill(
                        "Ranger Skill " + s,
                        "Ranger-specific description for skill " + s + " in row " + r,
                        5
                ));
            }
            rows.add(new SkillRow(skills));
        }
        return new SkillTree(rows);
    }

    private static SkillTree createElementalistSkillTree() {
        List<SkillRow> rows = new ArrayList<>();
        for (int r = 1; r <= 4; r++) {
            List<Skill> skills = new ArrayList<>();
            for (int s = 1; s <= 5; s++) {
                skills.add(new Skill(
                        "Elementalist Skill " + s,
                        "Elementalist-specific description for skill " + s + " in row " + r,
                        5
                ));
            }
            rows.add(new SkillRow(skills));
        }
        return new SkillTree(rows);
    }

    private static SkillTree createRougeSkillTree() {
        List<SkillRow> rows = new ArrayList<>();
        for (int r = 1; r <= 4; r++) {
            List<Skill> skills = new ArrayList<>();
            for (int s = 1; s <= 5; s++) {
                skills.add(new Skill(
                        "Rouge Skill " + s,
                        "Rouge-specific description for skill " + s + " in row " + r,
                        5
                ));
            }
            rows.add(new SkillRow(skills));
        }
        return new SkillTree(rows);
    }
}
