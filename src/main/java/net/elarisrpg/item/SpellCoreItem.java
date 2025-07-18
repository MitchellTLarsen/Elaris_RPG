package net.elarisrpg.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class SpellCoreItem extends TrinketItem implements Trinket {
    public SpellCoreItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            System.out.println("[SpellCoreItem] Equipped core: " + stack.getItem());
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            System.out.println("[SpellCoreItem] Unequipped core: " + stack.getItem());
        }
    }

    @Override
    public boolean isFireproof() {
        return true;
    }
}

