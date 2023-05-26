package me.night0721.lilase.utils.capes;

import com.google.gson.JsonParser;
import me.night0721.lilase.Lilase;
import me.night0721.lilase.utils.HWIDUtils;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Credits: Gabagooooooooooool
 */
public class CapeDatabase {
    private final Map<String, ResourceLocation> cachedCapeTextures = new HashMap<>();

    public boolean userHasCape(String username) {
        return cachedCapeTextures.containsKey(username.toLowerCase());
    }

    public ResourceLocation getUserCape(String username) {
        return cachedCapeTextures.get(username.toLowerCase());
    }

    public void init() {
        try {
            URL url = new URL("https://api.night0721.me/api/v1/wl");
            StringBuilder result = new StringBuilder();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "night0721");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            new JsonParser().parse(result.toString()).getAsJsonArray().forEach(jsonElement -> {
                String id = jsonElement.getAsJsonObject().get("id").getAsString();
                if (!id.equals(HWIDUtils.getID())) return;
                String username = jsonElement.getAsJsonObject().get("username").getAsString();
                String capeUrl = jsonElement.getAsJsonObject().get("url").getAsString();
                try {
                    BufferedImage image = ImageIO.read(new URL(capeUrl));
                    DynamicTexture texture = new DynamicTexture(image);
                    cachedCapeTextures.put(username.toLowerCase(), Lilase.mc.getTextureManager().getDynamicTextureLocation("Lilas_" + username, texture));
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {

        }
    }
}