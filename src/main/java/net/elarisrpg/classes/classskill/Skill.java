package net.elarisrpg.classes.classskill;

import net.minecraft.nbt.NbtCompound;

public class Skill {

    private final String name;
    private final String description;
    private final int maxPoints;

    private int pointsAllocated;

    public Skill(String name, String description, int maxPoints) {
        this.name = name;
        this.description = description;
        this.maxPoints = maxPoints;
        this.pointsAllocated = 0;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getPointsAllocated() {
        return pointsAllocated;
    }

    public void setPointsAllocated(int points) {
        this.pointsAllocated = Math.min(points, maxPoints);
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("Name", name);
        nbt.putInt("PointsAllocated", pointsAllocated);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("PointsAllocated")) {
            this.pointsAllocated = nbt.getInt("PointsAllocated");
        }
    }
}
