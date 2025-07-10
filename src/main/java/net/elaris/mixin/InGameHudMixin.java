package net.elaris.mixin;

// Import Minecraft classes for HUD rendering
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;

// Import SpongePowered Mixin classes
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin modifies the InGameHud class.
 *
 * In Minecraft, InGameHud is responsible for rendering:
 * - the vanilla hotbar
 * - health bar
 * - armor icons
 * - crosshair
 * - XP bar
 * - and other HUD overlays
 *
 * We're using this mixin to **cancel the vanilla XP bar** entirely,
 * so we can replace it with a custom one instead.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {

    /**
     * Inject into the method:
     *
     *    public void renderExperienceBar(DrawContext context, int x)
     *
     * This method normally draws:
     * - the vanilla green XP bar above the hotbar
     * - the player's level number
     *
     * We inject at "HEAD" meaning:
     * → right at the beginning of the vanilla method.
     *
     * We mark this injection as **cancellable.**
     * That allows us to completely stop the vanilla code from running.
     */
    @Inject(
            method = "renderExperienceBar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelXpBar(DrawContext context, int x, CallbackInfo ci) {
        // This cancels the vanilla XP bar rendering entirely.
        //
        // Without this, Minecraft would draw:
        // - the default XP bar background
        // - the filled green XP bar
        // - the player's level number
        //
        // We want our custom mod to render its own XP bar,
        // so we prevent the vanilla one from showing.
        ci.cancel();
    }
}
