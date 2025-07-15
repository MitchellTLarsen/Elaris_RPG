package net.elarisrpg.client.render.lootbeams;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public class LootBeamManager {
    private static final List<LootBeam> beams = new ArrayList<>();
    private static final Map<Item, LootRarity> ITEM_RARITIES = new HashMap<>();

    static {
        ITEM_RARITIES.put(net.minecraft.item.Items.DIAMOND, LootRarity.LEGENDARY);
        ITEM_RARITIES.put(net.minecraft.item.Items.IRON_INGOT, LootRarity.RARE);
        ITEM_RARITIES.put(net.minecraft.item.Items.EMERALD, LootRarity.UNCOMMON);
    }

    public static LootRarity getRarity(ItemStack stack) {
        return ITEM_RARITIES.getOrDefault(stack.getItem(), LootRarity.COMMON);
    }

    public static int getColorForItem(ItemStack stack) {
        LootRarity rarity = getRarity(stack);
        return rarity.color;
    }

}
