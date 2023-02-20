package me.night0721.lilase.utils;

import net.minecraft.client.Minecraft;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuctionHouse {
    private static String uuid;
    private static String message_toSend;
    private static long lastAction;
    private Thread thread;
    private Boolean open = false;
    private DiscordWebhook webhook;
    private final List<Item> items = new ArrayList<>();
    private final List<String> posted = new ArrayList<>();
    public static States clickState = States.NONE;

    public AuctionHouse() {
        // items.add(new Item("Livid Dagger", ItemType.WEAPON, 8000000, ItemTier.LEGENDARY));
        // items.add(new Item("Aspect of the Void", ItemType.WEAPON, 8000000, ItemTier.EPIC));
        // items.add(new Item("Bal", ItemType.MISC, 10000000, ItemTier.EPIC));
        items.add(new Item(" ", ItemType.ANY, 1000, ItemTier.ANY));
        webhook = new DiscordWebhook(ConfigUtils.getString("main", "Webhook"));
        webhook.setUsername("Lilase - Auction House");
        webhook.setAvatarUrl("https://wallpapercave.com/wp/wp2412537.jpg");
    }

    private JSONObject getHypixelData(String player) throws IOException, JSONException {
        URL url = new URL("https://api.hypixel.net/player?key=" + ConfigUtils.getString("main", "APIKey") + "&uuid=" + player);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return new JSONObject(content.toString());
    }

    private void getItem() throws IOException, JSONException {
        URL url = new URL("https://api.hypixel.net/skyblock/auctions");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        int status = connection.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        if (status != 200) return;
        JSONObject data = new JSONObject(content.toString());
        JSONArray auctions = data.getJSONArray("auctions");
        for (int i = 0; i < auctions.length(); i++) {
            JSONObject auction = auctions.getJSONObject(i);
            for (Item item : items) {
                String lore = " ";
                ItemType type = item.type;
                switch (item.query) {
                    case "Bal":
                        lore = "Made of Lava";
                        break;
                    case "Squid":
                        lore = "More Ink";
                        break;
                    case "Monkey":
                        lore = "Treeborn";
                        break;
                    case "Ocelot":
                        lore = "Tree Hugger";
                        break;
                    case "Dolphin":
                        lore = "Echolocation";
                        break;
                    case "Flying Fish":
                        lore = "Water Bender";
                        break;
                }

                if (!auction.getString("item_name").toLowerCase().contains(item.query.toLowerCase())) break;
                if (!auction.getString("item_lore").contains(lore)) break;
                if (auction.getInt("starting_bid") > item.price) break;
                if (item.tier != ItemTier.ANY) if (!auction.getString("tier").equals(item.tier.name())) break;
                if (type != ItemType.ANY) if (!auction.getString("category").equals(type.lowercase)) break;
                if (!auction.getBoolean("bin")) break;
                if (!posted.contains(auction.getString("uuid"))) {
                    posted.add(auction.getString("uuid"));
                    NumberFormat format = NumberFormat.getInstance(Locale.US);
                    JSONObject profile = getHypixelData(auction.getString("auctioneer"));
                    Pattern pattern = Pattern.compile("§[0-9a-z]", Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(auction.getString("item_lore"));
                    String updated = matcher.replaceAll("");
                    webhook.addEmbed(
                            new DiscordWebhook.EmbedObject()
                                    .setTitle("Item Is On Low Price")
                                    .setAuthor("night0721", "https://github.com/night0721", "https://avatars.githubusercontent.com/u/77528305?v=4")
                                    .setDescription(updated)
                                    .addField("Item", auction.getString("item_name"), true)
                                    .addField("Price", format.format(auction.getInt("starting_bid")) + " coins", true)
                                    .addField("Seller", profile.getJSONObject("player").getString("displayname"), true)
                                    .addField("Started for", toDuration(System.currentTimeMillis() - auction.getLong("start")), true)
                                    .addField("Ends in", getTimeSinceDate(auction.getLong("end") - System.currentTimeMillis()), true)
                                    .setUrl("https://www.brandonfowler.me/skyblockah/?uuid=" + auction.getString("uuid"))
                                    .setColor(Color.decode("#003153"))
                    );
                    webhook.setContent(auction.getString("item_name") + " is sale at " + format.format(auction.getInt("starting_bid")) + "!\n`" + "/viewauction " + auction.getString("uuid") + "`");
                    new Thread(() -> {
                        try {
                            webhook.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    uuid = auction.getString("uuid");
                    message_toSend = "Auction House: " + auction.getString("item_name") + " is sale for " + format.format(auction.getInt("starting_bid")) + "!";
                    clickState = States.OPEN;
                    return;
                }
            }
        }
    }

    public static void sendAuction() {
        Utils.sendServerMessage("/viewauction " + uuid);
        Utils.sendMessage(message_toSend);
    }

    public final List<Long> times = Arrays.asList(TimeUnit.DAYS.toMillis(365), TimeUnit.DAYS.toMillis(30), TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(1), TimeUnit.SECONDS.toMillis(1));
    public final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");

    public String toDuration(long duration) {

        StringBuilder res = new StringBuilder();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(" ").append(timesString.get(i)).append(temp != 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if ("".equals(res.toString())) return "0 seconds ago";
        else return res.toString();
    }

    private static String getTimeSinceDate(long timeSinceDate) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeSinceDate);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeSinceDate);
        long hours = TimeUnit.MILLISECONDS.toHours(timeSinceDate);
        long days = TimeUnit.MILLISECONDS.toDays(timeSinceDate);

        if (seconds < 60) return "in " + seconds + " seconds";
        else if (minutes < 60) return "in " + minutes + " minutes";
        else if (hours < 24) return "in " + hours + " hours";
        else return "in " + days + " days";

    }

    public void toggleAuction() {
        if (open) {
            Utils.sendMessage("Stopped Auction House");
            thread.stop();
            open = false;
        } else {
            Utils.sendMessage("Started Auction House");
            thread = new Thread(() -> {
                while (true) {
                    try {
                        getItem();
                        thread.sleep(TimeUnit.SECONDS.toMillis(ConfigUtils.getInt("main", "AuctionHouseDelay")));
                    } catch (IOException | JSONException | InterruptedException ignore) {
                    }
                }
            });
            thread.run(); //You didn't participate in this auction!
            open = true;
        }
    }

    public static void switchStates() {
        switch (clickState) {
            case CLICK:
                if (System.currentTimeMillis() - lastAction < 500) return;
                Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.openContainer.windowId, 31, 0, 0, Minecraft.getMinecraft().thePlayer);
                lastAction = System.currentTimeMillis();
                clickState = States.CONFIRM;
                break;
            case CONFIRM:
                if (System.currentTimeMillis() - lastAction < 500) return;
                Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.openContainer.windowId, 11, 0, 0, Minecraft.getMinecraft().thePlayer);
                clickState = States.NONE;
                break;
            case OPEN:
                AuctionHouse.sendAuction();
                lastAction = System.currentTimeMillis();
                clickState = States.CLICK;
            case NONE:
                break;
        }
    }
}

enum States {
    OPEN, NONE, CLICK, CONFIRM
}

enum ItemTier {
    ANY, COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY_SPECIAL,
}

enum ItemType {
    ANY("any"), WEAPON("weapon"), ARMOR("armor"), ACCESSORIES("accessories"), CONSUMABLES("consumables"), BLOCKS("blocks"), MISC("misc"),
    ;
    public final String lowercase;

    public String getLowercase() {
        return lowercase;
    }

    ItemType(String lowercase) {
        this.lowercase = lowercase;
    }
}

class Item {
    public String query;
    public ItemType type;
    public Integer price;
    public ItemTier tier;

    public Item(String query, ItemType type, Integer price, ItemTier tier) {
        this.query = query;
        this.type = type;
        this.price = price;
        this.tier = tier;
    }
}