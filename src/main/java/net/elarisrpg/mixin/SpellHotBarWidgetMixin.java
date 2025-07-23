package net.elarisrpg.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.client.gui.Drawable;
import net.spell_engine.client.gui.HudKeyVisuals;
import net.spell_engine.client.gui.HudRenderHelper;
import net.spell_engine.internals.SpellRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Mixin(HudRenderHelper.SpellHotBarWidget.class)
public class SpellHotBarWidgetMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void spellhudaddon$forceSimpleHud(DrawContext context, int screenWidth, int screenHeight, HudRenderHelper.SpellHotBarWidget.ViewModel viewModel, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // ⬇ Inline: Collect spells from items
        Set<Identifier> spellIds = new LinkedHashSet<>();

        // 1. Check trinket slots
        TrinketsApi.getTrinketComponent(client.player).ifPresent(component -> {
            component.getAllEquipped().forEach(pair -> {
                ItemStack stack = pair.getRight();
                extractSpellIdsFromItem(stack, spellIds);
            });
        });

        // 2. Check hands
        extractSpellIdsFromItem(client.player.getMainHandStack(), spellIds);
        extractSpellIdsFromItem(client.player.getOffHandStack(), spellIds);

        // 3. Check armor
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                extractSpellIdsFromItem(client.player.getEquippedStack(slot), spellIds);
            }
        }

        // ⬇ Now render using assigned spells
        List<HudRenderHelper.SpellHotBarWidget.SpellViewModel> spells = viewModel.spells();
        if (spells.isEmpty()) return;

        renderSimpleHorizontalHud(context, screenWidth, screenHeight, spells);
        ci.cancel();
    }

    private static void extractSpellIdsFromItem(ItemStack stack, Set<Identifier> spells) {
        if (stack.isEmpty()) return;

        NbtCompound tag = stack.getNbt();
        if (tag == null || !tag.contains("spell_ids", NbtList.STRING_TYPE)) return;

        NbtList list = tag.getList("spell_ids", NbtList.STRING_TYPE);
        for (int i = 0; i < list.size(); i++) {
            Identifier id = Identifier.tryParse(list.getString(i));
            if (id != null) {
                spells.add(id);
            }
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static void drawKeybinding(DrawContext context, TextRenderer textRenderer,
                                       HudRenderHelper.SpellHotBarWidget.KeyBindingViewModel keybinding,
                                       int x, int y,
                                       Drawable.Anchor horizontalAnchor, Drawable.Anchor verticalAnchor) {
        if (keybinding.drawable() != null) {
            keybinding.drawable().draw(context, x, y, horizontalAnchor, verticalAnchor);
        } else {
            String label = keybinding.label();
            int width = textRenderer.getWidth(label);
            int xOffset = switch (horizontalAnchor) {
                case LEADING -> width / 2;
                case TRAILING -> -width / 2;
                case CENTER -> 0;
            };
            x += xOffset;

            HudKeyVisuals.buttonLeading.draw(context, x - (width / 2), y, Drawable.Anchor.TRAILING, verticalAnchor);
            HudKeyVisuals.buttonCenter.drawFlexibleWidth(context, x - (width / 2), y, width, verticalAnchor);
            HudKeyVisuals.buttonTrailing.draw(context, x + (width / 2), y, Drawable.Anchor.LEADING, verticalAnchor);
            context.drawCenteredTextWithShadow(textRenderer, label, x, y - 10, 0xFFFFFF);
        }
    }


    @Unique
    private static void renderSimpleHorizontalHud(DrawContext context, int screenWidth, int screenHeight, List<HudRenderHelper.SpellHotBarWidget.SpellViewModel> spells) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
        int slotWidth = 21;
        int slotHeight = 22;
        int iconSize = 16;

        int spacingBetweenGroups = 10;
        int spacingWithinGroup = 0;

        // Compute group widths
        int groupWidth = 3 * slotWidth + 2 * spacingWithinGroup;
        int middleSlotWidth = slotWidth;
        int totalWidth = groupWidth * 2 + middleSlotWidth + spacingBetweenGroups * 2;

        int baseX = (screenWidth - totalWidth) / 2;
        int baseY = screenHeight - 50;

        int[] slotXs = new int[7];
        for (int i = 0; i < 7; i++) {
            if (i < 3) {
                slotXs[i] = baseX + i * (slotWidth + spacingWithinGroup);
            } else if (i == 3) {
                slotXs[i] = baseX + groupWidth + spacingBetweenGroups;
            } else {
                slotXs[i] = baseX + groupWidth + spacingBetweenGroups + middleSlotWidth + spacingBetweenGroups
                        + (i - 4) * (slotWidth + spacingWithinGroup);
            }
        }

        for (int i = 0; i < Math.min(spells.size(), 7); i++) {
            HudRenderHelper.SpellHotBarWidget.SpellViewModel spell = spells.get(i);
            int x = slotXs[i];
            int y = baseY;

            // Styled background
            context.drawTexture(WIDGETS_TEXTURE, x, y, 0, 0, slotWidth, slotHeight);

            // Spell icon
            int iconX = x + (slotWidth - iconSize) / 2;
            int iconY = y + 3;
            if (spell.iconId() != null) {
                context.drawTexture(spell.iconId(), iconX + 1, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
            } else if (spell.itemStack() != null) {
                context.drawItem(spell.itemStack(), iconX, iconY);
            }

            // Cooldown
            if (spell.cooldown() > 0) {
                int cooldownTop = iconY + (int) (iconSize * (1.0f - spell.cooldown()));
                context.fill(RenderLayer.getGuiOverlay(), iconX, cooldownTop, iconX + iconSize, iconY + iconSize, 0x88000000);
            }

            // Keybinding visual (styled buttons)
            var kb = spell.keybinding();
            var mod = spell.modifier();
            int keyX = x + (slotWidth / 2);
            int keyY = y + slotHeight + 5;

            if (kb != null) {
                context.getMatrices().push();
                context.getMatrices().translate(0, 0, 200);
                if (mod != null) {
                    int spacingBetweenKeys = 1;
                    int modWidth = mod.width(textRenderer);
                    int keyWidth = kb.width(textRenderer);
                    int total = modWidth + keyWidth + spacingBetweenKeys;
                    int left = keyX - (total / 2);

                    drawKeybinding(context, textRenderer, mod, left, keyY, Drawable.Anchor.LEADING, Drawable.Anchor.TRAILING);
                    drawKeybinding(context, textRenderer, kb, left + modWidth + spacingBetweenKeys, keyY, Drawable.Anchor.LEADING, Drawable.Anchor.TRAILING);
                } else {
                    drawKeybinding(context, textRenderer, kb, keyX, keyY, Drawable.Anchor.CENTER, Drawable.Anchor.TRAILING);
                }
                context.getMatrices().pop();
            }
        }

        context.setShaderColor(1F, 1F, 1F, 1F);
    }


}
