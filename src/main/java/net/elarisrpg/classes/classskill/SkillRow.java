package net.elarisrpg.classes.classskill;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.List;

public class SkillRow {

    private final List<Skill> skills;
    private int rowPointsAllocated;

    public SkillRow(List<Skill> skills) {
        this.skills = skills;
        this.rowPointsAllocated = 0;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public int getRowPointsAllocated() {
        return rowPointsAllocated;
    }

    public void setRowPointsAllocated(int rowPointsAllocated) {
        this.rowPointsAllocated = rowPointsAllocated;
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("RowPointsAllocated", rowPointsAllocated);

        NbtList skillList = new NbtList();
        for (Skill skill : skills) {
            skillList.add(skill.writeNbt());
        }
        nbt.put("Skills", skillList);

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("RowPointsAllocated")) {
            this.rowPointsAllocated = nbt.getInt("RowPointsAllocated");
        }

        if (nbt.contains("Skills")) {
            NbtList skillList = nbt.getList("Skills", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < skillList.size(); i++) {
                skills.get(i).readNbt(skillList.getCompound(i));
            }
        }
    }
}
