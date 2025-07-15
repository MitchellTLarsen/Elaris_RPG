package net.elarisrpg.client.render;

import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class DamagePopup {
    public Text text;
    public Vec3d position;
    public float age = 0;
    public float maxLifetime = 500; // ticks
    public int color;

    public DamagePopup(Text text, Vec3d position, int color) {
        this.text = text;
        this.position = position;
        this.color = color;
    }

    public void tick() {
        age++;
        // Move upward over time
        this.position = this.position.add(0, 0.01, 0);
    }

    public boolean isExpired() {
        return age >= maxLifetime;
    }

    public float getAlpha() {
        float progress = (float) age / maxLifetime;
        return (float)Math.pow(1.0f - progress, 0.5f);
    }
}
