package me.night0721.lilase.utils;

import me.night0721.lilase.Lilase;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyBindingManager {
    private final KeyBinding[] keyBindings = new KeyBinding[2];

    public void registerKeyBindings() {
        keyBindings[0] = new KeyBinding("Sniper Toggle", Keyboard.KEY_END, Lilase.MOD_NAME);
        keyBindings[1] = new KeyBinding("Config", Keyboard.KEY_MULTIPLY, Lilase.MOD_NAME);
        for (KeyBinding keyBinding : keyBindings) ClientRegistry.registerKeyBinding(keyBinding);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (keyBindings[0].isPressed()) {
            // TODO: Add sniper interface and use nested switch loop to prevent .toggleAuction(); repeating
            Lilase.cofl.toggleAuction();
        }
        if (keyBindings[1].isPressed()) {
            Lilase.config.openGui();
        }
    }

    public static void rightClick() {
        if (!ReflectionUtils.invoke(Lilase.mc, "func_147121_ag")) {
            ReflectionUtils.invoke(Lilase.mc, "rightClickMouse");
        }
    }

    public static void leftClick() {
        if (!ReflectionUtils.invoke(Lilase.mc, "func_147116_af")) {
            ReflectionUtils.invoke(Lilase.mc, "clickMouse");
        }
    }


    public static void updateKeys(boolean forward, boolean back, boolean right, boolean left, boolean attack) {
        updateKeys(forward, back, right, left, attack, false, false);
    }

    public static void updateKeys(boolean forward, boolean back, boolean right, boolean left, boolean attack, boolean crouch, boolean space) {
        if (Lilase.mc.currentScreen != null) {
            stopMovement();
            return;
        }
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindForward.getKeyCode(), forward);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindBack.getKeyCode(), back);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindRight.getKeyCode(), right);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindLeft.getKeyCode(), left);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindAttack.getKeyCode(), attack);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindSneak.getKeyCode(), crouch);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindJump.getKeyCode(), space);

        // TODO: Will fix it. Just give time.
    }

    public static void stopMovement() {
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindAttack.getKeyCode(), false);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        KeyBinding.setKeyBindState(Lilase.mc.gameSettings.keyBindJump.getKeyCode(), false);
    }

}