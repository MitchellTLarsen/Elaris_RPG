package net.elarisrpg.item;

import net.elarisrpg.ElarisRPG;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SPELL_CORE_1 = registerItem("spell_core_level_1",
            new SpellCoreItem(new FabricItemSettings().maxCount(1)));
    public static final Item SPELL_CORE_5 = registerItem("spell_core_level_5",
            new SpellCoreItem(new FabricItemSettings().maxCount(1)));
    public static final Item SPELL_CORE_10 = registerItem("spell_core_level_10",
            new SpellCoreItem(new FabricItemSettings().maxCount(1)));
    public static final Item SPELL_CORE_15 = registerItem("spell_core_level_15",
            new SpellCoreItem(new FabricItemSettings().maxCount(1)));

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ElarisRPG.MOD_ID, name), item);
    }

    public static void registerModItems() {
        ElarisRPG.LOGGER.info("Registering Mod Items for " + ElarisRPG.MOD_ID);
    }

}
