package net.elaris.mixin;

// Import Minecraft classes
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

// Import Mixin annotations
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * This mixin creates an **Accessor interface** for Minecraft's Camera class.
 *
 * In Minecraft:
 *   - The Camera class stores info about:
 *       - the player's viewpoint
 *       - camera position (x, y, z)
 *       - pitch/yaw
 *       - whether we're in 3rd person, etc.
 *
 * Unfortunately:
 *   - Camera's position field (`pos`) is private.
 *   - Vanilla Minecraft doesn't provide a public setter for it.
 *
 * But sometimes we want to:
 *   - override the camera's position for custom views
 *   - implement things like "over-the-shoulder" perspectives
 *   - or custom cutscenes
 *
 * So we use a mixin accessor to get around Minecraft's encapsulation.
 */
@Mixin(Camera.class)
public interface CameraAccessorMixin {

    /**
     * Expose the private field:
     *
     *     private Vec3d pos;
     *
     * as a public setter.
     *
     * The Mixin annotation:
     *
     *     @Accessor("pos")
     *
     * tells Fabric:
     *   → "create a synthetic method that lets me write to the private pos field."
     *
     * We're naming our new method:
     *   elaris$setPos(...)
     *
     * This avoids potential name collisions with other mods.
     *
     * Now, anywhere in our mod, we can cast:
     *
     *     (CameraAccessorMixin) camera
     *
     * and call:
     *
     *     elaris$setPos(...)
     *
     * to forcibly move the camera to a new position.
     */
    @Accessor("pos")
    void elaris$setPos(Vec3d pos);
}
