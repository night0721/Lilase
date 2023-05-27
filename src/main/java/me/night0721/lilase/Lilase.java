package me.night0721.lilase;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.config.ConfigHandler;
import me.night0721.lilase.events.ChatReceivedEvent;
import me.night0721.lilase.events.SniperFlipperEvents;
import me.night0721.lilase.features.claimer.Claimer;
import me.night0721.lilase.features.cofl.Cofl;
//import me.night0721.lilase.features.pageflipper.PageFlipper;
import me.night0721.lilase.features.cofl.QueueItem;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.features.flipper.FlipperState;
import me.night0721.lilase.features.relister.Relister;
import me.night0721.lilase.gui.GUIRenderer;
import me.night0721.lilase.remotecontrol.RemoteControl;
import me.night0721.lilase.utils.Clock;
import me.night0721.lilase.utils.KeyBindingManager;
import me.night0721.lilase.utils.ScoreboardUtils;
import me.night0721.lilase.utils.Utils;
import me.night0721.lilase.utils.capes.CapeDatabase;
import me.night0721.lilase.utils.capes.CapeManager;
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

import static me.night0721.lilase.config.AHConfig.*;
import static me.night0721.lilase.features.flipper.Flipper.icon;
import static me.night0721.lilase.features.flipper.Flipper.webhook;

@Mod(modid = Lilase.MODID, name = Lilase.MOD_NAME, version = Lilase.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class Lilase {
    public static final String MOD_NAME = "Lilase";
    public static final String MODID = "Lilase";
    public static final String VERSION = "3.0.4-beta";
    public static final Minecraft mc = Minecraft.getMinecraft();
    //    public static PageFlipper pageFlipper;
    public static Claimer claimer;
    public static Relister relister;
    public static Cofl cofl;
    public static AHConfig config;
    public static ConfigHandler configHandler;
    private int tickAmount;
    private final Clock clock = new Clock();
    public static RemoteControl remoteControl;
    public static final CapeDatabase capeDatabase = new CapeDatabase();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        (configHandler = new ConfigHandler()).init();
        KeyBindingManager keyBindingManager = new KeyBindingManager();
        addToEventBus(this, keyBindingManager, new SniperFlipperEvents(), new ChatReceivedEvent(), new GUIRenderer(), new CapeManager());
        EventManager.INSTANCE.register(this);
//        pageFlipper = new PageFlipper();
        keyBindingManager.registerKeyBindings();
        (cofl = new Cofl()).onOpen();
        claimer = new Claimer();
        relister = new Relister();
        webhook.setUsername("Lilase");
        webhook.setAvatarUrl(icon);
        remoteControl = new RemoteControl();
        capeDatabase.init();
    }

    private void addToEventBus(Object... objects) {
        for (Object object : objects) MinecraftForge.EVENT_BUS.register(object);
    }

    @Subscribe
    public void initConfig(InitializationEvent ignore) {
        config = new AHConfig();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || event.phase != TickEvent.Phase.START) return;
        tickAmount++;
        if (tickAmount % 20 == 0) Utils.checkFooter();
//        if (pageFlipper != null) pageFlipper.switchStates();
        if (tickAmount % (RELIST_CHECK_TIMEOUT * 72_000) == 0 && ScoreboardUtils.getSidebarLines().stream().map(ScoreboardUtils::cleanSB).anyMatch(s -> s.contains("SKYBLOCK")) && AUTO_RELIST) {
            relister.shouldBeRelisting = true;
            if (Flipper.state == FlipperState.NONE) relister.toggle();
        }
        if (claimer != null) claimer.onTick();
        if (relister != null) relister.onTick();
        if (cofl.isOpen() && !cofl.queue.isEmpty() && !cofl.queue.isRunning()) {
            cofl.queue.setRunning(true);
            QueueItem item = cofl.queue.get();
            item.openAuction();
        }
        if (mc.currentScreen instanceof GuiDisconnected && clock.passed()) {
            clock.schedule(RECONNECT_DELAY * 1000L);
            FMLClientHandler.instance().connectToServer(new GuiMultiplayer(new GuiMainMenu()), new ServerData(" ", "mc.hypixel.net", false));
        }
    }
}
// Cesium: #ab84ff
// Potassium: #deb4d1