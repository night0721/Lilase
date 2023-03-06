package me.night0721.lilase.features.flip;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.night0721.lilase.Lilase;
import me.night0721.lilase.events.SniperFlipperEvents;
import me.night0721.lilase.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.StringUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;


public class Flipper {
    private final String itemname;
    private final String bytedata;
    private final int itemprice;
    public static FlipperState state = FlipperState.NONE;
    public static final Rotation rotation = new Rotation();
    private final Clock buyWait = new Clock();
    private JsonObject object;

    public Flipper(String name, String data, int price) {
        itemname = name;
        bytedata = data;
        itemprice = price;
    }

    public int getItemPrice()  {
        if (object == null) {
            try {
                object = getItemData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return object.get("price").getAsInt();
    }

    public int checkProfitPercentage() throws IOException {
        if (object == null) object = getItemData();
        return object.get("price").getAsInt() / itemprice * 100;
    }

    public void sellItem() {
        Lilase.auctionHouse.incrementAuctionsSniped();
        Utils.sendMessage("Flipper is running, stopping, will resume when flipper is done");
        if (Lilase.auctionHouse.getOpen()) Lilase.auctionHouse.toggleAuction();
        UngrabUtils.ungrabMouse();
        Utils.sendServerMessage("/hub");
        state = FlipperState.WALKING_TO_FIRST_POINT;
    }

    public void switchStates() {
        switch (state) {
            case WALKING_TO_FIRST_POINT:
                if (Lilase.mc.currentScreen != null) {
                    Lilase.mc.thePlayer.closeScreen();
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
                if (Lilase.mc.currentScreen != null) {
                    Lilase.mc.thePlayer.closeScreen();
                } else if (AngleUtils.smallestAngleDifference(AngleUtils.get360RotationYaw(), 88f) > 1.2) {
                    Utils.debugLog("[Flipper] Rotating to Auction Master");
                    rotation.easeTo(88f, Lilase.mc.thePlayer.rotationPitch, 500);
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
                if (Lilase.mc.currentScreen == null && buyWait.passed()) {
                    final Entity auctionMaster = getAuctionMaster();
                    if (auctionMaster == null) {
                        Utils.debugLog("[Flipper] Cannot find shop NPC, retrying");
                        buyWait.schedule(500);
                    } else {
                        Lilase.mc.playerController.interactWithEntitySendPacket(Lilase.mc.thePlayer, auctionMaster);
                        buyWait.schedule(1500);
                    }
                } else if (InventoryUtils.inventoryNameContains("Auction House") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(15);
                    buyWait.schedule(1500);
                } else if (InventoryUtils.inventoryNameContains("Create BIN Auction")) {
                    if (InventoryUtils.isStoneButton() && buyWait.passed()) {
                        if (InventoryUtils.getSlotForItem(itemname) == -1) {
                            Utils.debugLog("[Flipper] Cannot find item in inventory, stopping flipper");
                            state = FlipperState.NONE;
                            Lilase.auctionHouse.setOpen(true);
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
                    Lilase.auctionHouse.incrementAuctionsPosted();
                    SniperFlipperEvents.postedNames.add(itemname);
                    buyWait.schedule(500);
                    Lilase.mc.thePlayer.closeScreen();
                    buyWait.schedule(500);
                    Utils.sendMessage("Posted item on Auction House, continue sniping now");
                    state = FlipperState.NONE;
                    Lilase.auctionHouse.toggleAuction();
                }
            case NONE:
                break;
        }

    }

    public JsonObject getItemData() throws IOException {
        URL url = new URL("https://www.night0721.me/api/skyblock");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "text/plain");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(bytedata);
        out.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        object = (JsonObject) new JsonParser().parse(content.toString());
        return (JsonObject) new JsonParser().parse(content.toString());
    }

    private float distanceToFirstPoint() {
        return (float) Math.sqrt(Math.pow(Lilase.mc.thePlayer.posX - (-2.5), 2) + Math.pow(Lilase.mc.thePlayer.posZ - (-91.5), 2));
    }

    private float distanceToAuctionMaster() {
        return (float) Math.sqrt(Math.pow(Lilase.mc.thePlayer.posX - (-45), 2) + Math.pow(Lilase.mc.thePlayer.posZ - (-90), 2));
    }

    private Entity getAuctionMaster() {
        for (final Entity e : Lilase.mc.theWorld.loadedEntityList) {
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

