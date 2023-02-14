package me.night0721.lilase.utils.ah;

import me.night0721.lilase.utils.Utils;
import net.minecraft.client.Minecraft;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.night0721.lilase.Main.clickState;

public class AuctionHouse {
    private final OkHttpClient client;
    private static String uuid;
    private static String message_toSend;
    private final List<Item> items = new ArrayList<>();
    private final List<String> posted = new ArrayList<>();

    public AuctionHouse() {
        client = new OkHttpClient();
        // items.add(new Item("Livid Dagger", ItemType.WEAPON, 8000000, ItemTier.LEGENDARY));
        // items.add(new Item("Aspect of the Void", ItemType.WEAPON, 8000000, ItemTier.EPIC));
        // items.add(new Item("Bal", ItemType.MISC, 10000000, ItemTier.EPIC));
        items.add(new Item(" ", ItemType.MISC, 1000, ItemTier.UNCOMMON));
        Utils.sendMessage("AuctionHouse is now running");
        new Thread(() -> {
            while (true) {
                try {
                    getItem();
                    TimeUnit.SECONDS.sleep(8);
                } catch (IOException | JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private JSONObject getHypixelData(String player) throws IOException, JSONException {
        String API_KEY = "d800be32-3abc-49bd-83a7-188573853d49";
        Request request = new Request.Builder()
                .url("https://api.hypixel.net/player?key=" + API_KEY + "&uuid=" + player)
                .build();

        Response response = client.newCall(request).execute();
        if (response.code() == 200 && response.body() != null)
            return new JSONObject(response.body().string());
        else
            return null;

    }

    private void getItem() throws IOException, JSONException {
        Request request = new Request.Builder()
                .url("https://api.hypixel.net/skyblock/auctions")
                .build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200 || response.body() == null) return;
        JSONObject data = new JSONObject(response.body().string());
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
                if (!auction.getString("tier").equals(item.tier.name())) break;
                if (auction.getInt("starting_bid") > item.price) break;
                if (!auction.getString("category").equals(type.lowercase)) break;
                if (!auction.getBoolean("bin")) break;
                if (!posted.contains(auction.getString("item_uuid"))) {
                    posted.add(auction.getString("item_uuid"));
                    NumberFormat format = NumberFormat.getInstance(Locale.US);
                    JSONObject profile = getHypixelData(auction.getString("auctioneer"));
                    Pattern pattern = Pattern.compile("§[0-9a-z]", Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(auction.getString("item_lore"));
                    String updated = matcher.replaceAll("");
                    JSONObject message = new JSONObject();
                    JSONObject embed = new JSONObject();
                    JSONObject author = new JSONObject();
                    JSONObject footer = new JSONObject();
                    JSONArray fields = new JSONArray();
                    JSONObject item_field = new JSONObject();
                    JSONObject price_field = new JSONObject();
                    JSONObject seller_field = new JSONObject();
                    JSONObject started_field = new JSONObject();
                    JSONObject ends_field = new JSONObject();

                    author.put("name", "Ń1ght#4004");
                    author.put("url", "https://github.com/night0721");
                    author.put("icon_url", "https://avatars.githubusercontent.com/u/77528305?v=4");
                    footer.put("text", "Made by Ń1ght#4004");
                    footer.put("icon_url", "https://avatars.githubusercontent.com/u/77528305?v=4");
                    item_field.put("name", "Item");
                    item_field.put("value", auction.getString("item_name"));
                    item_field.put("inline", true);
                    price_field.put("name", "Price");
                    price_field.put("value", format.format(auction.getInt("starting_bid")));
                    price_field.put("inline", true);
                    seller_field.put("name", "Seller");
                    assert profile != null;
                    seller_field.put("value", profile.getJSONObject("player").getString("displayname"));
                    seller_field.put("inline", true);
                    started_field.put("name", "Started for");
                    started_field.put("value", toDuration(System.currentTimeMillis() - auction.getLong("start")));
                    started_field.put("inline", true);
                    ends_field.put("name", "Ends in");
                    ends_field.put("value", getTimeSinceDate(auction.getLong("end") - System.currentTimeMillis()));
                    ends_field.put("inline", true);
                    fields.put(item_field);
                    fields.put(price_field);
                    fields.put(seller_field);
                    fields.put(started_field);
                    fields.put(ends_field);
                    embed.put("color", 0x003153);
                    embed.put("title", "Item Is On Low Price");
                    embed.put("url", "https://www.brandonfowler.me/skyblockah/?uuid=" + auction.getString("uuid"));
                    embed.put("timestamp", System.currentTimeMillis() / 1000);
                    embed.put("description", updated);
                    embed.put("author", author);
                    embed.put("footer", footer);
                    embed.put("fields", fields);
                    message.put("username", "Skyblock AH");
                    message.put("embeds", new JSONArray().put(embed));
                    message.put("content", auction.getString("item_name") + " is sale at " + format.format(auction.getInt("starting_bid")) + "!\n" + "/viewauction " + auction.getString("uuid"));
                    sendMessage(message);
                    uuid = auction.getString("uuid");
                    message_toSend = "Auction House: " + auction.getString("item_name") + " is sale for " + format.format(auction.getInt("starting_bid")) + "!";
                    clickState = States.OPEN;
                    Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.openContainer.windowId, 31, 0, 0, Minecraft.getMinecraft().thePlayer);

                    return;
                }
            }
        }
    }

    public static void sendAuction() {
        Utils.sendServerMessage("/viewauction " + uuid);
        Utils.sendMessage(message_toSend);
    }

    public final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
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
        if ("".equals(res.toString()))
            return "0 seconds ago";
        else
            return res.toString();
    }

    private static String getTimeSinceDate(long timeSinceDate) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeSinceDate);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeSinceDate);
        long hours = TimeUnit.MILLISECONDS.toHours(timeSinceDate);
        long days = TimeUnit.MILLISECONDS.toDays(timeSinceDate);

        if (seconds < 60)
            return "in " + seconds + " seconds";
        else if (minutes < 60)
            return "in " + minutes + " minutes";
        else if (hours < 24)
            return "in " + hours + " hours";
        else
            return "in " + days + " days";

    }

    private void sendMessage(JSONObject data) throws IOException, JSONException {
        String DISCORD_WEBHOOK = "https://discord.com/api/webhooks/979502673093079071/p539WaqjEwiUWqCXLSBAcfDY-EhmF2RU9ZzjCKW_8jtFMuldJQwCdOFMPsT0U3VhfdBH";

        URL url = new URL(DISCORD_WEBHOOK);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-BY-Gelox_");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(data.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();

        connection.getInputStream().close(); //I'm not sure why but it doesn't work without getting the InputStream
        connection.disconnect();


        // Request request = new Request.Builder()
           //     .url(DISCORD_WEBHOOK)
             //   .post(RequestBody.create(data.toString(), MediaType.get("application/json")))
               // .addHeader("Content-Type", "application/json")
                //.build();
        //client.newCall(request).execute();
    }
}