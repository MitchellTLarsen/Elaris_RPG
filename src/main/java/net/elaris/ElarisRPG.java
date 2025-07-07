package net.elaris;

import net.elaris.command.ResetLevelCommand;
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
				(dispatcher, registryAccess, environment) -> {
					ResetLevelCommand.register(dispatcher);
				}
		);

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			if (entity instanceof PlayerEntity player) {
				LevelInfo info = PlayerData.get(player);
				info.addXp(20);
//				player.sendMessage(
//						Text.literal("You gained 20 XP. Level: " + info.getLevel()),
//						false
//				);
			}
		});
	}
}