package me.night0721.lilase.features.ah;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.flip.Flipper;
import me.night0721.lilase.utils.*;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuctionHouse {
    private String uuid;
    private String message_toSend;
    private Boolean open = false;
    private int auctionsSniped = 0;
    public DiscordWebhook webhook = new DiscordWebhook(ConfigUtils.getString("main", "Webhook"));
    private final List<Item> items = new ArrayList<>();
    private final List<String> posted = new ArrayList<>();
    public static Flipper flipper;

    public AuctionHouse() {
        if (!ConfigUtils.getString("item1", "Name").equals("") && !ConfigUtils.getString("item1", "Type").equals("") && !ConfigUtils.getString("item1", "Tier").equals("") && ConfigUtils.getInt("item1", "Price") != 0)
            items.add(new Item(ConfigUtils.getString("item1", "Name"), ItemType.valueOf(ConfigUtils.getString("item1", "Type")), ConfigUtils.getInt("item1", "Price"), ItemTier.valueOf(ConfigUtils.getString("item1", "Tier"))));
        if (!ConfigUtils.getString("item2", "Name").equals("") && !ConfigUtils.getString("item2", "Type").equals("") && !ConfigUtils.getString("item2", "Tier").equals("") && ConfigUtils.getInt("item2", "Price") != 0)
            items.add(new Item(ConfigUtils.getString("item2", "Name"), ItemType.valueOf(ConfigUtils.getString("item2", "Type")), ConfigUtils.getInt("item2", "Price"), ItemTier.valueOf(ConfigUtils.getString("item2", "Tier"))));
        if (!ConfigUtils.getString("item3", "Name").equals("") && !ConfigUtils.getString("item3", "Type").equals("") && !ConfigUtils.getString("item3", "Tier").equals("") && ConfigUtils.getInt("item3", "Price") != 0)
            items.add(new Item(ConfigUtils.getString("item3", "Name"), ItemType.valueOf(ConfigUtils.getString("item3", "Type")), ConfigUtils.getInt("item3", "Price"), ItemTier.valueOf(ConfigUtils.getString("item3", "Tier"))));
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
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return new JSONObject(content.toString());
    }

    private float generateRandomFloat() {
        return (float) (ThreadLocalRandom.current().nextFloat() * 180);
    }

    public void getItem() throws IOException, JSONException {
        if (open == false) return;
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
            Utils.sendMessage("Missing APIKey or Webhook, stopping");
            open = false;
            return;
        }
        Utils.debugLog("[Sniper] Randomizing motion as we don't want to be AFK");
        KeyBindingManager.leftClick();
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
        Utils.sendMessage("Getting item from auction house");
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
                if (posted.contains(auction.getString("uuid"))) break;
                if (!auction.getString("item_name").toLowerCase().contains(item.query.toLowerCase())) break;
                if (!auction.getString("item_lore").contains(lore)) break;
                if (auction.getInt("starting_bid") > item.price) break;
                if (item.tier != ItemTier.ANY) if (!auction.getString("tier").equals(item.tier.name())) break;
                if (type != ItemType.ANY) if (!auction.getString("category").equals(type.lowercase)) break;
                if (!auction.getBoolean("bin")) break;
                if (!posted.contains(auction.getString("uuid"))) {
                    posted.add(auction.getString("uuid"));
                    flipper = new Flipper(auction.getString("item_name"), auction.getString("item_bytes"), auction.getInt("starting_bid"));
                    NumberFormat format = NumberFormat.getInstance(Locale.US);
                    JSONObject profile = getHypixelData(auction.getString("auctioneer"));
                    if (profile.getJSONObject("player").getString("displayname").toLowerCase() == Lilase.mc.thePlayer.getName().toLowerCase())
                        break;
                    Pattern pattern = Pattern.compile("§[0-9a-z]", Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(auction.getString("item_lore"));
                    String updated = matcher.replaceAll("");
                    webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Bought an item on low price").setUrl("https://sky.coflnet.com/auction/" + auction.getString("uuid")).setAuthor("night0721", "https://github.com/night0721", "https://avatars.githubusercontent.com/u/77528305?v=4").setDescription(updated.replace("\n", "\\n")).addField("Item", auction.getString("item_name"), true).addField("Price", format.format(auction.getInt("starting_bid")) + " coins", true).addField("Seller", profile.getJSONObject("player").getString("displayname"), true).addField("Started for", toDuration(System.currentTimeMillis() - auction.getLong("start")), true).addField("Ends in", getTimeSinceDate(auction.getLong("end") - System.currentTimeMillis()), true).setColor(Color.decode("#003153")));
                    webhook.setContent(auction.getString("item_name") + " is sale at " + format.format(auction.getInt("starting_bid")) + "!   `" + "/viewauction " + auction.getString("uuid") + "`");
                    uuid = auction.getString("uuid");
                    message_toSend = "Auction House: " + auction.getString("item_name") + " is sale for " + format.format(auction.getInt("starting_bid")) + "!";
                    if (ConfigUtils.getBoolean("main", "checkMultiplierBeforeBuy")) {
                        float multi = flipper.checkMultiplier();
                        Utils.debugLog("[Sniper] Found an item, checking profit multiplier");
                        if (multi > ConfigUtils.getInt("main", "Multiplier")) {
                            Utils.debugLog("[Sniper] Higher than required multiplier, buying now");
                            sendAuction();
                        }
                    } else {
                        Utils.debugLog("[Sniper] Found an item, trying to buy");
                        sendAuction();
                    }
                    return;
                }
            }
        }
    }

    private void sendAuction() {
        Utils.sendServerMessage("/viewauction " + uuid);
        Utils.sendMessage(message_toSend);
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
}