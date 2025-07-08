package net.elaris.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class OverTheShoulderCameraMixin {

    @Inject(
            method = "update",
            at = @At("TAIL")
    )
    private void elaris$shiftCamera(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!thirdPerson) return;
        if (!(focusedEntity instanceof PlayerEntity player)) return;

        Camera camera = (Camera) (Object) this;

        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        double pitchRad = Math.toRadians(-pitch);
        double yawRad = Math.toRadians(-yaw);

        double x = Math.sin(yawRad) * Math.cos(pitchRad);
        double y = Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        Vec3d forward = new Vec3d(x, y, z);
        Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();

        double sideOffset = 1;
        double verticalOffset = 0.2;

        Vec3d offset = right.multiply(sideOffset).add(0, verticalOffset, 0);

        Vec3d newPos = camera.getPos().add(offset);

        ((CameraAccessorMixin) camera).elaris$setPos(newPos);
    }
}
