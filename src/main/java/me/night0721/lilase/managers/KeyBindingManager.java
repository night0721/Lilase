package me.night0721.lilase.managers;

import me.night0721.lilase.Lilase;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyBindingManager {
    private static final KeyBinding[] keyBindings = new KeyBinding[4];
//    private static final ArrayList<Block> interactables = new ArrayList<>(Arrays.asList(Blocks.acacia_door, Blocks.anvil, Blocks.beacon, Blocks.bed, Blocks.birch_door, Blocks.brewing_stand, Blocks.command_block, Blocks.crafting_table, Blocks.chest, Blocks.dark_oak_door, Blocks.daylight_detector, Blocks.daylight_detector_inverted, Blocks.dispenser, Blocks.dropper, Blocks.enchanting_table, Blocks.ender_chest, Blocks.furnace, Blocks.hopper, Blocks.jungle_door, Blocks.lever, Blocks.noteblock, Blocks.powered_comparator, Blocks.unpowered_comparator, Blocks.powered_repeater, Blocks.unpowered_repeater, Blocks.standing_sign, Blocks.wall_sign, Blocks.trapdoor, Blocks.trapped_chest, Blocks.wooden_button, Blocks.stone_button, Blocks.oak_door, Blocks.skull));

    public static void registerKeyBindings() {
        //keyBindings[0] = new KeyBinding("Ghost Block Bind", Keyboard.KEY_G, Lilase.MOD_NAME);
        keyBindings[0] = new KeyBinding("Auction House", Keyboard.KEY_END, Lilase.MOD_NAME);
        keyBindings[1] = new KeyBinding("Config", Keyboard.KEY_MULTIPLY, Lilase.MOD_NAME);
        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
//        if (keyBindings[0].isKeyDown()) {
//            if (PlayerUtils.mc.objectMouseOver.getBlockPos() == null) return;
//            Block block = PlayerUtils.mc.theWorld.getBlockState(PlayerUtils.mc.objectMouseOver.getBlockPos()).getBlock();
//            if (!interactables.contains(block)) {
//                PlayerUtils.mc.theWorld.setBlockToAir(PlayerUtils.mc.objectMouseOver.getBlockPos());
//            }
//        }
//        if (keyBindings[1].isKeyDown()) {
//            List<String> lines = new ArrayList<>();
//            lines.add("X: " + Math.round(PlayerUtils.mc.thePlayer.posX));
//            lines.add("Y: " + Math.round(PlayerUtils.mc.thePlayer.posY));
//            lines.add("Z: " + Math.round(PlayerUtils.mc.thePlayer.posZ));
//            for (String line : lines) {
//                TextRenderer.drawString(line, 0, 0, 1);
//            }
//        }
        if (keyBindings[0].isPressed()) {
            Lilase.auctionHouse.toggleAuction();
        }
        if (keyBindings[1].isPressed()) {
            Lilase.config.openGui();
        }
    }

}
