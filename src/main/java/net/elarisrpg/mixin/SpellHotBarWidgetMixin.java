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
import net.spell_engine.client.gui.HudRenderHelper;
import net.spell_engine.internals.SpellRegistry;
import org.spongepowered.asm.mixin.Mixin;
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

//        HotbarComponent hotbar = PlayerComponents.HOTBAR.get(client.player);
//        int slot = 0;
//        for (Identifier id : spellIds) {
//            if (slot >= 7) break;
//            if (SpellRegistry.getSpell(id) != null) {
//                hotbar.set(slot, id);
//                slot++;
//            }
//        }

        // ⬇ Now render using assigned spells
        List<HudRenderHelper.SpellHotBarWidget.SpellViewModel> spells = viewModel.spells();
        if (spells.isEmpty()) return;

        renderSimpleHorizontalHud(context, screenWidth, screenHeight, spells);
        //ci.cancel();
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

    @Unique
    private static void renderSimpleHorizontalHud(DrawContext context, int screenWidth, int screenHeight, List<HudRenderHelper.SpellHotBarWidget.SpellViewModel> spells) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        int maxSlots = Math.min(spells.size(), 10);
        int slotSize = 22;
        int iconSize = 16;
        int spacing = 4;

        int totalWidth = maxSlots * (slotSize + spacing) - spacing;
        int baseX = (screenWidth - totalWidth) / 2;
        int baseY = screenHeight - 60; // Above hotbar

        for (int i = 0; i < maxSlots; i++) {
            HudRenderHelper.SpellHotBarWidget.SpellViewModel spell = spells.get(i);
            int x = baseX + i * (slotSize + spacing);
            int y = baseY;

            // Draw simple slot background (semi-transparent gray box)
            context.fill(RenderLayer.getGuiOverlay(), x, y, x + slotSize, y + slotSize, 0x88000000);

            // Draw spell icon or item
            int iconX = x + (slotSize - iconSize) / 2;
            int iconY = y + 3;
            if (spell.iconId() != null) {
                context.drawTexture(spell.iconId(), iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
            } else if (spell.itemStack() != null) {
                context.drawItem(spell.itemStack(), iconX, iconY);
            }

            // Cooldown overlay
            if (spell.cooldown() > 0) {
                int cooldownTop = iconY + (int) (iconSize * (1.0f - spell.cooldown()));
                context.fill(RenderLayer.getGuiOverlay(), iconX, cooldownTop, iconX + iconSize, iconY + iconSize, 0x88000000);
            }

            // Draw keybinding label (if any)
            var kb = spell.keybinding();
            if (kb != null) {
                context.getMatrices().push();
                context.getMatrices().translate(0, 0, 200); // Draw above other UI
                String label = kb.label();
                int labelWidth = textRenderer.getWidth(label);
                int labelX = x + (slotSize - labelWidth) / 2;
                int labelY = y + slotSize - 9;
                context.drawTextWithShadow(textRenderer, label, labelX, labelY, 0xFFFFFF);
                context.getMatrices().pop();
            }
        }

        context.setShaderColor(1F, 1F, 1F, 1F);
    }
}
