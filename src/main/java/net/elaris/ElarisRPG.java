package net.elaris;

import net.elaris.command.ResetClassCommand;
import net.elaris.command.ResetLevelCommand;
import net.elaris.command.SetClassCommand;
import net.elaris.command.SetLevelCommand;
import net.elaris.data.LevelData;
import net.elaris.data.PlayerData;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElarisRPG implements ModInitializer {
	public static final String MOD_ID = "elaris";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ElarisRPG.LOGGER.info("Elaris RPG loaded!");

		CommandRegistrationCallback.EVENT.register(
				(dispatcher, registryAccess, environment) -> ResetLevelCommand.register(dispatcher)
		);
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