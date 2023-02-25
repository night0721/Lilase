package me.night0721.lilase;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.events.PacketReceivedEvent;
import me.night0721.lilase.features.ah.AuctionHouse;
import me.night0721.lilase.features.flip.Flipper;
import me.night0721.lilase.utils.KeyBindingManager;
import me.night0721.lilase.events.SniperFlipperEvents;
import me.night0721.lilase.utils.ConfigUtils;
import me.night0721.lilase.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.Display;

import java.io.IOException;

import static me.night0721.lilase.config.AHConfig.AUCTION_HOUSE_DELAY;
import static me.night0721.lilase.config.AHConfig.RECONNECT_DELAY;

@Mod(modid = Lilase.MODID, name = Lilase.MOD_NAME, version = Lilase.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class Lilase {
    public static final String MOD_NAME = "Lilase";
    public static final String MODID = "Lilase";
    public static final String VERSION = "1.0.21";
    static int tickAmount;
    int waitTime;
    public static AuctionHouse auctionHouse;
    public static AHConfig config;
    static final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new KeyBindingManager());
        MinecraftForge.EVENT_BUS.register(new SniperFlipperEvents());
        EventManager.INSTANCE.register(this);
        ConfigUtils.register();
        auctionHouse = new AuctionHouse();
        KeyBindingManager.registerKeyBindings();
        Display.setTitle("Lilase v" + VERSION + " | night0721");
    }

    @Subscribe
    public void initConfig(InitializationEvent ignore) {
        config = new AHConfig();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) throws IOException {
        if (mc.thePlayer == null || event.phase != TickEvent.Phase.START) return;
        tickAmount++;
        if (tickAmount % 20 == 0) {
            Utils.checkForDungeon();
        }
        if (tickAmount % (20 * AUCTION_HOUSE_DELAY) == 0) {
            auctionHouse.getItem();
        }
        if (tickAmount % 2400 == 0) {
            ConfigUtils.checkWebhookAndAPI();
        }
        Flipper.switchStates();
        if ((mc.currentScreen instanceof GuiDisconnected)) {
            if (waitTime >= (RECONNECT_DELAY * 20)) {
                waitTime = 0;
                FMLClientHandler.instance().connectToServer(new GuiMultiplayer(new GuiMainMenu()), new ServerData(" ", "mc.hypixel.net", false));
            } else {
                waitTime++;
            }
        }
    }

    @SubscribeEvent
    public void onPacket(PacketReceivedEvent event) {
        if (Utils.inDungeon && event.packet instanceof S2DPacketOpenWindow && ChatFormatting.stripFormatting(((S2DPacketOpenWindow) event.packet).getWindowTitle().getFormattedText()).equals("Chest")) {
            event.setCanceled(true);
            mc.getNetHandler().getNetworkManager().sendPacket(new C0DPacketCloseWindow(((S2DPacketOpenWindow) event.packet).getWindowId()));
        }
    }
    /*
    TODO: Priority on buying
    TODO: Console client??
    TODO: Page flipper
    TODO: Percentage calculator
    TODO: Blacklist
    TODO: Wither Impact in lore check
    TODO: Blue omelette in lore check
    TODO: Discord Embed improve, tell profit and profit percentage
     */
}