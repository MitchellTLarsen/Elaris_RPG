package net.elarisrpg.client.render.lootbeams;

public enum LootRarity {
    COMMON(0xFFFFFF),      // White
    UNCOMMON(0x00FF00),    // Green
    RARE(0x00AAFF),        // Blue
    LEGENDARY(0xFF6600);   // Orange

    public final int color;

    LootRarity(int color) {
        this.color = color;
    }
}
