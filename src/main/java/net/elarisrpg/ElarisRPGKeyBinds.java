package net.elarisrpg;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ElarisRPGKeyBinds {

    public static KeyBinding OPEN_LEVEL_SCREEN;

    public static void register() {
        OPEN_LEVEL_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.elarisrpg.open_level_screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.elarisrpg"
        ));
    }
}