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
        if (hasNoKey("Webhook")) setString("Webhook", "");
        if (hasNoKey("RemoteControl")) setBoolean("RemoteControl", false);
        if (hasNoKey("BotToken")) setString("BotToken", "");
        if (hasNoKey("LogChannel")) setString("LogChannel", "");
    }

    public JsonObject readConfigFile() {
        try {
            File configFile = new File(System.getProperty("user.dir") + path);
            if (configFile.exists()) return new JsonParser().parse(new FileReader(configFile)).getAsJsonObject();
            else {
                boolean success = configFile.createNewFile();
                if (!success) {
                    System.out.println("Failed to create config file");
                }
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

    public boolean getBoolean(String key) {
        config = readConfigFile();
        if (config.get(key) != null) return config.get(key).getAsBoolean();
        return false;
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

    public void setFloat(String key, float value) {
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