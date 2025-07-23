package net.elarisrpg.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.SpellInfo;
import net.spell_engine.client.gui.HudRenderHelper;
import net.spell_engine.client.input.SpellHotbar;
import net.spell_engine.client.util.SpellRender;
import net.spell_engine.internals.SpellCooldownManager;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(value = HudRenderHelper.class, remap = false)
public class HudRenderHelperMixin {
    @Unique
    private static final Identifier CASTBAR_TEXTURE = new Identifier("spell_engine", "textures/hud/castbar.png");

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;FZ)V", at = @At("HEAD"), cancellable = true)
    private static void overrideHud(DrawContext context, float tickDelta, boolean config, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!(client.player instanceof SpellCasterClient caster)) return;

        ci.cancel();  // Cancel default rendering

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // === Render Casting Bar ===
        SpellCast.Progress castProgress = caster.getSpellCastProgress();
        if (castProgress != null) {
            Identifier icon = SpellRender.iconTexture(castProgress.process().id());
            float progress = castProgress.ratio();
            float barWidth = 182;
            float filled = progress * barWidth;
            int barX = (screenWidth - (int) barWidth) / 2;
            int barY = screenHeight - 60;

            RenderSystem.enableBlend();
            context.setShaderColor(1f, 1f, 1f, 1f);
            context.drawTexture(CASTBAR_TEXTURE, barX, barY, 0, 0, (int) barWidth, 5, 182, 10);
            context.drawTexture(CASTBAR_TEXTURE, barX, barY, 0, 5, (int) filled, 5, 182, 10);
            context.drawTexture(icon, barX - 20, barY - 6, 0f, 0f, 16, 16, 16, 16);
            RenderSystem.disableBlend();
        }

        // === Render Spell Hotbar ===
        if (!SpellHotbar.INSTANCE.slots.isEmpty()) {
            SpellCooldownManager cooldownManager = caster.getCooldownManager();
            List<HudRenderHelper.SpellHotBarWidget.SpellViewModel> spells = SpellHotbar.INSTANCE.slots.stream().map(slot -> {
                SpellInfo info = slot.spell();
                boolean useItem = info.spell().item_use.shows_item_as_icon;
                return new HudRenderHelper.SpellHotBarWidget.SpellViewModel(
                        useItem ? null : SpellRender.iconTexture(info.id()),
                        useItem ? SpellHotbar.expectedUseStack(client.player) : null,
                        cooldownManager.getCooldownProgress(info.id(), tickDelta),
                        HudRenderHelper.SpellHotBarWidget.KeyBindingViewModel.from(slot.getKeyBinding(client.options)),
                        slot.modifier() != null ? HudRenderHelper.SpellHotBarWidget.KeyBindingViewModel.from(slot.modifier()) : null
                );
            }).toList();

            HudRenderHelper.SpellHotBarWidget.ViewModel hotbarViewModel = new HudRenderHelper.SpellHotBarWidget.ViewModel(spells);
            HudRenderHelper.SpellHotBarWidget.render(context, screenWidth, screenHeight, hotbarViewModel);
        }
    }
}