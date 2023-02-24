package me.night0721.lilase.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Checkbox;
import cc.polyfrost.oneconfig.config.annotations.Number;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.night0721.lilase.utils.ConfigUtils;

public class AHConfig extends Config {
    public AHConfig() {
        super(new Mod("Lilase", ModType.UTIL_QOL), "lilase.json");
        initialize();
        addListener("AUCTION_HOUSE_DELAY", () -> ConfigUtils.writeIntConfig("main", "AuctionHouseDelay", Math.round(AHConfig.AUCTION_HOUSE_DELAY)));
        addListener("WEBHOOK", () -> ConfigUtils.writeStringConfig("main", "Webhook", AHConfig.WEBHOOK));
        addListener("CHECK_MULTIPLIER", () -> ConfigUtils.writeBooleanConfig("main", "checkMultiplierBeforeBuy", AHConfig.CHECK_MULTIPLIER));
        addListener("MULTIPLIER", () -> ConfigUtils.writeIntConfig("main", "MULTIPLIER", Math.round(AHConfig.MULTIPLIER)));
    }

//    @HUD(name = "Lilase")
//    public CoordinateGUI hud = new CoordinateGUI();


    @Slider(name = "Time per fetch (seconds)", min = 5, max = 15, step = 1, category = "Auction House")
    public static int AUCTION_HOUSE_DELAY = 8;

    @Text(name = "Discord Webhook", placeholder = "URL", category = "Auction House")
    public static String WEBHOOK = "";

    @Checkbox(name = "Check Multiplier Before Buying", category = "Flipper")
    public static boolean CHECK_MULTIPLIER = false;

    @Number(name = "Multiplier", min = 100, max = 5000, step = 50, category = "Flipper")
    public static int MULTIPLIER = 400;


}
