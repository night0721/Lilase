package me.night0721.lilase.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.night0721.lilase.utils.Utils;

import java.io.*;

public class ConfigHandler {
    private final Gson gson = new Gson();
    String path = "/config/Lilase.json";
    private JsonObject config;

    public void init() {
        config = readConfigFile();
        reloadConfig();
    }

    public void reloadConfig() {
        if (hasNoKey("APIKey")) setString("APIKey", "");
        if (hasNoKey("SendMessageToWebhook")) setBoolean("SendMessageToWebhook", true);
        if (hasNoKey("Webhook")) setString("Webhook", "");
        if (hasNoKey("ReconnectDelay")) setInt("ReconnectDelay", 20);
        if (hasNoKey("AuctionHouseDelay")) setInt("AuctionHouseDelay", 8);
        if (hasNoKey("SniperMode")) setBoolean("SniperMode", true);
        if (hasNoKey("BedSpam")) setBoolean("BedSpam", true);
        if (hasNoKey("BedSpamDelay")) setInt("BedSpamDelay", 100);
        if (hasNoKey("OnlySniper")) setBoolean("OnlySniper", false);
        if (hasNoKey("checkProfitPercentageBeforeBuy")) setBoolean("checkProfitPercentageBeforeBuy", false);
        if (hasNoKey("checkMaximumProfitPercentageBeforeBuy"))
            setBoolean("checkMaximumProfitPercentageBeforeBuy", false);
        if (hasNoKey("MaximumProfitPercentage")) setInt("MaximumProfitPercentage", 1000); //1000%
        if (hasNoKey("MinimumProfitPercentage")) setInt("MinimumProfitPercentage", 400); //400%
        if (hasNoKey("GUI")) setBoolean("GUI", true);
        if (hasNoKey("GUI_COLOR")) setInt("GUI_COLOR", 0x003153);
        if (hasNoKey("items")) setArray("items", new JsonObject[]{});
        if (hasNoKey("blacklist")) setArray("blacklist", new JsonObject[]{});
        for (int i = 1; i <= 3; i++) {
            if (hasNoItemKey(i, "Name")) setItems(i, "Name", "");
            if (hasNoItemKey(i, "Type")) setItems(i, "Type", "");
            if (hasNoItemKey(i, "Price")) setItems(i, "Price", 0);
            if (hasNoItemKey(i, "Tier")) setItems(i, "Tier", "");
        }
        for (int i = 1; i <= 3; i++) {
            if (hasNoBlacklistKey(i, "Name")) setBlacklists(i, "Name", "");
            if (hasNoBlacklistKey(i, "Type")) setBlacklists(i, "Type", "");
            if (hasNoBlacklistKey(i, "Price")) setBlacklists(i, "Price", 0);
            if (hasNoBlacklistKey(i, "Tier")) setBlacklists(i, "Tier", "");
        }
    }

    public void checkWebhookAndAPI() {
        if (getString("APIKey").equals("") || getString("Webhook").equals("")) {
            Utils.sendMessage("API Key or Webhook is not set, please set it in the menu (Press *)");
        }
    }

