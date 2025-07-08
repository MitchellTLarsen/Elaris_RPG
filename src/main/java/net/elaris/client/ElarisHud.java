package net.elaris.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class ElarisHud {

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            var client = MinecraftClient.getInstance();

            if (client.player == null) return;

            PlayerEntity player = client.player;

            double armor = player.getArmor();

            double attackDamage = 0.0;
            var mainHandStack = player.getMainHandStack();
            if (!mainHandStack.isEmpty()) {
                var attributeModifiers = mainHandStack.getAttributeModifiers(EquipmentSlot.MAINHAND);
                var modifier = attributeModifiers.get(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                if (!modifier.isEmpty()) {
                    attackDamage = modifier.stream()
                            .mapToDouble(EntityAttributeModifier::getValue)
                            .sum();
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

            // Sword icon
            var swordStack = new ItemStack(Items.NETHERITE_SWORD);
            drawContext.drawItem(swordStack, iconX, iconY + lineSpacing);

            drawContext.drawText(
                    client.textRenderer,
                    Text.literal(String.valueOf(attackDamage)),
                    iconX + 20,
                    iconY + lineSpacing + 4,
                    0xFFAA00,
                    false
            );
        });
    }
}
