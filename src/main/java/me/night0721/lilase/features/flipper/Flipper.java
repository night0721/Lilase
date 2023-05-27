package me.night0721.lilase.features.flipper;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.relister.RelisterState;
import me.night0721.lilase.player.EffectState;
import me.night0721.lilase.player.Rotation;
import me.night0721.lilase.utils.*;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static me.night0721.lilase.config.AHConfig.*;
import static me.night0721.lilase.events.SniperFlipperEvents.ah_full;
import static me.night0721.lilase.events.SniperFlipperEvents.selling_queue;
import static me.night0721.lilase.utils.PlayerUtils.sendPacketWithoutEvent;

// TODO: Fix repeating code (I will do it soon)
public class Flipper {
    public final String name;
    public final int price;
    public final int target;
    public final String uuid;
    public static FlipperState state = FlipperState.NONE;
    public static final Rotation rotation = new Rotation();
    private final Clock buyWait = new Clock();
    public static final DiscordWebhook webhook = new DiscordWebhook(Lilase.configHandler.getString("Webhook"));
    public static final NumberFormat format = NumberFormat.getInstance(Locale.US);
    public static final DecimalFormat df = new DecimalFormat("#.##");
    public static final String icon = "https://camo.githubusercontent.com/57a8295f890970d2173b895c7a0f6c60527fb3bec4489b233b221ab45cb9fa42/68747470733a2f2f63646e2e646973636f72646170702e636f6d2f6174746163686d656e74732f3834323031343930393236343935333335342f313038323337333237353033383030333231302f6c696c6173652e706e67";

    public Flipper(String name, int price, int target, String uuid) {
        this.name = name;
        this.price = price;
        this.target = target;
        this.uuid = uuid;
        webhook.setUsername("Lilase");
        webhook.setAvatarUrl(icon);
    }


