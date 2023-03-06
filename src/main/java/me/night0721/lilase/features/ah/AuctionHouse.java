package me.night0721.lilase.features.ah;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.flip.Flipper;
import me.night0721.lilase.features.flip.FlipperState;
import me.night0721.lilase.utils.ConfigUtils;
import me.night0721.lilase.utils.DiscordWebhook;
import me.night0721.lilase.utils.UngrabUtils;
import me.night0721.lilase.utils.Utils;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static me.night0721.lilase.features.ah.AHConfig.SEND_MESSAGE;

public class AuctionHouse {
    private Boolean open = false;
    public Boolean buying = false;
    private int auctionsSniped = 0;
    private int auctionsPosted = 0;
    private int auctionsFlipped = 0;
    public final DiscordWebhook webhook = new DiscordWebhook(ConfigUtils.getString("main", "Webhook"));
    private final List<Item> items = new ArrayList<>();
    private final List<Item> blacklist = new ArrayList<>();
    private final List<String> posted = new ArrayList<>();
    private final ThreadLocalRandom randomSlot;
    public static Flipper flipper;

    public AuctionHouse() {
        for (int i = 1; i <= 99; i++) {
            if (!ConfigUtils.getString("item" + i, "Name").equals("") && !ConfigUtils.getString("item" + i, "Type").equals("") && !ConfigUtils.getString("item" + i, "Tier").equals("") && ConfigUtils.getInt("item" + i, "Price") != 0){
                try {
                    ItemType.valueOf(ConfigUtils.getString("item" + i, "Type"));
                    ItemType.valueOf(ConfigUtils.getString("item" + i, "Tier"));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid item type for item " + i + ", please check your config");
                }
                items.add(new Item(ConfigUtils.getString("item" + i, "Name"), ItemType.valueOf(ConfigUtils.getString("item" + i, "Type")), ConfigUtils.getInt("item" + i, "Price"), ItemTier.valueOf(ConfigUtils.getString("item" + i, "Tier"))));
            }
        }
        for (int i = 1; i <= 99; i++) {
            if (!ConfigUtils.getString("blacklist" + i, "Name").equals(""))
                blacklist.add(new Item(ConfigUtils.getString("blacklist" + i, "Name"), null, null, null));
        }
        webhook.setUsername("Lilase - Auction House");
        webhook.setAvatarUrl("https://th.bing.com/th/id/OIP.Lk2cSujieY70GbsgPZ0TyAHaEK?w=325&h=182&c=7&r=0&o=5&pid=1.7");
        webhook.setTts(false);
        randomSlot = ThreadLocalRandom.current();
    }

    private JsonObject getHypixelData(String player) throws IOException {
        URL url = new URL("https://api.hypixel.net/player?key=" + ConfigUtils.getString("main", "APIKey") + "&uuid=" + player);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return (JsonObject) new JsonParser().parse(content.toString());
    }

