package net.elarisrpg.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private static final Identifier CUSTOM_BARS = new Identifier("elarisrpg", "textures/gui/healthbars.png");

    @Inject(method = "renderStatusBars", at = @At("HEAD"), cancellable = true)
    private void elarisrpg$moveBars(DrawContext context, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        PlayerEntity player = client.player;
        int x = 10;
        int y = 10;

        context.setShaderColor(1f, 1f, 1f, 1f);
        context.drawTexture(CUSTOM_BARS, 0, 0, 0, 0, 1, 1); // ensures the texture is bound
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 200); // make sure it draws above other things

        int barHeight = 5;
        int barSpacing = 7;

        float healthRatio = player.getHealth() / player.getMaxHealth();
        float hungerRatio = player.getHungerManager().getFoodLevel() / 20f;
        float xpRatio = player.experienceProgress;

        // Health
        drawBar(context, x, y, healthRatio, 0);
        y += barHeight + barSpacing;

        // Hunger
        drawBar(context, x, y, hungerRatio, 2);
        y += barHeight + barSpacing;

        // XP
        drawBar(context, x, y, xpRatio, 4);
        y += barHeight + barSpacing;

        context.getMatrices().pop();

        ci.cancel();
    }

    private void drawBar(DrawContext context, int x, int y, float ratio, int barIndex) {
        int barWidth = 64;
        int barHeight = 5;
        int spriteHeight = 5;

        int yOffset = barIndex * spriteHeight;

        // Draw background
        context.drawTexture(CUSTOM_BARS, x, y, 0, yOffset, barWidth, barHeight, 64, 64);

        // Draw fill
        int filled = (int) (barWidth * ratio);
        if (filled > 0) {
            context.drawTexture(CUSTOM_BARS, x, y, 0, yOffset + barHeight, filled, barHeight, 64, 64);
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void elarisrpg$cancelVanillaXpBar(DrawContext context, int x, CallbackInfo ci) {
        ci.cancel();
    }
}
