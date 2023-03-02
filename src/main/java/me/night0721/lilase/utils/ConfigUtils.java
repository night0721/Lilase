package me.night0721.lilase.utils;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigUtils {
    public static Configuration config;
    public final static String file = "config/Lilase.cfg";

    public static void register() {
        init();
        reloadConfig();
    }

    public static void checkWebhookAndAPI() {
        if (getString("main", "APIKey").equals("") || getString("main", "Webhook").equals("")) {
            Utils.sendMessage("API Key or Webhook is not set, please set it in the menu (Press *)");
        }
    }

    public static void reloadConfig() {
        if (!hasKey("main", "APIKey")) writeStringConfig("main", "APIKey", "");
        if (!hasKey("main", "SendMessageToWebhook")) writeBooleanConfig("main", "SendMessageToWebhook", true);
        if (!hasKey("main", "Webhook")) writeStringConfig("main", "Webhook", "");
        if (!hasKey("main", "ReconnectDelay")) writeIntConfig("main", "ReconnectDelay", 20);
        if (!hasKey("main", "AuctionHouseDelay")) writeIntConfig("main", "AuctionHouseDelay", 8);
        if (!hasKey("main", "checkProfitPercentageBeforeBuy")) writeBooleanConfig("main", "checkProfitPercentageBeforeBuy", false);
        if (!hasKey("main", "ProfitPercentage")) writeIntConfig("main", "ProfitPercentage", 400); //400%
        if (!hasKey("main", "GUI")) writeBooleanConfig("main", "GUI", true);
        if (!hasKey("main", "GUI_COLOR")) writeIntConfig("main", "GUI_COLOR", 0x003153);
        for (int i = 1; i <= 10; i++) {
            if (!hasKey("item" + i, "Name")) writeStringConfig("item" + i, "Name", "");
            if (!hasKey("item" + i, "Type")) writeStringConfig("item" + i, "Type", "");
            if (!hasKey("item" + i, "Price")) writeIntConfig("item" + i, "Price", 0);
            if (!hasKey("item" + i, "Tier")) writeStringConfig("item" + i, "Tier", "");
        }
        for (int i = 1; i <= 10; i++) {
            if (!hasKey("blacklist" + i, "Name")) writeStringConfig("blacklist" + i, "Name", "");
            if (!hasKey("blacklist" + i, "Type")) writeStringConfig("blacklist" + i, "Type", "");
            if (!hasKey("blacklist" + i, "Price")) writeIntConfig("blacklist" + i, "Price", 0);
            if (!hasKey("blacklist" + i, "Tier")) writeStringConfig("blacklist" + i, "Tier", "");
        }
    }

    public static void init() {
        config = new Configuration(new File(file));
        try {
            config.load();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }


    public static int getInt(String category, String key) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, 0).getInt();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return 0;
    }

    public static double getDouble(String category, String key) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, 0D).getDouble();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return 0D;
    }

    public static String getString(String category, String key) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, "").getString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return "";
    }

    public static boolean getBoolean(String category, String key) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            if (config.getCategory(category).containsKey(key)) {
                return config.get(category, key, false).getBoolean();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return true;
    }

    public static void writeIntConfig(String category, String key, int value) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            int set = config.get(category, key, value).getInt();
            config.getCategory(category).get(key).set(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static void writeDoubleConfig(String category, String key, double value) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            double set = config.get(category, key, value).getDouble();
            config.getCategory(category).get(key).set(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static void writeStringConfig(String category, String key, String value) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            String set = config.get(category, key, value).getString();
            config.getCategory(category).get(key).set(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static void writeBooleanConfig(String category, String key, boolean value) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            boolean set = config.get(category, key, value).getBoolean();
            config.getCategory(category).get(key).set(value);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }

    public static boolean hasKey(String category, String key) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            if (!config.hasCategory(category)) return false;
            return config.getCategory(category).containsKey(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
        return false;
    }

    public static void deleteCategory(String category) {
        category = category.toLowerCase();
        config = new Configuration(new File(file));
        try {
            config.load();
            if (config.hasCategory(category)) {
                config.removeCategory(new ConfigCategory(category));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            config.save();
        }
    }
}
