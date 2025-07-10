package net.elarisrpg;

import net.elarisrpg.command.ResetClassCommand;
import net.elarisrpg.command.ResetLevelCommand;
import net.elarisrpg.command.SetClassCommand;
import net.elarisrpg.command.SetLevelCommand;
import net.elarisrpg.data.LevelData;
import net.elarisrpg.data.PlayerData;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElarisRPG implements ModInitializer {
	public static final String MOD_ID = "elarisrpg";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ElarisRPG.LOGGER.info("Elaris RPG loaded!");
		
		CommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess, environment) -> {
					ResetLevelCommand.register(dispatcher);
					ResetClassCommand.register(dispatcher);
					SetLevelCommand.register(dispatcher);
					SetClassCommand.register(dispatcher);
				}
		);

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			if (entity instanceof PlayerEntity player) {
				LevelData levelData = PlayerData.get(player).getLevelData();
				levelData.addXp(player, 20);
			}
		});

	}
}