    public void sellItem() {
        Utils.checkFooter();
        if (Lilase.relister.state != RelisterState.NONE) {
            Utils.debugLog("Relister is running, stopping, will resume when flipper is done");
            Lilase.relister.toggle();
        }
        Utils.sendMessage("Flipper is running, stopping, will resume when flipper is done");
        if (Lilase.cofl.isOpen()) {
            Lilase.cofl.toggleAuction();
        }
        UngrabUtils.ungrabMouse();
        Utils.debugLog("Cookie: " + (Utils.cookie == EffectState.ON ? "ON" : "OFF"));
        System.out.println("Slot in inventory: " + InventoryUtils.getSlotForItem(this.uuid));
        try {
            Thread.sleep(RELIST_TIMEOUT);
        } catch (InterruptedException ignored) {
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
//                    System.out.println("Moving to auction house");
                    KeyBindingManager.updateKeys(false, false, false, false, false);
                    state = FlipperState.WALKING_INTO_AUCTION_HOUSE;
                } else if (distanceToFirstPoint() < 5f) {
//                    System.out.println("Crouching to point 1");
                    KeyBindingManager.updateKeys(true, false, false, false, false, true, false);
                } else {
                    KeyBindingManager.updateKeys(true, false, false, false, false);
                }
                break;
            case WALKING_INTO_AUCTION_HOUSE:
                if (Lilase.mc.currentScreen != null) {
                    Lilase.mc.thePlayer.closeScreen();
                } else if (AngleUtils.smallestAngleDifference(AngleUtils.get360RotationYaw(), 88f) > 1.2) {
//                    System.out.println("Rotating to Auction Master");
                    rotation.easeTo(88f, Lilase.mc.thePlayer.rotationPitch, 500);
                } else if (distanceToAuctionMaster() < 0.7f) {
                    Utils.debugLog("At Auction Master, opening shop");
                    KeyBindingManager.updateKeys(false, false, false, false, false);
                    state = FlipperState.BUYING;
                } else if (distanceToAuctionMaster() < 5f) {
//                    System.out.println("Crouching to Auction Master");
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
                        if (InventoryUtils.getSlotForItem(this.uuid) == -1) {
                            Utils.debugLog("Cannot find item in inventory, stopping flipper");
                            selling_queue.remove(0);
                            if (SEND_MESSAGE) {
                                try {
                                    webhook.addEmbed(embed("Failed to post an item!", "Could not find item in inventory, sending so you can post it manually", "#ff0000"));
                                    webhook.execute();
                                    Utils.debugLog("Notified Webhook");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.debugLog("Failed to send webhook");
                                }
                            }
                            Lilase.mc.thePlayer.closeScreen();
                            state = FlipperState.NONE;
                            Lilase.cofl.toggleAuction();
                            return;
                        }
                        InventoryUtils.clickOpenContainerSlot(InventoryUtils.getSlotForItem(this.uuid) + 81);
                        buyWait.schedule(1000);
                    } else if (!InventoryUtils.isStoneButton() && InventoryUtils.isToAuctionItem(this.uuid) && buyWait.passed()) {
                        InventoryUtils.clickOpenContainerSlot(31);
                        state = FlipperState.PRICE;
                        buyWait.schedule(1500);
                    } else if (!InventoryUtils.isStoneButton() && !InventoryUtils.isToAuctionItem(this.uuid) && buyWait.passed()) {
                        InventoryUtils.clickOpenContainerSlot(13);
                        buyWait.schedule(1000);
                    } // TODO: Ternary Expression
                }
                if (InventoryUtils.inventoryNameContains("Manage Auctions") && buyWait.passed()) {
                    ItemStack slot24 = InventoryUtils.getStackInOpenContainerSlot(24);
                    ItemStack slot33 = InventoryUtils.getStackInOpenContainerSlot(33);

                    if (slot24 != null && slot24.getItem() == Items.golden_horse_armor) {
                        InventoryUtils.clickOpenContainerSlot(24);
                        buyWait.schedule(1000);
                    } else if (slot33 != null) {
                        if (slot33.getSubCompound("display", false).getString("Name").startsWith("§c")) {
                            ah_full = true;
                            Utils.debugLog("Auction slots full, stopping sniper for a while");
                            selling_queue.remove(0);
                            if (SEND_MESSAGE) {
                                try {
                                    webhook.addEmbed(embed("Auction slots are full!", "Could not create more auctions as slots are full already, sending you here so you could create it manually", "#ff0000"));
                                    webhook.execute();
                                    Utils.debugLog("Notified Webhook");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utils.debugLog("Failed to send webhook");
                                }
                            }
                            Lilase.mc.thePlayer.closeScreen();
                            state = FlipperState.NONE;
//                            Lilase.cofl.toggleAuction();
                            return;
                        } else if (slot33.getItem() == Items.golden_horse_armor) {
                            InventoryUtils.clickOpenContainerSlot(33);
                            buyWait.schedule(1000);
                        }
                    } else {
                        Utils.debugLog("Can't find create auction button, stopping flipper");
                        selling_queue.remove(0);
                        if (SEND_MESSAGE) {
                            try {
                                webhook.addEmbed(embed("Failed to post an item!", "Could not find create auction button, sending so you can post it manually", "#ff0000"));
                                webhook.execute();
                                Utils.debugLog("Notified Webhook");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.debugLog("Failed to send webhook");
                            }
                        }
                        Lilase.mc.thePlayer.closeScreen();
                        state = FlipperState.NONE;
                        Lilase.cofl.toggleAuction();
                        return;
                    }
                }
            case PRICE:
                if (Lilase.mc.currentScreen instanceof GuiEditSign && buyWait.passed()) {
                    GuiEditSign guiEditSign = (GuiEditSign) Lilase.mc.currentScreen;
                    TileEntitySign tileSign;
                    try {
                        tileSign = (TileEntitySign) ReflectionUtils.field(guiEditSign, "tileSign");
                    } catch (Exception e) {
                        tileSign = (TileEntitySign) ReflectionUtils.field(guiEditSign, "field_146848_f");
                    }
                    if (tileSign == null) {
                        Utils.debugLog("TileEntitySign is null, stopping flipper");
                        selling_queue.remove(0);
                        if (SEND_MESSAGE) {
                            try {
                                webhook.addEmbed(embed("Failed to post an item!", "Could not find TileEntitySign, sending so you can post it manually", "#ff0000"));
                                webhook.execute();
                                Utils.debugLog("Notified Webhook");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utils.debugLog("Failed to send webhook");
                            }
                        }
                        Lilase.mc.thePlayer.closeScreen();
                        state = FlipperState.NONE;
                        Lilase.cofl.toggleAuction();
                        return;
                    }
                    String price = SHORTEN_NUMBERS ? Utils.convertToShort(this.target) : String.valueOf(this.target);
                    Utils.debugLog("Long Price: " + this.target);
                    Utils.debugLog("Shorten Price: " + Utils.convertToShort(this.target));
                    tileSign.signText[0] = new ChatComponentText(price);
                    sendPacketWithoutEvent(new C12PacketUpdateSign(tileSign.getPos(), tileSign.signText));
                    state = FlipperState.TIME;
                    buyWait.schedule(1000);
                }
            case TIME:
                if (!InventoryUtils.isStoneButton() && InventoryUtils.isToAuctionItem(this.uuid) && InventoryUtils.inventoryNameStartsWith("Create BIN Auction") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(33);
                    buyWait.schedule(1000);
                }
                if (InventoryUtils.inventoryNameContains("Auction Duration") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(AUCTION_LENGTH == 0 ? 10 : AUCTION_LENGTH == 1 ? 11 : AUCTION_LENGTH == 2 ? 12 : AUCTION_LENGTH == 3 ? 13 : AUCTION_LENGTH == 4 ? 14 : 12);
                    state = FlipperState.START;
                    buyWait.schedule(1000);
                }
            case START:
                if (!InventoryUtils.isStoneButton() && InventoryUtils.isToAuctionItem(this.uuid) && InventoryUtils.inventoryNameStartsWith("Create BIN Auction") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(29);
                    buyWait.schedule(1000);
                } else if (InventoryUtils.inventoryNameContains("Confirm BIN Auction") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(11);
                    buyWait.schedule(1000);
                } else if (InventoryUtils.inventoryNameContains("BIN Auction View") && buyWait.passed()) {
                    InventoryUtils.clickOpenContainerSlot(49);
                    buyWait.schedule(500);
                    Lilase.mc.thePlayer.closeScreen();
                    buyWait.schedule(500);
                    state = FlipperState.NONE;
                    if (ah_full) {
                        Utils.sendMessage("Posted item on Auction House, claiming item now as slots aren't full anymore");
                        if (!Lilase.claimer.isOpen()) Lilase.claimer.toggle();
                    } else if (Lilase.relister.toRelist.size() > 1) {
                        Utils.debugLog("Still have items to relist, continuing to relist");
                        Lilase.relister.toggle();
                    } else if (Lilase.relister.toRelist.size() == 1) {
                        Utils.debugLog("Relisted all items, stopping relister and continuing to snipe");
                        Lilase.relister.toggle();
                        Lilase.cofl.toggleAuction();
                    } else if (Lilase.relister.shouldBeRelisting) {
                        Lilase.relister.toggle();
                    } else {
                        Utils.sendMessage("Posted item on Auction House, continue sniping now");
                        Lilase.cofl.toggleAuction();
                    }
                    if (selling_queue.get(0) != null) selling_queue.remove(0);
                }
            case NONE:
                break;
        }
    }

    public void sendInterrupt() {
        if (SEND_MESSAGE) {
            try {
                webhook.addEmbed(embed("Could not create as interruption", "Could not create as interruption, sending so you can post it manually", "#ff0000"));
                webhook.execute();
                Utils.debugLog("Notified Webhook");
            } catch (Exception e) {
                e.printStackTrace();
                Utils.debugLog("Failed to send webhook");
            }
        }
    }

    public void sendNotEnoughCoins() {
        if (SEND_MESSAGE) {
            try {
                webhook.addEmbed(embed("Could not create as not enough money", "Could not find create as your purse don't have enough money, sending so you can post it manually", "#ff0000"));
                webhook.execute();
                Utils.debugLog("Notified Webhook");
            } catch (Exception e) {
                e.printStackTrace();
                Utils.debugLog("Failed to send webhook");
            }
        }
    }

    public void sendBought() {
        if (SEND_MESSAGE) {
            try {
                webhook.addEmbed(embed("Just purchased an item!", "", "#003153"));
                webhook.execute();
                Utils.debugLog("Notified Webhook");
            } catch (Exception e) {
                e.printStackTrace();
                Utils.debugLog("Failed to send webhook");
            }
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

    public DiscordWebhook.EmbedObject embed(String title, String description, String color) {
        return new DiscordWebhook.EmbedObject()
                .setTitle(title)
                .setDescription(description)
                .setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                .addField("Item:", name, true)
                .addField("Price:", format.format(price), true)
                .addField("Target Price:", format.format(target), true)
                .addField("Profit Percentage:", df.format((double) (target - price) / price * 100f) + "%", true)
                .setColor(Color.decode(color));
    }
}

