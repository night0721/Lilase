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
        super(new Mod("Lilase", ModType.UTIL_QOL), "lilase.json");
        initialize();
        addListener("SEND_MESSAGE", () -> Lilase.configHandler.setBoolean("SendMessageToWebhook", SEND_MESSAGE));
        addListener("WEBHOOK", () -> Lilase.configHandler.setString("Webhook", WEBHOOK));
        addListener("RECONNECT_DELAY", () -> Lilase.configHandler.setInt("ReconnectDelay", Math.round(RECONNECT_DELAY)));
        addListener("BED_SPAM", () -> Lilase.configHandler.setBoolean("BedSpam", BED_SPAM));
        addListener("BED_SPAM_DELAY", () -> Lilase.configHandler.setInt("BedSpamDelay", Math.round(BED_SPAM_DELAY)));
        addListener("ONLY_SNIPER", () -> Lilase.configHandler.setBoolean("OnlySniper", ONLY_SNIPER));
        addListener("GUI", () -> Lilase.configHandler.setBoolean("GUI", GUI));
        addListener("GUI_COLOR", () -> Lilase.configHandler.setInt("GUI_COLOR", GUI_COLOR.getRGB()));
        addDependency("WEBHOOK", "SEND_MESSAGE");
        addDependency("MINIMUM_PROFIT_PERCENTAGE", "CHECK_PERCENTAGE");
        addDependency("MAXIMUM_PROFIT_PERCENTAGE", "CHECK_MAXIMUM_PROFIT");
        addDependency("GUI_COLOR", "GUI");
    }

    @Switch(name = "Bed Spam & Skip Confirm", category = "Auction House", subcategory = "Sniper", description = "Spam the bed to buy the item just after the grace period ends and skips the confirmation of buying the item")
    public static boolean BED_SPAM = true;

    @Slider(name = "Bed Spam Delay (ms)", min = 50, max = 500, step = 50, category = "Auction House", subcategory = "Sniper", description = "Delay between each bed spam")
    public static int BED_SPAM_DELAY = 100;

    @Switch(name = "Only Sniper", category = "Auction House", subcategory = "Sniper", description = "Only snipe items, stop auto resell")
    public static boolean ONLY_SNIPER = false;

    @Text(name = "Discord Webhook", placeholder = "URL", category = "Auction House", subcategory = "Webhook", description = "Discord webhook to send messages to")
    public static String WEBHOOK = "";

    @Switch(name = "Send message to webhook", category = "Auction House", subcategory = "Webhook", description = "Send a message to the webhook when an item is bought")
    public static boolean SEND_MESSAGE = true;

    @Number(name = "Reconnect Delay", min = 5, max = 20, category = "Failsafe", description = "Delay between each reconnect attempt to the server")
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
