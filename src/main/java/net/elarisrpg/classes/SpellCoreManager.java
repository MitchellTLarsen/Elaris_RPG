package net.elarisrpg.classes;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class SpellCoreManager {
    public static void updateSpellCore(ServerPlayerEntity player, int level) {
        System.out.println("[SpellCoreManager] Updating spell core for " + player.getName().getString() + " at level " + level);
        Optional<TrinketComponent> optional = TrinketsApi.getTrinketComponent(player);

        if (optional.isEmpty()) return;

        TrinketComponent component = optional.get();

        // Determine which spell core item to equip
        Identifier coreId = level >= 15
                ? new Identifier("elarisrpg", "spell_core_level_15")
                : level >= 10
                ? new Identifier("elarisrpg", "spell_core_level_10")
                : level >= 5
                ? new Identifier("elarisrpg", "spell_core_level_5")
                : new Identifier("elarisrpg", "spell_core_level_1");

        ItemStack newCore = new ItemStack(Registries.ITEM.get(coreId));

        System.out.println(coreId);

        // Trinkets uses slot group and slot name, e.g., "elarisrpg:spell_core"
        String group = "elarisrpg";
        String slot = "spell_core";

        // Loop through the trinket inventories
        component.getInventory().forEach((groupKey, slotMap) -> {
            System.out.println(groupKey);
            if (!groupKey.equals(group)) return;

            slotMap.forEach((slotKey, inventory) -> {
                if (!slotKey.equals(slot)) return;

                if (inventory.size() > 0) {
                    ItemStack current = inventory.getStack(0);

                    if (!ItemStack.areItemsEqual(current, newCore)) {
                        inventory.setStack(0, newCore.copy());
                        System.out.println("[SpellCore] Equipped spell core for level " + level + ": " + coreId);
                    } else {
                        System.out.println("[SpellCore] Player already has correct core for level " + level);
                    }
                }
            });
        });

        TrinketsApi.TRINKET_COMPONENT.sync(player);
    }
}
