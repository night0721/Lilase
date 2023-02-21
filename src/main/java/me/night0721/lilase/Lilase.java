package me.night0721.lilase;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import com.mojang.realmsclient.gui.ChatFormatting;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.events.PacketReceivedEvent;
import me.night0721.lilase.features.ah.AuctionHouse;
import me.night0721.lilase.managers.KeyBindingManager;
import me.night0721.lilase.utils.ConfigUtils;
import me.night0721.lilase.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = Lilase.MODID, name = Lilase.MOD_NAME, version = Lilase.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class Lilase {
    public static final String MOD_NAME = "Lilase";
    public static final String MODID = "Lilase";
    public static final String VERSION = "1.0.0";
    static int tickAmount;
    public static AuctionHouse auctionHouse;
    public static AHConfig config;
    static final Minecraft mc = Minecraft.getMinecraft();


    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new KeyBindingManager());
        MinecraftForge.EVENT_BUS.register(new EventManager());
        EventManager.INSTANCE.register(this);
        auctionHouse = new AuctionHouse();
        KeyBindingManager.registerKeyBindings();
        ConfigUtils.init();
        ConfigUtils.reloadConfig();
    }

    @Subscribe
    public void onConfigInit(InitializationEvent event) {
        config = new AHConfig();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || event.phase != TickEvent.Phase.START) return;
        tickAmount++;
        if (tickAmount % 20 == 0) {
            Utils.checkForDungeon();
        }
        AuctionHouse.switchStates();
    }

    @SubscribeEvent
    public void onPacket(PacketReceivedEvent event) {
        if (Utils.inDungeon && event.packet instanceof S2DPacketOpenWindow && ChatFormatting.stripFormatting(((S2DPacketOpenWindow) event.packet).getWindowTitle().getFormattedText()).equals("Chest")) {
            event.setCanceled(true);
            mc.getNetHandler().getNetworkManager().sendPacket(new C0DPacketCloseWindow(((S2DPacketOpenWindow) event.packet).getWindowId()));
        }
    }
}