package me.night0721.lilase.features.flipper;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.events.SniperFlipperEvents;
import me.night0721.lilase.player.EffectState;
import me.night0721.lilase.player.Rotation;
import me.night0721.lilase.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static me.night0721.lilase.config.AHConfig.SEND_MESSAGE;

// TODO: Fix repeating code (I will do it soon)
public class Flipper {
    private static String itemname = "";
    private static int itemprice = 0;
    private static int target = 0;
    public static FlipperState state = FlipperState.NONE;
    public static final Rotation rotation = new Rotation();
    private final Clock buyWait = new Clock();
    public static final DiscordWebhook webhook = new DiscordWebhook(Lilase.configHandler.getString("Webhook"));
    public static final NumberFormat format = NumberFormat.getInstance(Locale.US);
    public static final DecimalFormat df = new DecimalFormat("#.##");
    public static final String icon = "https://camo.githubusercontent.com/57a8295f890970d2173b895c7a0f6c60527fb3bec4489b233b221ab45cb9fa42/68747470733a2f2f63646e2e646973636f72646170702e636f6d2f6174746163686d656e74732f3834323031343930393236343935333335342f313038323337333237353033383030333231302f6c696c6173652e706e67";


    public Flipper(String name, int price, int targetprice) {
        itemname = name;
        itemprice = price;
        target = targetprice;
        webhook.setUsername("Lilase");
        webhook.setAvatarUrl(icon);
    }


