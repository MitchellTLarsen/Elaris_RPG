package net.elarisrpg.client;

import net.elarisrpg.ElarisNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class DialogueManager {

    private static VillagerEntity currentVillager = null;

    public static void startDialogue(VillagerEntity villager) {
        currentVillager = villager;
        if (MinecraftClient.getInstance().player != null) {
            ElarisNetworking.sendVillagerAiDisablePacket(currentVillager.getId());
        }
    }

    public static void endDialogue() {
        if (currentVillager != null) {
            if (MinecraftClient.getInstance().player != null) {
                ElarisNetworking.sendVillagerAiEnablePacket(currentVillager.getId());
            }
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
