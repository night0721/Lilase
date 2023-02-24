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
        if (!hasKey("main", "Webhook")) writeStringConfig("main", "Webhook", "");
        if (!hasKey("main", "AuctionHouseDelay")) writeIntConfig("main", "AuctionHouseDelay", 8);
        if (!hasKey("main", "checkMultiplierBeforeBuy")) writeBooleanConfig("main", "checkMultiplierBeforeBuy", false);
        if (!hasKey("main", "Multiplier")) writeIntConfig("main", "Multiplier", 400); //400%
        if (!hasKey("item1", "Name")) writeStringConfig("item1", "Name", "");
        if (!hasKey("item1", "Type")) writeStringConfig("item1", "Type", "");
        if (!hasKey("item1", "Price")) writeIntConfig("item1", "Price", 0);
        if (!hasKey("item1", "Tier")) writeStringConfig("item1", "Tier", "");
        if (!hasKey("item2", "Name")) writeStringConfig("item2", "Name", "");
        if (!hasKey("item2", "Type")) writeStringConfig("item2", "Type", "");
        if (!hasKey("item2", "Price")) writeIntConfig("item2", "Price", 0);
        if (!hasKey("item2", "Tier")) writeStringConfig("item2", "Tier", "");
        if (!hasKey("item3", "Name")) writeStringConfig("item3", "Name", "");
        if (!hasKey("item3", "Type")) writeStringConfig("item3", "Type", "");
        if (!hasKey("item3", "Price")) writeIntConfig("item3", "Price", 0);
        if (!hasKey("item3", "Tier")) writeStringConfig("item3", "Tier", "");
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
