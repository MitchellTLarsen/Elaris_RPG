package net.elarisrpg.client;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class DialogueManager {

    private static VillagerEntity currentVillager = null;

    public static void startDialogue(VillagerEntity villager, PlayerEntity player) {
        currentVillager = villager;
        villager.setAiDisabled(true);
        villager.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, player.getEyePos());
    }

    public static void endDialogue() {
        if (currentVillager != null) {
            currentVillager.setAiDisabled(false);
            currentVillager = null;
        }
    }

    public static VillagerEntity getActiveVillager() {
        return currentVillager;
    }

    public static boolean isDialogueOpen() {
        return currentVillager != null;
    }
}
