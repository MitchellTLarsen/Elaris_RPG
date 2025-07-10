package net.elaris.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;

import java.util.*;

public class HitMobTracker {

    private static final Map<Integer, MobHitData> activeMobs = new HashMap<>();
    private static final long TIMEOUT_MS = 5000;

    public static void markMobHit(int entityId) {
        activeMobs.put(entityId, new MobHitData(System.currentTimeMillis()));
    }

    public static Set<Integer> getActiveMobs() {
        return activeMobs.keySet();
    }

    public static void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        long now = System.currentTimeMillis();

        activeMobs.entrySet().removeIf(entry -> {
            int id = entry.getKey();
            MobHitData hitData = entry.getValue();

            var entity = client.world.getEntityById(id);
            if (!(entity instanceof LivingEntity mob)) {
                return true; // mob no longer exists
            }

            if (!mob.isAlive()) return true; // mob is dead

            // Check if mob has lost aggro (only for mobs capable of aggro)
            if (mob instanceof MobEntity mobEntity) {
                boolean lostAggro = mobEntity.getTarget() == null;
                boolean timedOut = (now - hitData.lastHitTime > TIMEOUT_MS);

                // Remove if lost aggro AND it's been long enough
                if (lostAggro && timedOut) {
                    return true;
                }
            } else {
                // For passive mobs: remove only if timed out
                boolean timedOut = (now - hitData.lastHitTime > TIMEOUT_MS);
                if (timedOut) {
                    return true;
                }
            }

            return false;
        });
    }
}
