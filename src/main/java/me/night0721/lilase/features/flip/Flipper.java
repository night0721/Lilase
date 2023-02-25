package me.night0721.lilase.features.flip;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.StringUtils;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class Flipper {
    private static String itemname = null;
    private final String bytedata;
    private final int itemprice;
    public static FlipperState state = FlipperState.NONE;
    public static final Rotation rotation = new Rotation();
    private static final Clock buyWait = new Clock();

    public Flipper(String name, String data, int price) {
        itemname = name;
        bytedata = data;
        itemprice = price;
    }

    public int getItemPrice() throws IOException {
        JSONObject item = getItemData();
        return (int) item.get("price");
    }

    public int checkMultiplier() throws IOException {
        JSONObject item = getItemData();
        return (int) item.get("price") / itemprice * 100;
    }

    public void sellItem() {
        Utils.sendMessage("Flipper is running, stopping, will resume when flipper is done");
        if (Lilase.auctionHouse.open) Lilase.auctionHouse.toggleAuction();
        UngrabUtils.ungrabMouse();
        Utils.sendServerMessage("/hub");
        state = FlipperState.WALKING_TO_FIRST_POINT;
    }

    public static void switchStates() {
        switch (state) {
            case WALKING_TO_FIRST_POINT:
                if (PlayerUtils.mc.currentScreen != null) {
                    PlayerUtils.mc.thePlayer.closeScreen();
                } else if (distanceToFirstPoint() < 0.7f) {
                    Utils.debugLog("[Flipper] Moving to auction house");
                    KeyBindingManager.updateKeys(false, false, false, false, false);
                    state = FlipperState.WALKING_INTO_AUCTION_HOUSE;
                } else if (distanceToFirstPoint() < 5f) {
                    Utils.debugLog("[Flipper] Crouching to point 1");
                    KeyBindingManager.updateKeys(true, false, false, false, false, true, false);
                } else {
                    KeyBindingManager.updateKeys(true, false, false, false, false);
                }
                break;
            case WALKING_INTO_AUCTION_HOUSE:
                if (PlayerUtils.mc.currentScreen != null) {
                    PlayerUtils.mc.thePlayer.closeScreen();
                } else if (AngleUtils.smallestAngleDifference(AngleUtils.get360RotationYaw(), 88f) > 1.2) {
                    Utils.debugLog("[Flipper] Rotating to Auction Master");
                    rotation.easeTo(88f, PlayerUtils.mc.thePlayer.rotationPitch, 500);
                } else if (distanceToAuctionMaster() < 0.7f) {
                    Utils.debugLog("[Flipper] At Auction Master, opening shop");
                    KeyBindingManager.updateKeys(false, false, false, false, false);
                    state = FlipperState.BUYING;
                } else if (distanceToAuctionMaster() < 5f) {
                    Utils.debugLog("[Flipper] Crouching to Auction Master");
                    KeyBindingManager.updateKeys(true, false, false, false, false, true, false);
                } else {
                    KeyBindingManager.updateKeys(true, false, false, false, false);
                }
                break;
            case BUYING:
                if (PlayerUtils.mc.currentScreen == null && buyWait.passed()) {
                    final Entity auctionMaster = getAuctionMaster();
                    if (auctionMaster == null) {
                        Utils.debugLog("[Flipper] Cannot find shop NPC, retrying");
                        buyWait.schedule(500);
                    } else {
                        PlayerUtils.mc.playerController.interactWithEntitySendPacket(PlayerUtils.mc.thePlayer, auctionMaster);
                        buyWait.schedule(1500);
                    }
                } else if (InventoryUtils.inventoryNameContains("Auction House") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(15);
                    buyWait.schedule(1500);
                } else if (InventoryUtils.inventoryNameContains("Create BIN Auction")) {
                    if (InventoryUtils.isStoneButton() && buyWait.passed()) {
                        if (InventoryUtils.getSlotForItem(itemname) == -1) {
                            Utils.sendMessage("Cannot find item in inventory, stopping flipper");
                            state = FlipperState.NONE;
                            Lilase.auctionHouse.open = true;
                            return;
                        }
                        InventoryUtils.clickOpenContainerSlot(InventoryUtils.getSlotForItem(itemname));
                        buyWait.schedule(1000);
                    } else if (!InventoryUtils.isStoneButton() && InventoryUtils.isToAuctionItem(itemname) && buyWait.passed()) {
                        InventoryUtils.clickOpenContainerSlot(31);
                        buyWait.schedule(1000);
                        state = FlipperState.START;
                    } else if (!InventoryUtils.isStoneButton() && !InventoryUtils.isToAuctionItem(itemname) && buyWait.passed()) {
                        InventoryUtils.clickOpenContainerSlot(13);
                        buyWait.schedule(1000);
                    }
                } else if (InventoryUtils.inventoryNameContains("Manage Auction") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(24);
                    buyWait.schedule(1500);
                }
            case START:
                if (!InventoryUtils.isStoneButton() && InventoryUtils.isToAuctionItem(itemname) && InventoryUtils.inventoryNameStartsWith("Create BIN Auction") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(29);
                    buyWait.schedule(1000);
                } else if (InventoryUtils.inventoryNameContains("Confirm BIN Auction") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(11);
                    buyWait.schedule(1000);
                } else if (InventoryUtils.inventoryNameContains("BIN Auction View") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(49);
                    PlayerUtils.mc.thePlayer.closeScreen();
                    Utils.sendMessage("Posted item on Auction House, continue sniping now");
                    state = FlipperState.NONE;
                    Lilase.auctionHouse.toggleAuction();
                }
            case NONE:
                break;
        }

    }

    public JSONObject getItemData() throws IOException {
        URL url = new URL("https://api.night0721.me/api/v1/skyblock");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        OutputStream stream = connection.getOutputStream();
        stream.write(("{\"ByteData\": \"" + bytedata + "\"}").getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();
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

    private static float distanceToFirstPoint() {
        return (float) Math.sqrt(Math.pow(PlayerUtils.mc.thePlayer.posX - (-2.5), 2) + Math.pow(PlayerUtils.mc.thePlayer.posZ - (-91.5), 2));
    }

    private static float distanceToAuctionMaster() {
        return (float) Math.sqrt(Math.pow(PlayerUtils.mc.thePlayer.posX - (-45), 2) + Math.pow(PlayerUtils.mc.thePlayer.posZ - (-90), 2));
    }

    private static Entity getAuctionMaster() {
        for (final Entity e : PlayerUtils.mc.theWorld.loadedEntityList) {
            if (e instanceof EntityArmorStand) {
                final String name = StringUtils.stripControlCodes(e.getDisplayName().getUnformattedText());
                if (name.startsWith("Auction Master")) {
                    return e;
                }
            }
        }
        return null;
    }
}
