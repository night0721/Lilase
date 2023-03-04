package me.night0721.lilase.features.ah;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.night0721.lilase.utils.ConfigUtils;
import me.night0721.lilase.utils.Utils;

public class AHConfig extends Config {
    public AHConfig() {
        super(new Mod("Lilase", ModType.UTIL_QOL), "lilase.json");
        initialize();
        addListener("SEND_MESSAGE", () -> ConfigUtils.writeBooleanConfig("main", "SendMessageToWebhook", AHConfig.SEND_MESSAGE));
        addListener("WEBHOOK", () -> ConfigUtils.writeStringConfig("main", "Webhook", AHConfig.WEBHOOK));
        addListener("RECONNECT_DELAY", () -> ConfigUtils.writeIntConfig("main", "ReconnectDelay", Math.round(AHConfig.RECONNECT_DELAY)));
        addListener("AUCTION_HOUSE_DELAY", () -> ConfigUtils.writeIntConfig("main", "AuctionHouseDelay", Math.round(AHConfig.AUCTION_HOUSE_DELAY)));
        addListener("CHECK_PERCENTAGE", () -> ConfigUtils.writeBooleanConfig("main", "checkProfitPercentageBeforeBuy", AHConfig.CHECK_PERCENTAGE));
        addListener("PROFIT_PERCENTAGE", () -> ConfigUtils.writeIntConfig("main", "ProfitPercentage", Math.round(AHConfig.PROFIT_PERCENTAGE)));
        addListener("GUI", () -> ConfigUtils.writeBooleanConfig("main", "GUI", AHConfig.GUI));
        addListener("GUI_COLOR", () -> ConfigUtils.writeIntConfig("main", "GUI_COLOR", AHConfig.GUI_COLOR.getRGB()));
        addListener("ITEM_1_NAME", () -> ConfigUtils.writeStringConfig("item1", "Name", AHConfig.ITEM_1_NAME));
        addListener("ITEM_1_TYPE", () -> ConfigUtils.writeStringConfig("item1", "Type", AHConfig.ITEM_1_TYPE));
        addListener("ITEM_1_PRICE", () -> ConfigUtils.writeIntConfig("item1", "Price", Math.round(AHConfig.ITEM_1_PRICE)));
        addListener("ITEM_1_TIER", () -> ConfigUtils.writeStringConfig("item1", "Tier", AHConfig.ITEM_1_TIER));
        addListener("ITEM_2_NAME", () -> ConfigUtils.writeStringConfig("item2", "Name", AHConfig.ITEM_2_NAME));
        addListener("ITEM_2_TYPE", () -> ConfigUtils.writeStringConfig("item2", "Type", AHConfig.ITEM_2_TYPE));
        addListener("ITEM_2_PRICE", () -> ConfigUtils.writeIntConfig("item2", "Price", Math.round(AHConfig.ITEM_2_PRICE)));
        addListener("ITEM_2_TIER", () -> ConfigUtils.writeStringConfig("item2", "Tier", AHConfig.ITEM_2_TIER));
        addListener("ITEM_3_NAME", () -> ConfigUtils.writeStringConfig("item3", "Name", AHConfig.ITEM_3_NAME));
        addListener("ITEM_3_TYPE", () -> ConfigUtils.writeStringConfig("item3", "Type", AHConfig.ITEM_3_TYPE));
        addListener("ITEM_3_PRICE", () -> ConfigUtils.writeIntConfig("item3", "Price", Math.round(AHConfig.ITEM_3_PRICE)));
        addListener("ITEM_3_TIER", () -> ConfigUtils.writeStringConfig("item3", "Tier", AHConfig.ITEM_3_TIER));
        addListener("ITEM_4_NAME", () -> ConfigUtils.writeStringConfig("item4", "Name", AHConfig.ITEM_4_NAME));
        addListener("ITEM_4_TYPE", () -> ConfigUtils.writeStringConfig("item4", "Type", AHConfig.ITEM_4_TYPE));
        addListener("ITEM_4_PRICE", () -> ConfigUtils.writeIntConfig("item4", "Price", Math.round(AHConfig.ITEM_4_PRICE)));
        addListener("ITEM_4_TIER", () -> ConfigUtils.writeStringConfig("item4", "Tier", AHConfig.ITEM_4_TIER));
        addListener("ITEM_5_NAME", () -> ConfigUtils.writeStringConfig("item5", "Name", AHConfig.ITEM_5_NAME));
        addListener("ITEM_5_TYPE", () -> ConfigUtils.writeStringConfig("item5", "Type", AHConfig.ITEM_5_TYPE));
        addListener("ITEM_5_PRICE", () -> ConfigUtils.writeIntConfig("item5", "Price", Math.round(AHConfig.ITEM_5_PRICE)));
        addListener("ITEM_5_TIER", () -> ConfigUtils.writeStringConfig("item5", "Tier", AHConfig.ITEM_5_TIER));
        addListener("ITEM_6_NAME", () -> ConfigUtils.writeStringConfig("item6", "Name", AHConfig.ITEM_6_NAME));
        addListener("ITEM_6_TYPE", () -> ConfigUtils.writeStringConfig("item6", "Type", AHConfig.ITEM_6_TYPE));
        addListener("ITEM_6_PRICE", () -> ConfigUtils.writeIntConfig("item6", "Price", Math.round(AHConfig.ITEM_6_PRICE)));
        addListener("ITEM_6_TIER", () -> ConfigUtils.writeStringConfig("item6", "Tier", AHConfig.ITEM_6_TIER));
        addListener("ITEM_7_NAME", () -> ConfigUtils.writeStringConfig("item7", "Name", AHConfig.ITEM_7_NAME));
        addListener("ITEM_7_TYPE", () -> ConfigUtils.writeStringConfig("item7", "Type", AHConfig.ITEM_7_TYPE));
        addListener("ITEM_7_PRICE", () -> ConfigUtils.writeIntConfig("item7", "Price", Math.round(AHConfig.ITEM_7_PRICE)));
        addListener("ITEM_7_TIER", () -> ConfigUtils.writeStringConfig("item7", "Tier", AHConfig.ITEM_7_TIER));
        addListener("ITEM_8_NAME", () -> ConfigUtils.writeStringConfig("item8", "Name", AHConfig.ITEM_8_NAME));
        addListener("ITEM_8_TYPE", () -> ConfigUtils.writeStringConfig("item8", "Type", AHConfig.ITEM_8_TYPE));
        addListener("ITEM_8_PRICE", () -> ConfigUtils.writeIntConfig("item8", "Price", Math.round(AHConfig.ITEM_8_PRICE)));
        addListener("ITEM_8_TIER", () -> ConfigUtils.writeStringConfig("item8", "Tier", AHConfig.ITEM_8_TIER));
        addListener("ITEM_9_NAME", () -> ConfigUtils.writeStringConfig("item9", "Name", AHConfig.ITEM_9_NAME));
        addListener("ITEM_9_TYPE", () -> ConfigUtils.writeStringConfig("item9", "Type", AHConfig.ITEM_9_TYPE));
        addListener("ITEM_9_PRICE", () -> ConfigUtils.writeIntConfig("item9", "Price", Math.round(AHConfig.ITEM_9_PRICE)));
        addListener("ITEM_9_TIER", () -> ConfigUtils.writeStringConfig("item9", "Tier", AHConfig.ITEM_9_TIER));
        addListener("ITEM_10_NAME", () -> ConfigUtils.writeStringConfig("item10", "Name", AHConfig.ITEM_10_NAME));
        addListener("ITEM_10_TYPE", () -> ConfigUtils.writeStringConfig("item10", "Type", AHConfig.ITEM_10_TYPE));
        addListener("ITEM_10_PRICE", () -> ConfigUtils.writeIntConfig("item10", "Price", Math.round(AHConfig.ITEM_10_PRICE)));
        addListener("ITEM_10_TIER", () -> ConfigUtils.writeStringConfig("item10", "Tier", AHConfig.ITEM_10_TIER));
        addDependency("WEBHOOK", "SEND_MESSAGE");
        addDependency("PROFIT_PERCENTAGE", "CHECK_PERCENTAGE");
        addDependency("GUI_COLOR", "GUI");
        addDependency("ITEM_1_NAME", "addItem", () -> ConfigUtils.getString("item1", "Name").equals(""));
        addDependency("ITEM_1_TYPE", "addItem", () -> ConfigUtils.getString("item1", "Type").equals(""));
        addDependency("ITEM_1_PRICE", "addItem", () -> ConfigUtils.getString("item1", "Price").equals(""));
        addDependency("ITEM_1_TIER", "addItem", () -> ConfigUtils.getString("item1", "Tier").equals(""));
        addDependency("ITEM_2_NAME", "addItem", () -> ConfigUtils.getString("item2", "Name").equals(""));
        addDependency("ITEM_2_TYPE", "addItem", () -> ConfigUtils.getString("item2", "Type").equals(""));
        addDependency("ITEM_2_PRICE", "addItem", () -> ConfigUtils.getString("item2", "Price").equals(""));
        addDependency("ITEM_2_TIER", "addItem", () -> ConfigUtils.getString("item2", "Tier").equals(""));
        addDependency("ITEM_3_NAME", "addItem", () -> ConfigUtils.getString("item3", "Name").equals(""));
        addDependency("ITEM_3_TYPE", "addItem", () -> ConfigUtils.getString("item3", "Type").equals(""));
        addDependency("ITEM_3_PRICE", "addItem", () -> ConfigUtils.getString("item3", "Price").equals(""));
        addDependency("ITEM_3_TIER", "addItem", () -> ConfigUtils.getString("item3", "Tier").equals(""));
        addDependency("ITEM_4_NAME", "addItem", () -> ConfigUtils.getString("item4", "Name").equals(""));
        addDependency("ITEM_4_TYPE", "addItem", () -> ConfigUtils.getString("item4", "Type").equals(""));
        addDependency("ITEM_4_PRICE", "addItem", () -> ConfigUtils.getString("item4", "Price").equals(""));
        addDependency("ITEM_4_TIER", "addItem", () -> ConfigUtils.getString("item4", "Tier").equals(""));
        addDependency("ITEM_5_NAME", "addItem", () -> !ConfigUtils.getString("item5", "Name").equals(""));
        addDependency("ITEM_5_TYPE", "addItem", () -> !ConfigUtils.getString("item5", "Type").equals(""));
        addDependency("ITEM_5_PRICE", "addItem", () -> !ConfigUtils.getString("item5", "Price").equals(""));
        addDependency("ITEM_5_TIER", "addItem", () -> !ConfigUtils.getString("item5", "Tier").equals(""));
        addDependency("ITEM_6_NAME", "addItem", () -> !ConfigUtils.getString("item6", "Name").equals(""));
        addDependency("ITEM_6_TYPE", "addItem", () -> !ConfigUtils.getString("item6", "Type").equals(""));
        addDependency("ITEM_6_PRICE", "addItem", () -> !ConfigUtils.getString("item6", "Price").equals(""));
        addDependency("ITEM_6_TIER", "addItem", () -> !ConfigUtils.getString("item6", "Tier").equals(""));
        addDependency("ITEM_7_NAME", "addItem", () -> !ConfigUtils.getString("item7", "Name").equals(""));
        addDependency("ITEM_7_TYPE", "addItem", () -> !ConfigUtils.getString("item7", "Type").equals(""));
        addDependency("ITEM_7_PRICE", "addItem", () -> !ConfigUtils.getString("item7", "Price").equals(""));
        addDependency("ITEM_7_TIER", "addItem", () -> !ConfigUtils.getString("item7", "Tier").equals(""));
        addDependency("ITEM_8_NAME", "addItem", () -> !ConfigUtils.getString("item8", "Name").equals(""));
        addDependency("ITEM_8_TYPE", "addItem", () -> !ConfigUtils.getString("item8", "Type").equals(""));
        addDependency("ITEM_8_PRICE", "addItem", () -> !ConfigUtils.getString("item8", "Price").equals(""));
        addDependency("ITEM_8_TIER", "addItem", () -> !ConfigUtils.getString("item8", "Tier").equals(""));
        addDependency("ITEM_9_NAME", "addItem", () -> !ConfigUtils.getString("item9", "Name").equals(""));
        addDependency("ITEM_9_TYPE", "addItem", () -> !ConfigUtils.getString("item9", "Type").equals(""));
        addDependency("ITEM_9_PRICE", "addItem", () -> !ConfigUtils.getString("item9", "Price").equals(""));
        addDependency("ITEM_9_TIER", "addItem", () -> !ConfigUtils.getString("item9", "Tier").equals(""));
        addDependency("ITEM_10_NAME", "addItem", () -> !ConfigUtils.getString("item10", "Name").equals(""));
        addDependency("ITEM_10_TYPE", "addItem", () -> !ConfigUtils.getString("item10", "Type").equals(""));
        addDependency("ITEM_10_PRICE", "addItem", () -> !ConfigUtils.getString("item10", "Price").equals(""));
        addDependency("ITEM_10_TIER", "addItem", () -> !ConfigUtils.getString("item10", "Tier").equals(""));
    }

