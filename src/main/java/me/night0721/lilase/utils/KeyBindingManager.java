package me.night0721.lilase.utils;

import me.night0721.lilase.Lilase;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyBindingManager {
    private static final KeyBinding[] keyBindings = new KeyBinding[4];

    public static void registerKeyBindings() {
        keyBindings[0] = new KeyBinding("Auction House Toggle", Keyboard.KEY_END, Lilase.MOD_NAME);
        keyBindings[1] = new KeyBinding("Config", Keyboard.KEY_MULTIPLY, Lilase.MOD_NAME);
        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (keyBindings[0].isPressed()) {
            Lilase.auctionHouse.toggleAuction();
        }
        if (keyBindings[1].isPressed()) {
            Lilase.config.openGui();
        }
    }

    public static void rightClick() {
        if (!ReflectionUtils.invoke(PlayerUtils.mc, "func_147121_ag")) {
            ReflectionUtils.invoke(PlayerUtils.mc, "rightClickMouse");
        }
    }

    public static void leftClick() {
        if (!ReflectionUtils.invoke(PlayerUtils.mc, "func_147116_af")) {
            ReflectionUtils.invoke(PlayerUtils.mc, "clickMouse");
        }
    }

    public static void middleClick() {
        if (!ReflectionUtils.invoke(PlayerUtils.mc, "func_147112_ai")) {
            ReflectionUtils.invoke(PlayerUtils.mc, "middleClickMouse");
        }
    }

    public static void updateKeys(boolean forward, boolean back, boolean right, boolean left, boolean attack) {
        updateKeys(forward, back, right, left, attack, false, false);
    }

    public static void updateKeys(boolean forward, boolean back, boolean right, boolean left, boolean attack, boolean crouch, boolean space) {
        if (PlayerUtils.mc.currentScreen != null) {
            stopMovement();
            return;
        }
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindForward.getKeyCode(), forward);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindBack.getKeyCode(), back);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindRight.getKeyCode(), right);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindLeft.getKeyCode(), left);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindAttack.getKeyCode(), attack);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindSneak.getKeyCode(), crouch);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindJump.getKeyCode(), space);
    }

    public static void stopMovement() {
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindAttack.getKeyCode(), false);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        KeyBinding.setKeyBindState(PlayerUtils.mc.gameSettings.keyBindJump.getKeyCode(), false);
    }

}
