package me.night0721.lilase.features.flip;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Flipper {
    public static List<JSONObject> same_items = new ArrayList<>();
    private static String[] SWORD_REFORGE_NAMES = new String[]{"Epic", "Fair", "Fast", "Gentle", "Heroic", "Legendary", "Odd", "Sharp", "Spicy", "Strong", "Coldfused", "Dirty", "Fabled", "Glided", "Suspicious", "Warped", "Withered", "Bulky", "Jerry's"};
    private static String[] ARMOR_REFORGE_NAMES = new String[]{"Clean", "Fierce", "Heavy", "Light", "Mythic", "Pure", "Titanic", "Smart", "Wise", "Candied", "Submerged", "Perfect", "Reinforced", "Renowned", "Spiked", "Hyper", "Giant", "Jaded", "Cubic", "Necrotic", "Empowered", "Ancient", "Undead", "Loving", "Ridiculous"};
    private static String reforge;
    public static void walkToAuctionHouse() {

    }

    public static void walkToBank() {

    }

    public static void sellItem() {

    }

    public static void calculateProfit(JSONObject item) throws IOException {
        URL url = new URL("https://api.hypixel.net/skyblock/auctions");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        int status = connection.getResponseCode();
        if (status != 200) return;
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        JSONObject data = new JSONObject(content.toString());
        JSONArray auctions = data.getJSONArray("auctions");
        for (int i = 0; i < auctions.length(); i++) {
            JSONObject auction = auctions.getJSONObject(i);
            if (auction.getString("item_name").contains(item.getString("name"))) {
                same_items.add(auction);
//                if (auction.getInt("starting_bid") < item.getInt("price")) {
//                    System.out.println("Profitable");
//                }
            }
        }

        // check reforge
        switch (item.getString("type")) {
            case "weapon":
                for (String reforge_name : SWORD_REFORGE_NAMES) {
                    if (item.getString("name").contains(reforge_name)) {
                        for (JSONObject auction : same_items) {
                            if (auction.getString("item_name").split(" ")[0].equals(reforge_name)) {
                                reforge = reforge_name;
                            }
                        }
                    }
                }
                break;
            case "armor":
                for (String reforge_name : ARMOR_REFORGE_NAMES) {
                    if (item.getString("name").contains(reforge_name)) {
                        for (JSONObject auction : same_items) {
                            if (auction.getString("item_name").split(" ")[0].equals(reforge_name)) {
                                reforge = reforge_name;
                            }
                        }
                    }
                }
                break;
        }
        // check the median price on skyblock every 5 minutes, if it rise then put the item in median price

    }
}
