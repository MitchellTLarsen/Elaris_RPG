package net.elarisrpg.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.Collections;

public class ElarisHud {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            var client = MinecraftClient.getInstance();

            if (client.player == null) return;

            PlayerEntity player = client.player;

            double armor = player.getArmor();

            double attackDamage = 0.0;
            boolean isBow = false;
            String damageStr = "-";

            var mainHandStack = player.getMainHandStack();

            if (!mainHandStack.isEmpty()) {
                if (mainHandStack.isOf(Items.BOW)) {
                    isBow = true;

                    double baseDamage = 4.0;
                    int powerLevel = EnchantmentHelper.getLevel(
                            Enchantments.POWER,
                            mainHandStack
                    );
                    double bonus = powerLevel > 0
                            ? (0.5 + powerLevel * 0.5)
                            : 0.0;

                    float minPull = BowItem.getPullProgress(2);     // simulate quick tap
                    float maxPull = BowItem.getPullProgress(20);    // full draw

                    double minDamage = (baseDamage + bonus) * minPull;
                    double maxDamage = (baseDamage + bonus) * maxPull;

                    damageStr = String.format("%.1f - %.1f", minDamage, maxDamage);

                } else {
                    // Melee weapon logic
                    var attributeModifiers = mainHandStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
                    var modifier = attributeModifiers.get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                    if (!modifier.isEmpty()) {
                        attackDamage = modifier.stream()
                                .mapToDouble(EntityAttributeModifier::getValue)
                                .sum();

                        damageStr = String.format("%.1f", attackDamage);
                    }
                }
            }

            int boxX = 10;
            int boxY = 10;

            int textX = boxX + 5;
            int titleY = boxY + 2;
            int iconX = boxX + 5;
            int iconY = boxY + 14;
            int lineSpacing = 16;

            // Title inside box
            drawContext.drawText(
                    client.textRenderer,
                    Text.literal("Stats"),
                    textX,
                    titleY,
                    0xFFFFFF,
                    false
            );

            // Chestplate icon
            var chestplateStack = new ItemStack(Items.NETHERITE_CHESTPLATE);
            drawContext.drawItem(chestplateStack, iconX, iconY);

            drawContext.drawText(
                    client.textRenderer,
                    Text.literal(String.valueOf(armor)),
                    iconX + 20,
                    iconY + 4,
                    0x00FF00,
                    false
            );

            // Pick icon based on held item
            ItemStack iconStack = isBow
                    ? new ItemStack(Items.BOW)
                    : new ItemStack(Items.NETHERITE_SWORD);

            drawContext.drawItem(iconStack, iconX, iconY + lineSpacing);

            drawContext.drawText(
                    client.textRenderer,
                    Text.literal(damageStr),
                    iconX + 20,
                    iconY + lineSpacing + 4,
                    0xFFAA00,
                    false
            );
        });
    }
}