    public void sellItem() {
        Utils.checkFooter();
        Lilase.sniper.incrementAuctionsSniped();
        Utils.sendMessage("Flipper is running, stopping, will resume when flipper is done");
        if (Lilase.cofl.getOpen()) Lilase.cofl.toggleAuction();
        UngrabUtils.ungrabMouse();
        Utils.debugLog("Cookie: " + (Utils.cookie == EffectState.ON ? "ON" : "OFF"));
        Utils.debugLog("Have screen: " + (Lilase.mc.currentScreen != null ? "Yes" : "No"));
        Utils.debugLog("Profit Percentage: " + target / itemprice);
        try {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("Just purchased an item!")
                    .setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                    .addField("Item:", itemname, true)
                    .addField("Price:", format.format(itemprice), true)
                    .addField("Target Price:", format.format(target), true)
                    .addField("Profit Percentage:", df.format(target / itemprice * 100L) + "%", true)
                    .setColor(Color.decode("#003153")));
            if (SEND_MESSAGE) webhook.execute();
            Utils.debugLog("Notified Webhook");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.debugLog("Failed to send webhook");
        }
        if (Utils.cookie != EffectState.ON) {
            Utils.sendServerMessage("/hub");
            state = FlipperState.WALKING_TO_FIRST_POINT;
        } else {
            Utils.sendServerMessage("/ah");
            state = FlipperState.BUYING;
        }
    }

    public void switchStates() {
        switch (state) {
            case WALKING_TO_FIRST_POINT:
                if (Lilase.mc.currentScreen != null) {
                    Lilase.mc.thePlayer.closeScreen();
                } else if (distanceToFirstPoint() < 0.7f) {
                    System.out.println("Moving to auction house");
                    KeyBindingManager.updateKeys(false, false, false, false, false);
                    state = FlipperState.WALKING_INTO_AUCTION_HOUSE;
                } else if (distanceToFirstPoint() < 5f) {
                    System.out.println("Crouching to point 1");
                    KeyBindingManager.updateKeys(true, false, false, false, false, true, false);
                } else {
                    KeyBindingManager.updateKeys(true, false, false, false, false);
                }
                break;
            case WALKING_INTO_AUCTION_HOUSE:
                if (Lilase.mc.currentScreen != null) {
                    Lilase.mc.thePlayer.closeScreen();
                } else if (AngleUtils.smallestAngleDifference(AngleUtils.get360RotationYaw(), 88f) > 1.2) {
                    System.out.println("Rotating to Auction Master");
                    rotation.easeTo(88f, Lilase.mc.thePlayer.rotationPitch, 500);
                } else if (distanceToAuctionMaster() < 0.7f) {
                    Utils.debugLog("At Auction Master, opening shop");
                    KeyBindingManager.updateKeys(false, false, false, false, false);
                    state = FlipperState.BUYING;
                } else if (distanceToAuctionMaster() < 5f) {
                    System.out.println("Crouching to Auction Master");
                    KeyBindingManager.updateKeys(true, false, false, false, false, true, false);
                } else {
                    KeyBindingManager.updateKeys(true, false, false, false, false);
                }
                break;
            case BUYING:
                if (Utils.cookie != EffectState.ON && Lilase.mc.currentScreen == null && buyWait.passed()) {
                    final Entity auctionMaster = getAuctionMaster();
                    boolean auctionMasterExists = auctionMaster != null;
                    if (auctionMasterExists) {
                        Lilase.mc.playerController.interactWithEntitySendPacket(Lilase.mc.thePlayer, auctionMaster);
                    } else {
                        Utils.debugLog("Cannot find shop NPC, retrying");
                    }
                    buyWait.schedule(auctionMasterExists ? 1500 : 500);
                } else if (InventoryUtils.inventoryNameContains("Auction House") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(15);
                    buyWait.schedule(1500);
                } else if (InventoryUtils.inventoryNameContains("Create BIN Auction")) {
                    if (InventoryUtils.isStoneButton() && buyWait.passed()) {
                        if (InventoryUtils.getSlotForItem(itemname) == -1) {
                            Utils.debugLog("Cannot find item in inventory, stopping flipper");
                            try {
                                webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                        .setTitle("Failed to post an item!")
                                        .setDescription("Could not find item in inventory, sending so you can post it manually")
                                        .setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                                        .addField("Item:", itemname, true)
                                        .addField("Price:", format.format(itemprice), true)
                                        .addField("Target Price:", format.format(target), true)
                                        .addField("Profit Percentage:", Float.parseFloat(df.format(target / itemprice * 100L)) + "%", true)
                                        .setColor(Color.decode("#ff0000")));
                                if (SEND_MESSAGE) webhook.execute();
                                Utils.debugLog("Notified Webhook");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.debugLog("Failed to send webhook");
                            }
                            Lilase.mc.thePlayer.closeScreen();
                            state = FlipperState.NONE;
                            Lilase.cofl.setOpen(true);
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
                    } // TODO: Ternary Expression
                } else if (InventoryUtils.inventoryNameContains("Manage Auction") && buyWait.passed()) {
                    ItemStack slot24 = InventoryUtils.getStackInOpenContainerSlot(24);
                    ItemStack slot33 = InventoryUtils.getStackInOpenContainerSlot(33);
                    ItemStack slot42 = InventoryUtils.getStackInOpenContainerSlot(42);
                    ItemStack slot51 = InventoryUtils.getStackInOpenContainerSlot(51);

                    if (slot24 != null && slot24.getItem() == Items.golden_horse_armor) {
                        InventoryUtils.clickOpenContainerSlot(24);
                        buyWait.schedule(1000);
                    } else if (slot33 != null && slot33.getItem() == Items.golden_horse_armor) {
                        InventoryUtils.clickOpenContainerSlot(33);
                        buyWait.schedule(1000);
                    } else if (slot42 != null && slot42.getItem() == Items.golden_horse_armor) {
                        InventoryUtils.clickOpenContainerSlot(42);
                        buyWait.schedule(1000);
                    } else if (slot51 != null && slot51.getItem() == Items.golden_horse_armor) {
                        InventoryUtils.clickOpenContainerSlot(51);
                        buyWait.schedule(1000);
                    } else {
                        Utils.debugLog("Can't find create auction button, stopping flipper");
                        try {
                            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                    .setTitle("Failed to post an item!")
                                    .setDescription("Could not find create auction button, sending so you can post it manually")
                                    .setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                                    .addField("Item:", itemname, true)
                                    .addField("Price:", format.format(itemprice), true)
                                    .addField("Target Price:", format.format(target), true)
                                    .addField("Profit Percentage:", Float.parseFloat(df.format(target / itemprice * 100L)) + "%", true)
                                    .setColor(Color.decode("#ff0000")));
                            if (SEND_MESSAGE) webhook.execute();
                            Utils.debugLog("Notified Webhook");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.debugLog("Failed to send webhook");
                        }
                        Lilase.mc.thePlayer.closeScreen();
                        state = FlipperState.NONE;
                        Lilase.cofl.setOpen(true);
                        return;
                    }
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
                    Lilase.sniper.incrementAuctionsPosted();
                    SniperFlipperEvents.postedNames.add(itemname);
                    buyWait.schedule(500);
                    Lilase.mc.thePlayer.closeScreen();
                    buyWait.schedule(500);
                    Utils.sendMessage("Posted item on Auction House, continue sniping now");
                    state = FlipperState.NONE;
                    Lilase.cofl.toggleAuction();
                }
            case NONE:
                break;
        }
    }
    public static void sendInterrupt() {
        try {
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("Failed to post an item!")
                    .setDescription("Could not find create as interruption, sending so you can post it manually")
                    .setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                    .addField("Item:", itemname, true)
                    .addField("Price:", format.format(itemprice), true)
                    .addField("Target Price:", format.format(target), true)
                    .addField("Profit Percentage:", Float.parseFloat(df.format(target / itemprice * 100L)) + "%", true)
                    .setColor(Color.decode("#ff0000")));
            if (SEND_MESSAGE) webhook.execute();
            Utils.debugLog("Notified Webhook");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.debugLog("Failed to send webhook");
        }
    }
    public static float distanceToFirstPoint() {
        return (float) Math.sqrt(Math.pow(Lilase.mc.thePlayer.posX - (-2.5), 2) + Math.pow(Lilase.mc.thePlayer.posZ - (-91.5), 2));
    }

    public static float distanceToAuctionMaster() {
        return (float) Math.sqrt(Math.pow(Lilase.mc.thePlayer.posX - (-45), 2) + Math.pow(Lilase.mc.thePlayer.posZ - (-90), 2));
    }

    public static Entity getAuctionMaster() {
        return Lilase.mc.theWorld.loadedEntityList.stream().filter(e -> e instanceof EntityArmorStand && StringUtils.stripControlCodes(e.getDisplayName().getUnformattedText()).startsWith("Auction Master")).findFirst().orElse(null);
    }
}

