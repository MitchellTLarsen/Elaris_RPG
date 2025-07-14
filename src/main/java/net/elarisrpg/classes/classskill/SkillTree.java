package net.elarisrpg.classes.classskill;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.List;

public class SkillTree {

    private final List<SkillRow> rows;

    public SkillTree(List<SkillRow> rows) {
        this.rows = rows;
    }

    public List<SkillRow> getRows() {
        return rows;
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();

        NbtList rowList = new NbtList();
        for (SkillRow row : rows) {
            rowList.add(row.writeNbt());
        }
        nbt.put("Rows", rowList);

        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("Rows")) {
            NbtList rowList = nbt.getList("Rows", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < rowList.size(); i++) {
                rows.get(i).readNbt(rowList.getCompound(i));
            }
        }
    }
}
