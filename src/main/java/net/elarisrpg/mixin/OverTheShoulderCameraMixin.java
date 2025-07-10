package net.elarisrpg.mixin;

// Import Minecraft classes
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

// Import Mixin classes
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin modifies the Camera class.
 *
 * The Camera class controls:
 * - the player's viewpoint
 * - position
 * - yaw/pitch
 * - first/third person angles
 *
 * Our goal here:
 * → implement an **over-the-shoulder camera** in third-person view.
 *
 * Similar to Epic Fight Mod:
 * - shifts the camera slightly to the side
 * - so the player model is offset rather than centered
 *
 * This creates a more cinematic feel.
 */
@Mixin(Camera.class)
public class OverTheShoulderCameraMixin {

    /**
     * Inject into the Camera#update method.
     *
     * Vanilla method signature:
     *
     *   public void update(
     *       BlockView area,
     *       Entity focusedEntity,
     *       boolean thirdPerson,
     *       boolean inverseView,
     *       float tickDelta
     *   )
     *
     * We inject at "TAIL":
     * → after the vanilla method has finished positioning the camera normally.
     *
     * This way, we can adjust the vanilla position **without interfering
     * with the normal calculations.**
     */
    @Inject(
            method = "update",
            at = @At("TAIL")
    )
    private void elaris$shiftCamera(
            BlockView area,
            Entity focusedEntity,
            boolean thirdPerson,
            boolean inverseView,
            float tickDelta,
            CallbackInfo ci
    ) {
        // Only run if we're in third-person mode.
        // In first-person, we don't want any offset.
        if (!thirdPerson) return;

        // Only run if the focused entity is a PlayerEntity.
        // We don't want to offset the camera for e.g. mobs.
        if (!(focusedEntity instanceof PlayerEntity player)) return;

        // "this" in a Mixin is a raw Object.
        // We cast it to Camera so we can call methods like getYaw().
        Camera camera = (Camera) (Object) this;

        // Vanilla yaw and pitch, in degrees.
        float yaw = camera.getYaw();
        float pitch = camera.getPitch();

        // Convert pitch and yaw to radians for math functions.
        double pitchRad = Math.toRadians(-pitch);
        double yawRad = Math.toRadians(-yaw);

        /**
         * Calculate the forward vector:
         *
         * Vanilla camera looks "forward" in the direction:
         * - rotated by pitch up/down
         * - rotated by yaw left/right
         *
         * We convert spherical angles to cartesian coordinates:
         *
         * X = sin(yaw) * cos(pitch)
         * Y = sin(pitch)
         * Z = cos(yaw) * cos(pitch)
         */
        double x = Math.sin(yawRad) * Math.cos(pitchRad);
        double y = Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        Vec3d forward = new Vec3d(x, y, z);

        /**
         * To shift the camera sideways, we need a vector pointing "right."
         *
         * Cross product:
         * forward × (0, 1, 0) → right vector
         *
         * This gives a vector perpendicular to forward and up.
         */
        Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();

        // Define how much to shift sideways and vertically.
        double sideOffset = 1;      // move right by 1 block
        double verticalOffset = 0.2; // raise camera slightly above vanilla position

        /**
         * Compute final offset:
         * - move sideways along right vector
         * - add vertical offset upward
         */
        Vec3d offset = right.multiply(sideOffset).add(0, verticalOffset, 0);

        /**
         * Calculate the new camera position:
         *
         * - Start at vanilla camera position.
         * - Add the sideways + vertical offset.
         */
        Vec3d newPos = camera.getPos().add(offset);

        /**
         * Actually apply the new camera position.
         *
         * Note:
         * - Camera.pos is private.
         * - So we use a Mixin accessor (CameraAccessorMixin) to set it.
         *
         * This forcibly changes the camera's x/y/z.
         */
        ((CameraAccessorMixin) camera).elaris$setPos(newPos);
    }
}
