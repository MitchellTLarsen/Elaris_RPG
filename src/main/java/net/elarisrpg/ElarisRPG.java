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
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
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

		ServerPlayNetworking.registerGlobalReceiver(
				ElarisNetworking.CHOOSE_CLASS_PACKET,
				(server, player, handler, buf, responseSender) -> {
					String chosenClass = buf.readString();

					server.execute(() -> {
						var data = PlayerData.get(player);
						data.getClassData().setPlayerClass(chosenClass);

						// Grant items server-side
						var playerClass = net.elarisrpg.classes.ElarisClasses.getByName(chosenClass);
						if (playerClass != null) {
							for (ItemStack original : playerClass.getStartingItems()) {
								ItemStack copy = original.copy();
								copy.getOrCreateNbt().putBoolean("ElarisClassItem", true);
								player.getInventory().insertStack(copy);
							}
						}

						// Send sync packet back to client
						PacketByteBuf syncBuf = PacketByteBufs.create();
						syncBuf.writeInt(data.getLevelData().getLevel());
						syncBuf.writeInt(data.getLevelData().getXp());
						syncBuf.writeString(data.getClassData().getPlayerClass());

						ServerPlayNetworking.send(player, ElarisNetworking.PLAYER_DATA_SYNC_PACKET, syncBuf);

						player.playerScreenHandler.sendContentUpdates();
					});
				}
		);

	}
}