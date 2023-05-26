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
        super(new Mod("Lilase", ModType.SKYBLOCK, "/assets/lilase.png", 84, 84), "lilase.json");
        initialize();
        addListener("WEBHOOK", () -> Lilase.configHandler.setString("Webhook", WEBHOOK));addListener("REMOTE_CONTROL", () -> Lilase.configHandler.setBoolean("RemoteControl", REMOTE_CONTROL));
        addListener("BOT_TOKEN", () -> Lilase.configHandler.setString("BotToken", BOT_TOKEN));
        addListener("LOG_CHANNEL", () -> Lilase.configHandler.setString("LogChannel", LOG_CHANNEL));
        addDependency("WEBHOOK", "SEND_MESSAGE");
        addDependency("GUI_COLOR", "GUI");
        addDependency("RELIST_CHECK_TIMEOUT", "AUTO_RELIST");
        addDependency("BOT_TOKEN", "REMOTE_CONTROL");
        addDependency("LOG_CHANNEL", "REMOTE_CONTROL");
    }

    @Switch(name = "Bed Spam & Skip Confirm", category = "Auction House", subcategory = "Sniper", description = "Spam the bed to buy the item just after the grace period ends and skips the confirmation of buying the item")
    public static boolean BED_SPAM = true;

    @Slider(name = "Bed Spam Delay (ms)", min = 50, max = 500, step = 50, category = "Auction House", subcategory = "Sniper", description = "Delay between each bed spam (milliseconds)")
    public static int BED_SPAM_DELAY = 100;

    @Number(name = "Relist timeout (ms)", min = 1500, max = 60000, step = 500, category = "Auction House", subcategory = "Flipper", description = "Delay between buying and relisting an item (milliseconds)")
    public static int RELIST_TIMEOUT = 1500;

    @Number(name = "Relist check timeout (hours)", min = 0.5f, max = 2f, category = "Auction House", subcategory = "Flipper", description = "Delay between checking if an item is expired and relisting it (hours)")
    public static float RELIST_CHECK_TIMEOUT = 1f;

    @Dropdown(name = "Auction Listing Length", options = {"1 Hour", "6 Hours", "12 Hours", "24 Hours", "2 Days"}, category = "Auction House", subcategory = "Flipper", description = "Length of the auction listing")
    public static int AUCTION_LENGTH = 2;

    @Checkbox(name = "Shorten Numbers when listing", category = "Auction House", subcategory = "Flipper", description = "Shorten numbers when listing items")
    public static boolean SHORTEN_NUMBERS = false;

    @Checkbox(name = "Auto Relist", category = "Auction House", subcategory = "Flipper", description = "Automatically relist items after auctions are expired")
    public static boolean AUTO_RELIST = true;

    @Checkbox(name = "Auto Claim", category = "Auction House", subcategory = "Flipper", description = "Automatically claim items when it is bought")
    public static boolean AUTO_CLAIM = true;

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

    @Switch(name = "Custom Scoreboard", category = "GUI", description = "Enable the custom scoreboard")
    public static boolean CUSTOM_SCOREBOARD = true;

    @Switch(name = "Remote Control", category = "Remote Control", description = "Enable remote control")
    public static boolean REMOTE_CONTROL = false;

    @Text(name = "Bot Token", placeholder = "Token", category = "Remote Control", description = "Discord bot token")
    public static String BOT_TOKEN = "";

    @Text(name = "Log Channel", placeholder = "Channel ID", category = "Remote Control", description = "Discord channel ID to log messages to")
    public static String LOG_CHANNEL = "";
}