    public void getItem() throws IOException {
        if (!open) return;
        if (!Utils.checkInHub()) {
            Utils.sendMessage("Not in hub, stopping");
            open = false;
            return;
        }
        if (items.size() == 0) {
            Utils.sendMessage("No Item queued, stopping");
            open = false;
            return;
        }
        if (ConfigUtils.getString("main", "APIKey").equals("") || ConfigUtils.getString("main", "Webhook").equals("")) {
            Utils.sendMessage("Missing APIKey, stopping");
            open = false;
            return;
        }
        if (SEND_MESSAGE && ConfigUtils.getString("main", "Webhook").equals("")) {
            Utils.sendMessage("Sending message to Webhook is on but Webhook is missing, stopping");
            open = false;
            return;
        }
        if (Flipper.state != FlipperState.NONE) {
            Utils.sendMessage("Flipper is running, stopping");
            open = false;
            return;
        }
        if (Lilase.mc.currentScreen != null) Lilase.mc.thePlayer.closeScreen();
        Utils.debugLog("[Sniper] Doing some motion as we don't want to be AFK");
        Lilase.mc.thePlayer.inventory.currentItem = randomSlot.nextInt(9);
        URL url = new URL("https://api.hypixel.net/skyblock/auctions");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        int status = connection.getResponseCode();
        if (status != 200) {
            Utils.sendMessage("Error getting data from Hypixel API, either API down or internal error");
            return;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        Utils.sendMessage("Getting item from auction house");
        JsonObject data = (JsonObject) new JsonParser().parse(content.toString());
        JsonArray auctions = data.getAsJsonArray("auctions");
        for (int i = 0; i < auctions.size(); i++) {
            JsonObject auction = auctions.get(i).getAsJsonObject();
            for (Item item : items) {
                String lore = " ";
                ItemType type = item.type;
                String itemName = auction.get("item_name").getAsString();
                String uuid = auction.get("uuid").getAsString();
                Integer price = auction.get("starting_bid").getAsInt();
                boolean found = false;
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
                if (posted.contains(uuid)) break;
                for (Item blacklisted : blacklist) {
                    if (itemName.contains(blacklisted.getQuery())) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
                if (!itemName.toLowerCase().contains(item.query.toLowerCase())) break;
                if (!auction.get("item_lore").getAsString().contains(lore)) break;
                if (price > item.price) break;
                if (item.tier != ItemTier.ANY) if (!auction.get("tier").getAsString().equals(item.tier.name())) break;
                if (type != ItemType.ANY) if (!auction.get("category").getAsString().equals(type.getLowercase())) break;
                if (!auction.get("bin").getAsBoolean()) break;
                if (!posted.contains(uuid)) {
                    posted.add(uuid);
                    flipper = new Flipper(itemName, auction.get("item_bytes").getAsString(), price);
                    NumberFormat format = NumberFormat.getInstance(Locale.US);
                    JsonObject profile = getHypixelData(auction.get("auctioneer").getAsString());
                    String profileName = profile.get("player").getAsJsonObject().get("displayname").getAsString();
                    if (profileName.equalsIgnoreCase(Lilase.mc.thePlayer.getName())) break;
                    String updated = auction.get("item_lore").getAsString().replaceAll("§[0-9a-z]", "");
                    DecimalFormat df = new DecimalFormat("#.##");
                    if (ConfigUtils.getBoolean("main", "checkProfitPercentageBeforeBuy")) {
                        float multi = flipper.checkProfitPercentage();
                        Utils.debugLog("[Sniper] Found an item, checking profit percentage");
                        if (multi > ConfigUtils.getInt("main", "ProfitPercentage")) {
                            Utils.debugLog("[Sniper] Higher than required profit percentage, buying now");
                            buying = true;
                        }
                    } else {
                        Utils.debugLog("[Sniper] Found an item, trying to buy");
                        buying = true;
                    }
                    if (buying) {
                        Utils.sendServerMessage("/viewauction " + uuid);
                        webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Bought an item on low price").setUrl("https://sky.coflnet.com/auction/" + uuid).setAuthor("night0721", "https://github.com/night0721", "https://avatars.githubusercontent.com/u/77528305?v=4").setDescription(updated.replace("\n", "\\n")).addField("Item", itemName, true).addField("Price", format.format(price) + " coins", true).addField("Profit", format.format(flipper.getItemPrice() - price) + " coins", true).addField("Profit Percentage", Float.parseFloat(df.format(flipper.getItemPrice() - price)) + "%", true).addField("Seller", profileName, true).addField("Started for", toDuration(System.currentTimeMillis() - auction.get("start").getAsLong()), true).addField("Ends in", getTimeSinceDate(auction.get("end").getAsLong() - System.currentTimeMillis()), true).setColor(Color.decode("#003153")));
                        webhook.setContent(itemName + " is sale at " + format.format(price) + "!   `" + "/viewauction " + uuid + "`");
                    }
                    return;
                }
            }
        }
    }


    private final List<Long> times = Arrays.asList(TimeUnit.DAYS.toMillis(365), TimeUnit.DAYS.toMillis(30), TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(1), TimeUnit.SECONDS.toMillis(1));
    private final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");

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

    private String getTimeSinceDate(long timeSinceDate) {
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
            Utils.sendMessage("Stopped AH Sniper");
            open = false;
            UngrabUtils.regrabMouse();
        } else {
            if (Utils.checkInHub()) {
                Utils.sendMessage("Started AH Sniper");
                open = true;
                UngrabUtils.ungrabMouse();
            } else Utils.sendMessage("Detected not in hub, please go to hub to start");
        }
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public int getAuctionsSniped() {
        return auctionsSniped;
    }

    public void incrementAuctionsSniped() {
        this.auctionsSniped += 1;
    }

    public int getAuctionsPosted() {
        return auctionsPosted;
    }

    public void incrementAuctionsPosted() {
        this.auctionsPosted += 1;
    }

    public int getAuctionsFlipped() {
        return auctionsFlipped;
    }

    public void incrementAuctionsFlipped() {
        this.auctionsFlipped += 1;
    }
}


enum ItemTier {
    ANY, COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY_SPECIAL
}

enum ItemType {
    ANY("any"), WEAPON("weapon"), ARMOR("armor"), ACCESSORIES("accessories"), CONSUMABLES("consumables"), BLOCKS("blocks"), MISC("misc");

    public final String lowercase;

    public String getLowercase() {
        return lowercase;
    }

    ItemType(String lowercase) {
        this.lowercase = lowercase;
    }
}

class Item {
    public final String query;
    public final ItemType type;
    public final Integer price;
    public final ItemTier tier;

    public Item(String query, ItemType type, Integer price, ItemTier tier) {
        this.query = query;
        this.type = type;
        this.price = price;
        this.tier = tier;
    }

    public String getQuery() {
        return query;
    }

    public ItemType getType() {
        return type;
    }

    public Integer getPrice() {
        return price;
    }

    public ItemTier getTier() {
        return tier;
    }
}