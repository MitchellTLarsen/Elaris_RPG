package net.elarisrpg.mixin;
import net.elarisrpg.ElarisNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerInteractMixin {

    @Inject(
            method = "interactMob",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onPlayerInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!player.getWorld().isClient) {
            ElarisNetworking.lastVillagerInteracted = (VillagerEntity)(Object)this; // This cast works because we're in a VillagerEntity mixin
            ElarisNetworking.sendOpenDialogPacket(
                    (ServerPlayerEntity) player,
                    "Greetings, traveler!",
                    "Would you like to trade with me?"
            );
        }
        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
