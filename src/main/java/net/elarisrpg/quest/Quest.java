package net.elarisrpg.quest;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public abstract class Quest {
    protected final Identifier id;
    protected final String title;
    protected final boolean repeatable;

    public Quest(Identifier id, String title, boolean repeatable) {
        this.id = id;
        this.title = title;
        this.repeatable = repeatable;
    }

    public Identifier getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public abstract boolean isComplete(ServerPlayerEntity player);
}
