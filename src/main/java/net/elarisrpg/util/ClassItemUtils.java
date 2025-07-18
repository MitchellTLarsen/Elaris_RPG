package net.elarisrpg.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ClassItemUtils {

    private static final String CLASS_ITEM_TAG = "ElarisClassItem";

    /**
     * Removes all class-given slots from the player, including:
     * - Main inventory
     * - Armor slots
     * - Offhand
     * - Personal 2x2 crafting grid
     */
    public static void removeClassItems(PlayerEntity player) {
        // Main + hotbar
        player.getInventory().remove(
                ClassItemUtils::isClassItem,
                Integer.MAX_VALUE,
                player.playerScreenHandler.getCraftingInput()

        );

        // Armor slots
        for (int slot = 0; slot < player.getInventory().armor.size(); slot++) {
            ItemStack stack = player.getInventory().armor.get(slot);
            if (isClassItem(stack)) {
                player.getInventory().armor.set(slot, ItemStack.EMPTY);
            }
        }

        // Offhand
        ItemStack offhand = player.getInventory().offHand.get(0);
        if (isClassItem(offhand)) {
            player.getInventory().offHand.set(0, ItemStack.EMPTY);
        }

        // Personal crafting grid (2x2)
        Inventory craftingInv = player.playerScreenHandler.getCraftingInput();
        for (int i = 0; i < craftingInv.size(); i++) {
            ItemStack stack = craftingInv.getStack(i);
            if (isClassItem(stack)) {
                craftingInv.setStack(i, ItemStack.EMPTY);
            }
        }
    }

    /**
     * Tags an ItemStack as a class-given item.
     */
    public static ItemStack tagAsClassItem(ItemStack stack) {
        stack.getOrCreateNbt().putBoolean(CLASS_ITEM_TAG, true);
        return stack;
    }

    /**
     * Checks whether an ItemStack is tagged as a class-given item.
     */
    public static boolean isClassItem(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().getBoolean(CLASS_ITEM_TAG);
    }
}
