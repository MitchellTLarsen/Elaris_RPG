package net.elarisrpg.client.render.lootbeams;

import net.minecraft.util.math.Vec3d;

public class LootBeam {
    public final Vec3d position;
    public final LootRarity rarity;

    public LootBeam(Vec3d pos, LootRarity rarity) {
        this.position = pos;
        this.rarity = rarity;
    }
}