    @Slider(name = "Time per fetch (seconds)", min = 5, max = 15, step = 1, category = "Auction House", description = "Time between each fetch of the auction house, the faster the fetch, the more likely you will snipe the item")
    public static int AUCTION_HOUSE_DELAY = 8;

    @Text(name = "Discord Webhook", placeholder = "URL", category = "Auction House", description = "Discord webhook to send messages to")
    public static String WEBHOOK = "";

    @Switch(name = "Send message to webhook", category = "Auction House", description = "Send a message to the webhook when an item is bought")
    public static boolean SEND_MESSAGE = true;

    @Number(name = "Reconnect Delay", min = 5, max = 20, category = "Auction House", description = "Delay between each reconnect attempt to the server")
    public static int RECONNECT_DELAY = 20;

    @Switch(name = "Check Profit Percentage Before Buying", category = "Flipper", description = "Check the profit percentage before buying the item, if the profit percentage is too low, it will not buy the item")
    public static boolean CHECK_PERCENTAGE = false;

    @Number(name = "ProfitPercentage", min = 100, max = 5000, step = 50, category = "Flipper", description = "Profit percentage to check before buying the item, if the profit percentage is too low, it will not buy the item")
    public static int PROFIT_PERCENTAGE = 400;

    @Switch(name = "Bed Spam & Skip Confirm", category = "Auction House", description = "Spam the bed to buy the item just after the grace period ends and skips the confirmation of buying the item")
    public static boolean BED_SPAM = true;

