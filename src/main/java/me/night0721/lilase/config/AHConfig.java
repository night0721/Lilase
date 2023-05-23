package me.night0721.lilase.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.night0721.lilase.Lilase;

public class AHConfig extends Config {
    public AHConfig() {
        super(new Mod("Lilase", ModType.SKYBLOCK, "/assets/lilase.png", 100, 100), "lilase.json");
        initialize();
        addListener("SEND_MESSAGE", () -> Lilase.configHandler.setBoolean("SendMessageToWebhook", SEND_MESSAGE));
        addListener("WEBHOOK", () -> Lilase.configHandler.setString("Webhook", WEBHOOK));
        addListener("RECONNECT_DELAY", () -> Lilase.configHandler.setInt("ReconnectDelay", Math.round(RECONNECT_DELAY)));
        addListener("BED_SPAM", () -> Lilase.configHandler.setBoolean("BedSpam", BED_SPAM));
        addListener("BED_SPAM_DELAY", () -> Lilase.configHandler.setInt("BedSpamDelay", Math.round(BED_SPAM_DELAY)));
        addListener("ONLY_SNIPER", () -> Lilase.configHandler.setBoolean("OnlySniper", ONLY_SNIPER));
        addListener("GUI", () -> Lilase.configHandler.setBoolean("GUI", GUI));
        addListener("GUI_COLOR", () -> Lilase.configHandler.setInt("GUI_COLOR", GUI_COLOR.getRGB()));
        addListener("RELIST_TIMEOUT", () -> Lilase.configHandler.setInt("RelistTimeout", Math.round(RELIST_TIMEOUT)));
        addListener("DEBUG", () -> Lilase.configHandler.setBoolean("Debug", DEBUG));
        addListener("AUCTION_LENGTH", () -> Lilase.configHandler.setInt("AuctionLength", Math.round(AUCTION_LENGTH)));
        addDependency("WEBHOOK", "SEND_MESSAGE");
        addDependency("GUI_COLOR", "GUI");
    }

    @Switch(name = "Bed Spam & Skip Confirm", category = "Auction House", subcategory = "Sniper", description = "Spam the bed to buy the item just after the grace period ends and skips the confirmation of buying the item")
    public static boolean BED_SPAM = true;

    @Slider(name = "Bed Spam Delay (ms)", min = 50, max = 500, step = 50, category = "Auction House", subcategory = "Sniper", description = "Delay between each bed spam (milliseconds)")
    public static int BED_SPAM_DELAY = 100;

    @Number(name = "Relist timeout (ms)", min = 1500, max = 60000, step = 500, category = "Auction House", subcategory = "Flipper", description = "Delay between buying and relisting an item (milliseconds)")
    public static int RELIST_TIMEOUT = 1500;
    @Dropdown(name = "Auction Listing Length", options = {"1 Hour", "6 Hours", "12 Hours", "24 Hours", "2 Days"}, category = "Auction House", subcategory = "Flipper", description = "Length of the auction listing")
    public static int AUCTION_LENGTH = 2;

    @Switch(name = "Debug", category = "Auction House", subcategory = "Sniper", description = "Debug mode")
    public static boolean DEBUG = false;

    @Switch(name = "Only Sniper", category = "Auction House", subcategory = "Sniper", description = "Only snipe items, stop auto resell")
    public static boolean ONLY_SNIPER = false;

    @Text(name = "Discord Webhook", placeholder = "URL", category = "Auction House", subcategory = "Webhook", description = "Discord webhook to send messages to")
    public static String WEBHOOK = "";

    @Switch(name = "Send message to webhook", category = "Auction House", subcategory = "Webhook", description = "Send a message to the webhook when an item is bought")
    public static boolean SEND_MESSAGE = true;

    @Number(name = "Reconnect Delay(s)", min = 5, max = 20, category = "Failsafe", description = "Delay between each reconnect attempt to the server (seconds)")
    public static int RECONNECT_DELAY = 20;

    @Checkbox(name = "GUI", category = "GUI", description = "Enable the GUI")
    public static boolean GUI = true;

    @Checkbox(name = "HK No.1", category = "GUI", description = "Enable the string HK No.1")
    public static boolean HKNO1 = true;

    @Checkbox(name = "Crabby", category = "GUI", description = "Enable the Crabby image")
    public static boolean CRABBY = true;

    @Color(name = "GUI Color", category = "GUI")
    public static OneColor GUI_COLOR = new OneColor(0, 49, 83);
}
