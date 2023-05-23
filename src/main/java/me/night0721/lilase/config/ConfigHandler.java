package me.night0721.lilase.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class ConfigHandler {
    private final Gson gson = new Gson();
    private final String path = "/config/Lilase.json";
    private JsonObject config;

    public void init() {
        config = readConfigFile();
        reloadConfig();
    }

    public void reloadConfig() {
        // TODO: Fix
        if (hasNoKey("SendMessageToWebhook")) setBoolean("SendMessageToWebhook", true);
        if (hasNoKey("Webhook")) setString("Webhook", "");
        if (hasNoKey("ReconnectDelay")) setInt("ReconnectDelay", 20);
        if (hasNoKey("AuctionHouseDelay")) setInt("AuctionHouseDelay", 8);
        if (hasNoKey("SniperMode")) setBoolean("SniperMode", true);
        if (hasNoKey("BedSpam")) setBoolean("BedSpam", true);
        if (hasNoKey("BedSpamDelay")) setInt("BedSpamDelay", 100);
        if (hasNoKey("OnlySniper")) setBoolean("OnlySniper", false);
        if (hasNoKey("GUI")) setBoolean("GUI", true);
        if (hasNoKey("GUI_COLOR")) setInt("GUI_COLOR", 0x003153);
        if (hasNoKey("Debug")) setBoolean("Debug", false);
        if (hasNoKey("RelistTimeout")) setInt("RelistTimeout", 1500);
        if (hasNoKey("AuctionLength")) setInt("AuctionLength", 2);
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

    public String getString(String key) {
        config = readConfigFile();
        if (config.get(key) != null) return config.get(key).getAsString();
        return null;
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