    @Checkbox(name = "GUI", category = "GUI", description = "Enable the GUI")
    public static boolean GUI = true;

    @Color(name = "GUI_COLOR", category = "GUI")
    public static OneColor GUI_COLOR = new OneColor(0, 49, 83);

    @Button(name = "Add Item", text = "Click to add an item to snipe", category = "Auction House")
    public static void addItem() {
        Utils.debugLog("[AHConfig] Add Item Button Clicked");
    }

    @Dropdown(name = "Remove Item", category = "Auction House", description = "Remove an item from the snipe list", options = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"})
    public static int value = 0;

    @Text(name = "Item 1 Name", placeholder = "Item Name")
    public static String ITEM_1_NAME = " ";

    @Text(name = "Item 1 Type", placeholder = "Item Type")
    public static String ITEM_1_TYPE = "ANY";

    @Number(name = "Item 1 Price", min = 1, max = 1000000000)
    public static int ITEM_1_PRICE = 1;

    @Text(name = "Item 1 Tier", placeholder = "Item Tier")
    public static String ITEM_1_TIER = "ANY";

    @Text(name = "Item 2 Name", placeholder = "Item Name")
    public static String ITEM_2_NAME = " ";

    @Text(name = "Item 2 Type", placeholder = "Item Type")
    public static String ITEM_2_TYPE = "ANY";

    @Number(name = "Item 2 Price", min = 1, max = 1000000000)
    public static int ITEM_2_PRICE = 1;

    @Text(name = "Item 2 Tier", placeholder = "Item Tier")
    public static String ITEM_2_TIER = "ANY";

    @Text(name = "Item 3 Name", placeholder = "Item Name")
    public static String ITEM_3_NAME = " ";

    @Text(name = "Item 3 Type", placeholder = "Item Type")
    public static String ITEM_3_TYPE = "ANY";

    @Number(name = "Item 3 Price", min = 1, max = 1000000000)
    public static int ITEM_3_PRICE = 1;

    @Text(name = "Item 3 Tier", placeholder = "Item Tier")
    public static String ITEM_3_TIER = "ANY";

    @Text(name = "Item 4 Name", placeholder = "Item Name")
    public static String ITEM_4_NAME = " ";

    @Text(name = "Item 4 Type", placeholder = "Item Type")
    public static String ITEM_4_TYPE = "ANY";

    @Number(name = "Item 4 Price", min = 1, max = 1000000000)
    public static int ITEM_4_PRICE = 1;

    @Text(name = "Item 4 Tier", placeholder = "Item Tier")
    public static String ITEM_4_TIER = "ANY";

    @Text(name = "Item 5 Name", placeholder = "Item Name")
    public static String ITEM_5_NAME = " ";

    @Text(name = "Item 5 Type", placeholder = "Item Type")
    public static String ITEM_5_TYPE = "ANY";

    @Number(name = "Item 5 Price", min = 1, max = 1000000000)
    public static int ITEM_5_PRICE = 1;

    @Text(name = "Item 5 Tier", placeholder = "Item Tier")
    public static String ITEM_5_TIER = "ANY";

    @Text(name = "Item 6 Name", placeholder = "Item Name")
    public static String ITEM_6_NAME = " ";

    @Text(name = "Item 6 Type", placeholder = "Item Type")
    public static String ITEM_6_TYPE = "ANY";

    @Number(name = "Item 6 Price", min = 1, max = 1000000000)
    public static int ITEM_6_PRICE = 1;

    @Text(name = "Item 6 Tier", placeholder = "Item Tier")
    public static String ITEM_6_TIER = "ANY";

    @Text(name = "Item 7 Name", placeholder = "Item Name")
    public static String ITEM_7_NAME = " ";

    @Text(name = "Item 7 Type", placeholder = "Item Type")
    public static String ITEM_7_TYPE = "ANY";

    @Number(name = "Item 7 Price", min = 1, max = 1000000000)
    public static int ITEM_7_PRICE = 1;

    @Text(name = "Item 7 Tier", placeholder = "Item Tier")
    public static String ITEM_7_TIER = "ANY";

    @Text(name = "Item 8 Name", placeholder = "Item Name")
    public static String ITEM_8_NAME = " ";

    @Text(name = "Item 8 Type", placeholder = "Item Type")
    public static String ITEM_8_TYPE = "ANY";

    @Number(name = "Item 8 Price", min = 1, max = 1000000000)
    public static int ITEM_8_PRICE = 1;

    @Text(name = "Item 8 Tier", placeholder = "Item Tier")
    public static String ITEM_8_TIER = "ANY";

    @Text(name = "Item 9 Name", placeholder = "Item Name")
    public static String ITEM_9_NAME = " ";

    @Text(name = "Item 9 Type", placeholder = "Item Type")
    public static String ITEM_9_TYPE = "ANY";

    @Number(name = "Item 9 Price", min = 1, max = 1000000000)
    public static int ITEM_9_PRICE = 1;

    @Text(name = "Item 9 Tier", placeholder = "Item Tier")
    public static String ITEM_9_TIER = "ANY";

    @Text(name = "Item 10 Name", placeholder = "Item Name")
    public static String ITEM_10_NAME = " ";

    @Text(name = "Item 10 Type", placeholder = "Item Type")
    public static String ITEM_10_TYPE = "ANY";

    @Number(name = "Item 10 Price", min = 1, max = 1000000000)
    public static int ITEM_10_PRICE = 1;

    @Text(name = "Item 10 Tier", placeholder = "Item Tier")
    public static String ITEM_10_TIER = "ANY";

}
