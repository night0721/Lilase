package me.night0721.lilase.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.night0721.lilase.utils.Utils;

public class AHConfig extends Config {
    public AHConfig() {
        super(new Mod("Lilase", ModType.UTIL_QOL), "lilase.json");
        initialize();
        addListener("SEND_MESSAGE", () -> ConfigUtils.writeBooleanConfig("main", "SendMessageToWebhook", AHConfig.SEND_MESSAGE));
        addListener("WEBHOOK", () -> ConfigUtils.writeStringConfig("main", "Webhook", AHConfig.WEBHOOK));
        addListener("RECONNECT_DELAY", () -> ConfigUtils.writeIntConfig("main", "ReconnectDelay", Math.round(AHConfig.RECONNECT_DELAY)));
        addListener("AUCTION_HOUSE_DELAY", () -> ConfigUtils.writeIntConfig("main", "AuctionHouseDelay", Math.round(AHConfig.AUCTION_HOUSE_DELAY)));
        addListener("BED_SPAM", () -> ConfigUtils.writeBooleanConfig("main", "BedSpam", AHConfig.BED_SPAM));
        addListener("BED_SPAM_DELAY", () -> ConfigUtils.writeIntConfig("main", "BedSpamDelay", Math.round(AHConfig.BED_SPAM_DELAY)));
        addListener("ONLY_SNIPER", () -> ConfigUtils.writeBooleanConfig("main", "OnlySniper", AHConfig.ONLY_SNIPER));
        addListener("CHECK_PERCENTAGE", () -> ConfigUtils.writeBooleanConfig("main", "checkProfitPercentageBeforeBuy", AHConfig.CHECK_PERCENTAGE));
        addListener("MINIMUM_PROFIT_PERCENTAGE", () -> ConfigUtils.writeIntConfig("main", "MinimumProfitPercentage", Math.round(AHConfig.MINIMUM_PROFIT_PERCENTAGE)));
        addListener("GUI", () -> ConfigUtils.writeBooleanConfig("main", "GUI", AHConfig.GUI));
        addListener("GUI_COLOR", () -> ConfigUtils.writeIntConfig("main", "GUI_COLOR", AHConfig.GUI_COLOR.getRGB()));
        addListener("ITEM_1_NAME", () -> ConfigUtils.writeStringConfig("item1", "Name", AHConfig.ITEM_1_NAME));
        addListener("ITEM_1_TYPE", () -> ConfigUtils.writeStringConfig("item1", "Type", AHConfig.ITEM_1_TYPE));
        addListener("ITEM_1_PRICE", () -> ConfigUtils.writeIntConfig("item1", "Price", Math.round(AHConfig.ITEM_1_PRICE)));
        addListener("ITEM_1_TIER", () -> ConfigUtils.writeStringConfig("item1", "Tier", AHConfig.ITEM_1_TIER));
        addDependency("WEBHOOK", "SEND_MESSAGE");
        addDependency("PROFIT_PERCENTAGE", "CHECK_PERCENTAGE");
        addDependency("GUI_COLOR", "GUI");
        addDependency("ITEM_1_NAME", "addItem", () -> ConfigUtils.getString("item1", "Name").equals(""));
        addDependency("ITEM_1_TYPE", "addItem", () -> ConfigUtils.getString("item1", "Type").equals(""));
        addDependency("ITEM_1_PRICE", "addItem", () -> ConfigUtils.getString("item1", "Price").equals(""));
        addDependency("ITEM_1_TIER", "addItem", () -> ConfigUtils.getString("item1", "Tier").equals(""));
        }

    @Slider(name = "Time per fetch (seconds)", min = 5, max = 15, step = 1, category = "Auction House", subcategory = "Sniper", description = "Time between each fetch of the auction house, the faster the fetch, the more likely you will snipe the item")
    public static int AUCTION_HOUSE_DELAY = 8;

    @Switch(name = "Bed Spam & Skip Confirm", category = "Auction House", subcategory = "Sniper", description = "Spam the bed to buy the item just after the grace period ends and skips the confirmation of buying the item")
    public static boolean BED_SPAM = true;

    @Slider(name = "Bed Spam Delay (ms)", min = 50, max = 500, step = 50, category = "Auction House", subcategory = "Sniper", description = "Delay between each bed spam")
    public static int BED_SPAM_DELAY = 100;

    @Switch(name = "Only Sniper", category = "Auction House", subcategory = "Sniper", description = "Only snipe items, stop auto resell")
    public static boolean ONLY_SNIPER = false;

    @Button(name = "Add Item", text = "Click to add an item to snipe", subcategory = "Items", category = "Auction House")
    public static void addItem() {
        Utils.debugLog("[AHConfig] Add Item Button Clicked");
    }

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

    @Checkbox(name = "GUI", category = "GUI", description = "Enable the GUI")
    public static boolean GUI = true;

    @Color(name = "GUI Color", category = "GUI")
    public static OneColor GUI_COLOR = new OneColor(0, 49, 83);

    @Text(name = "Item 1 Name", placeholder = "Item Name")
    public static String ITEM_1_NAME = " ";

    @Text(name = "Item 1 Type", placeholder = "Item Type")
    public static String ITEM_1_TYPE = "ANY";

    @Number(name = "Item 1 Price", min = 1, max = 1000000000)
    public static int ITEM_1_PRICE = 1;

    @Text(name = "Item 1 Tier", placeholder = "Item Tier")
    public static String ITEM_1_TIER = "ANY";
}