    public JsonObject readConfigFile() {
        try {
            File configFile = new File(System.getProperty("user.dir") + path);
            if (configFile.exists()) return new JsonParser().parse(new FileReader(configFile)).getAsJsonObject();
            else {
                configFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
                writer.write("{}");
                writer.close();
                return new JsonObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasNoKey(String key) {
        config = readConfigFile();
        return config.get(key) == null;
    }

    public boolean hasNoItemKey(int i, String key) {
        config = readConfigFile();
        try {
            if (config.getAsJsonArray("items").get(i - 1) == null) return true;
        } catch (Exception e) {
            return true;
        }
        return config.getAsJsonArray("items").get(i - 1).getAsJsonObject().get(key) == null;
    }

    public boolean hasNoBlacklistKey(int i, String key) {
        config = readConfigFile();
        try {
            if (config.getAsJsonArray("blacklist").get(i - 1) == null) return true;
        } catch (Exception e) {
            return true;
        }
        return config.getAsJsonArray("blacklist").get(i - 1).getAsJsonObject().get(key) == null;
    }

    public boolean getBoolean(String key) {
        config = readConfigFile();
        if (config.get(key) != null) return config.get(key).getAsBoolean();
        return false;
    }

    public int getInt(String key) {
        config = readConfigFile();
        if (config.get(key) != null) return config.get(key).getAsInt();
        return 0;
    }

    public String getString(String key) {
        config = readConfigFile();
        if (config.get(key) != null) return config.get(key).getAsString();
        return null;
    }

    public String getString(int item, String key) {
        config = readConfigFile();
        try {
            if (config.getAsJsonArray("items").get(item - 1).getAsJsonObject().get(key) != null)
                return config.getAsJsonArray("items").get(item - 1).getAsJsonObject().get(key).getAsString();
        } catch (Exception ignore) {
        }
        return "";
    }

    public int getInt(int item, String key) {
        config = readConfigFile();
        try {
            if (config.getAsJsonArray("items").get(item - 1).getAsJsonObject().get(key) != null)
                return config.getAsJsonArray("items").get(item - 1).getAsJsonObject().get(key).getAsInt();
        } catch (Exception ignore) {
        }
        return 0;
    }

    public void setBoolean(String key, boolean value) {
        config = readConfigFile();
        if (config.get(key) != null) config.remove(key);
        config.addProperty(key, value);
        writeJsonToFile(config);
    }

    public void setInt(String key, int value) {
        config = readConfigFile();
        if (config.get(key) != null) config.remove(key);
        config.addProperty(key, value);
        writeJsonToFile(config);
    }

    public void setString(String key, String value) {
        config = readConfigFile();
        if (config.get(key) != null) config.remove(key);
        config.addProperty(key, value);
        writeJsonToFile(config);
    }

    public void setArray(String key, JsonObject[] value) {
        config = readConfigFile();
        if (config.get(key) != null) config.remove(key);
        JsonArray array = new JsonArray();
        for (JsonObject object : value) {
            array.add(object);
        }
        config.add(key, array);
        writeJsonToFile(config);
    }

    public void setItems(int item, String key, Object value) {
        config = readConfigFile();
        try {
            if (config.getAsJsonArray("items").get(item - 1).getAsJsonObject().get(key) != null)
                config.getAsJsonArray("items").get(item - 1).getAsJsonObject().remove(key);
        } catch (Exception e) {
            JsonObject object = new JsonObject();
            object.addProperty("Name", "");
            object.addProperty("Type", "");
            object.addProperty("Price", 0);
            object.addProperty("Tier", "");
            config.getAsJsonArray("items").add(object);
        }
        if (value instanceof Integer)
            config.getAsJsonArray("items").get(item - 1).getAsJsonObject().addProperty(key, (Integer) value);
        else if (value instanceof String)
            config.getAsJsonArray("items").get(item - 1).getAsJsonObject().addProperty(key, (String) value);
        else if (value instanceof Boolean)
            config.getAsJsonArray("items").get(item - 1).getAsJsonObject().addProperty(key, (Boolean) value);
        writeJsonToFile(config);
    }

    public void setBlacklists(int item, String key, Object value) {
        config = readConfigFile();
        try {
            if (config.getAsJsonArray("blacklist").get(item - 1).getAsJsonObject().get(key) != null)
                config.getAsJsonArray("blacklist").get(item - 1).getAsJsonObject().remove(key);
        } catch (Exception e) {
            JsonObject object = new JsonObject();
            object.addProperty("Name", "");
            object.addProperty("Type", "");
            object.addProperty("Price", 0);
            object.addProperty("Tier", "");
            config.getAsJsonArray("blacklist").add(object);
        }
        if (value instanceof Integer)
            config.getAsJsonArray("blacklist").get(item - 1).getAsJsonObject().addProperty(key, (Integer) value);
        else if (value instanceof String)
            config.getAsJsonArray("blacklist").get(item - 1).getAsJsonObject().addProperty(key, (String) value);
        else if (value instanceof Boolean)
            config.getAsJsonArray("blacklist").get(item - 1).getAsJsonObject().addProperty(key, (Boolean) value);
        writeJsonToFile(config);
    }

    public void writeJsonToFile(JsonObject jsonObject) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + path));
            gson.toJson(jsonObject, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}