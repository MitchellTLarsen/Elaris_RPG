package net.elarisrpg.client.render;

import net.elarisrpg.util.RaycastUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

public class MobOutlineRenderer {
    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            var client = MinecraftClient.getInstance();
            var player = client.player;

            if (player == null) return;

            var mob = RaycastUtils.findLookedAtMob(player, 16.0);
            if (mob != null) {
                MobOutlineUtils.renderEntityOutline(mob, context.matrixStack(), context.camera().getPos());
            }
        });
    }
}
