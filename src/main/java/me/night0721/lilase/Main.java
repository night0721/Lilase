package me.night0721.lilase;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.night0721.lilase.events.PacketReceivedEvent;
import me.night0721.lilase.utils.Utils;
import me.night0721.lilase.utils.ah.AuctionHouse;
import me.night0721.lilase.utils.ah.States;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

@Mod(modid = Main.MODID, name = Main.MOD_NAME, version = Main.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class Main {

    public static final String MOD_NAME = "Lilase";
    public static final String MODID = "Lilase";
    public static final String VERSION = "1.0.0";

    static int tickAmount;
    static long lastAction;
    public static States clickState;
    static Minecraft mc = Minecraft.getMinecraft();
    static KeyBinding[] keyBindings = new KeyBinding[3];
    static ArrayList<Block> interactables = new ArrayList<>(Arrays.asList(Blocks.acacia_door, Blocks.anvil, Blocks.beacon, Blocks.bed, Blocks.birch_door, Blocks.brewing_stand, Blocks.command_block, Blocks.crafting_table, Blocks.chest, Blocks.dark_oak_door, Blocks.daylight_detector, Blocks.daylight_detector_inverted, Blocks.dispenser, Blocks.dropper, Blocks.enchanting_table, Blocks.ender_chest, Blocks.furnace, Blocks.hopper, Blocks.jungle_door, Blocks.lever, Blocks.noteblock, Blocks.powered_comparator, Blocks.unpowered_comparator, Blocks.powered_repeater, Blocks.unpowered_repeater, Blocks.standing_sign, Blocks.wall_sign, Blocks.trapdoor, Blocks.trapped_chest, Blocks.wooden_button, Blocks.stone_button, Blocks.oak_door, Blocks.skull));

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        keyBindings[0] = new KeyBinding("Ghost Block Bind", Keyboard.KEY_G, "Lilase");
        keyBindings[1] = new KeyBinding("Hub", Keyboard.KEY_DIVIDE, "Lilase");
        keyBindings[2] = new KeyBinding("Auction House", Keyboard.KEY_END, "Lilase");
        for (KeyBinding keyBinding : keyBindings) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || event.phase != TickEvent.Phase.START) return;
        tickAmount++;

        if (keyBindings[0].isKeyDown()) {
            if (mc.objectMouseOver.getBlockPos() == null) return;
            Block block = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
            if (!interactables.contains(block)) {
                mc.theWorld.setBlockToAir(mc.objectMouseOver.getBlockPos());
            }
        }
        if (keyBindings[1].isKeyDown()) {
            mc.thePlayer.sendChatMessage("/hub");
        }
        if (keyBindings[2].isPressed()) {
            new AuctionHouse();
        }
        if (tickAmount % 20 == 0) {
            Utils.checkForDungeon();
        }

        switch (clickState) {
            case CLICK:
                if (System.currentTimeMillis() - lastAction < 500) return;
                Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.openContainer.windowId, 31, 0, 0, Minecraft.getMinecraft().thePlayer);
                break;

            case OPEN:
                lastAction = System.currentTimeMillis();
                AuctionHouse.sendAuction();
                clickState = States.CLICK;
        }

    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (!message.contains(":")) {
            if (message.equals("Myrap opened a WITHER door!")) {
                Utils.addTitle("&aMyr opened a door");
            }
        }
        event.message = new ChatComponentText(event.message.getFormattedText().replace("§bCo-op > ", "§zCo-op > "));
    }

    @SubscribeEvent
    public void onPacket(PacketReceivedEvent event) {
        if (Utils.inDungeon && event.packet instanceof S2DPacketOpenWindow && ChatFormatting.stripFormatting(((S2DPacketOpenWindow) event.packet).getWindowTitle().getFormattedText()).equals("Chest")) {
            event.setCanceled(true);
            mc.getNetHandler().getNetworkManager().sendPacket(new C0DPacketCloseWindow(((S2DPacketOpenWindow) event.packet).getWindowId()));
        }
    }
}