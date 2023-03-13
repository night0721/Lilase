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
        addListener("AUCTION_HOUSE_DELAY", () -> Lilase.configHandler.setInt("AuctionHouseDelay", Math.round(AUCTION_HOUSE_DELAY)));
        addListener("SNIPER_MODE", () -> Lilase.configHandler.setInt("SniperMode", SNIPER_MODE));
        addListener("BED_SPAM", () -> Lilase.configHandler.setBoolean("BedSpam", BED_SPAM));
        addListener("BED_SPAM_DELAY", () -> Lilase.configHandler.setInt("BedSpamDelay", Math.round(BED_SPAM_DELAY)));
        addListener("ONLY_SNIPER", () -> Lilase.configHandler.setBoolean("OnlySniper", ONLY_SNIPER));
        addListener("CHECK_PERCENTAGE", () -> Lilase.configHandler.setBoolean("checkProfitPercentageBeforeBuy", CHECK_PERCENTAGE));
        addListener("MINIMUM_PROFIT_PERCENTAGE", () -> Lilase.configHandler.setInt("MinimumProfitPercentage", Math.round(MINIMUM_PROFIT_PERCENTAGE)));
        addListener("CHECK_MAXIMUM_PROFIT", () -> Lilase.configHandler.setBoolean("checkMaxiumProfitPercentageBeforeBuy", CHECK_MAXIMUM_PROFIT));
        addListener("MAXIMUM_PROFIT_PERCENTAGE", () -> Lilase.configHandler.setInt("MaximumProfitPercentage", MAXIMUM_PROFIT_PERCENTAGE));
        addListener("GUI", () -> Lilase.configHandler.setBoolean("GUI", GUI));
        addListener("GUI_COLOR", () -> Lilase.configHandler.setInt("GUI_COLOR", GUI_COLOR.getRGB()));
        addDependency("WEBHOOK", "SEND_MESSAGE");
        addDependency("MINIMUM_PROFIT_PERCENTAGE", "CHECK_PERCENTAGE");
        addDependency("MAXIMUM_PROFIT_PERCENTAGE", "CHECK_MAXIMUM_PROFIT");
        addDependency("GUI_COLOR", "GUI");
    }

    @Slider(name = "Time per fetch (seconds)", min = 5, max = 15, step = 1, category = "Auction House", subcategory = "Sniper", description = "Time between each fetch of the auction house, the faster the fetch, the more likely you will snipe the item")
    public static int AUCTION_HOUSE_DELAY = 8;

    @Dropdown(name = "Sniper Mode", category = "Auction House", subcategory = "Sniper", options = {"API", "Page Flipper[WIP]", "COFL"})
    public static int SNIPER_MODE = 2;

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

    @Switch(name = "Check Profit Percentage Before Buying", category = "Flipper", description = "Check the profit percentage before buying the item, if the profit percentage is too low, it will not buy the item")
    public static boolean CHECK_PERCENTAGE = false;

    @Number(name = "Minimum Profit Percentage", min = 100, max = 5000, step = 50, category = "Flipper", description = "Profit percentage to check before buying the item, if the profit percentage is too low, it will not buy the item")
    public static int MINIMUM_PROFIT_PERCENTAGE = 400;

    @Switch(name = "Check Maximum Profit Before Buying", category = "Flipper", description = "Check the maximal profit before buying the item, if the maximal profit is too high, it will not buy the item (Can prevent duped items)")
    public static boolean CHECK_MAXIMUM_PROFIT = false;

    @Number(name = "Maximum Profit Percentage", min = 1000, max = 10000, step = 1000, category = "Flipper", description = "Maximum profit to check before buying the item, if the maximal profit is too high, it will not buy the item (Can prevent duped items)")
    public static int MAXIMUM_PROFIT_PERCENTAGE = 1000;

    @Checkbox(name = "GUI", category = "GUI", description = "Enable the GUI")
    public static boolean GUI = true;

    @Color(name = "GUI Color", category = "GUI")
    public static OneColor GUI_COLOR = new OneColor(0, 49, 83);
}
