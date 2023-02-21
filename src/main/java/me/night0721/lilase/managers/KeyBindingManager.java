package me.night0721.lilase.managers;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.utils.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

public class KeyBindingManager {
    private static final KeyBinding[] keyBindings = new KeyBinding[4];
    private static final ArrayList<Block> interactables = new ArrayList<>(Arrays.asList(Blocks.acacia_door, Blocks.anvil, Blocks.beacon, Blocks.bed, Blocks.birch_door, Blocks.brewing_stand, Blocks.command_block, Blocks.crafting_table, Blocks.chest, Blocks.dark_oak_door, Blocks.daylight_detector, Blocks.daylight_detector_inverted, Blocks.dispenser, Blocks.dropper, Blocks.enchanting_table, Blocks.ender_chest, Blocks.furnace, Blocks.hopper, Blocks.jungle_door, Blocks.lever, Blocks.noteblock, Blocks.powered_comparator, Blocks.unpowered_comparator, Blocks.powered_repeater, Blocks.unpowered_repeater, Blocks.standing_sign, Blocks.wall_sign, Blocks.trapdoor, Blocks.trapped_chest, Blocks.wooden_button, Blocks.stone_button, Blocks.oak_door, Blocks.skull));

    public static void registerKeyBindings() {
        keyBindings[0] = new KeyBinding("Ghost Block Bind", Keyboard.KEY_G, Lilase.MOD_NAME);
        keyBindings[1] = new KeyBinding("Hub", Keyboard.KEY_DIVIDE, Lilase.MOD_NAME);
        keyBindings[2] = new KeyBinding("Auction House", Keyboard.KEY_END, Lilase.MOD_NAME);
        keyBindings[3] = new KeyBinding("Config", Keyboard.KEY_MULTIPLY, Lilase.MOD_NAME);
        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (keyBindings[0].isKeyDown()) {
            if (PlayerUtils.mc.objectMouseOver.getBlockPos() == null) return;
            Block block = PlayerUtils.mc.theWorld.getBlockState(PlayerUtils.mc.objectMouseOver.getBlockPos()).getBlock();
            if (!interactables.contains(block)) {
                PlayerUtils.mc.theWorld.setBlockToAir(PlayerUtils.mc.objectMouseOver.getBlockPos());
            }
        }
        if (keyBindings[1].isKeyDown()) {
            PlayerUtils.mc.thePlayer.sendChatMessage("/hub");
        }
        if (keyBindings[2].isPressed()) {
            Lilase.auctionHouse.toggleAuction();
        }
        if (keyBindings[3].isPressed()) {
            Lilase.config.openGui();
        }
    }
}
