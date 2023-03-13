package me.night0721.lilase;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.config.ConfigHandler;
import me.night0721.lilase.events.SniperFlipperEvents;
import me.night0721.lilase.features.cofl.Cofl;
import me.night0721.lilase.features.sniper.PageFlipper;
import me.night0721.lilase.features.sniper.Sniper;
import me.night0721.lilase.gui.ImageRenderer;
import me.night0721.lilase.utils.Clock;
import me.night0721.lilase.utils.KeyBindingManager;
import me.night0721.lilase.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.IOException;

import static me.night0721.lilase.config.AHConfig.RECONNECT_DELAY;

@Mod(modid = Lilase.MODID, name = Lilase.MOD_NAME, version = Lilase.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class Lilase {
    public static final String MOD_NAME = "Lilase";
    public static final String MODID = "Lilase";
    public static final String VERSION = "1.0.26";
    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Sniper sniper;
    public static PageFlipper pageFlipper;
    public static Cofl cofl;
    public static AHConfig config;
    public static ConfigHandler configHandler;
    private int tickAmount;
    private final Clock clock = new Clock();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        configHandler = new ConfigHandler();
        configHandler.init();
        KeyBindingManager keyBindingManager = new KeyBindingManager();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(keyBindingManager);
        MinecraftForge.EVENT_BUS.register(new SniperFlipperEvents());
        MinecraftForge.EVENT_BUS.register(new ImageRenderer());
        EventManager.INSTANCE.register(this);
        sniper = new Sniper();
        pageFlipper = new PageFlipper();
        keyBindingManager.registerKeyBindings();
        cofl = new Cofl();
        cofl.onOpen();
    }

    @Subscribe
    public void initConfig(InitializationEvent ignore) {
        config = new AHConfig();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) throws IOException {
        if (mc.thePlayer == null || event.phase != TickEvent.Phase.START) return;
        tickAmount++;
        if (tickAmount % 20 == 0) Utils.checkFooter();
        if (tickAmount % (20 * 60) == 0) sniper.start();
        if (tickAmount % 2400 == 0) configHandler.checkWebhookAndAPI();
        if (pageFlipper != null) pageFlipper.switchStates();
        if (Sniper.flipper != null) Sniper.flipper.switchStates();
        if (mc.currentScreen instanceof GuiDisconnected && clock.passed()) {
            clock.schedule(RECONNECT_DELAY * 1000L);
            FMLClientHandler.instance().connectToServer(new GuiMultiplayer(new GuiMainMenu()), new ServerData(" ", "mc.hypixel.net", false));
        }
    }
}
// Cesium: #ab84ff
// Potassium: #deb4d1