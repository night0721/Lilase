package me.night0721.lilase.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import me.night0721.lilase.gui.ConfigGUI;

public class AHConfig extends Config {
    public AHConfig() {
        super(new Mod("Lilase", ModType.UTIL_QOL), "lilase.json");
        initialize();
    }

    @HUD(name = "Lilase")
    public ConfigGUI hud = new ConfigGUI();

    @Slider(name = "Time per fetch (seconds)", min = 5, max = 15, step = 1)
    public static int AUCTION_HOUSE_DELAY = 5;

    @Text(name = "Discord Webhook", placeholder = "URL")
    public static String WEBHOOK = "";
}
