package me.night0721.lilase.features.claimer;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.features.flipper.FlipperState;
import me.night0721.lilase.features.relister.RelisterState;
import me.night0721.lilase.features.sniper.Sniper;
import me.night0721.lilase.player.EffectState;
import me.night0721.lilase.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.night0721.lilase.config.AHConfig.SEND_MESSAGE;
import static me.night0721.lilase.events.SniperFlipperEvents.ah_full;
import static me.night0721.lilase.events.SniperFlipperEvents.selling_queue;
import static me.night0721.lilase.utils.InventoryUtils.clickWindow;
import static me.night0721.lilase.features.flipper.Flipper.*;
import static me.night0721.lilase.utils.KeyBindingManager.stopMovement;

public class Claimer extends Sniper {
    public ClaimerState state = ClaimerState.NONE;
    public List<Integer> toClaim = new ArrayList<>();

    @Override
    public void onTick() {
        switch (state) {
            case WALKING_TO_FIRST_POINT:
                if (Lilase.mc.currentScreen != null) {
                    Lilase.mc.thePlayer.closeScreen();
                } else if (distanceToFirstPoint() < 0.7f) {
                    System.out.println("Moving to auction house");
                    KeyBindingManager.updateKeys(false, false, false, false, false);
                    state = ClaimerState.WALKING_INTO_AUCTION_HOUSE;
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
                    state = ClaimerState.OPENING;
                } else if (distanceToAuctionMaster() < 5f) {
                    System.out.println("Crouching to Auction Master");
                    KeyBindingManager.updateKeys(true, false, false, false, false, true, false);
                } else {
                    KeyBindingManager.updateKeys(true, false, false, false, false);
                }
                break;
            case OPENING:
                if (Utils.cookie != EffectState.ON && Lilase.mc.currentScreen == null && cooldown.passed()) {
                    final Entity auctionMaster = getAuctionMaster();
                    if (auctionMaster == null) {
                        Utils.debugLog("Cannot find shop NPC, retrying");
                        cooldown.schedule(500);
                    } else {
                        Lilase.mc.playerController.interactWithEntitySendPacket(Lilase.mc.thePlayer, auctionMaster);
                        cooldown.schedule(1500);
                    }
                } else if (Utils.cookie == EffectState.ON) {
                    if (Lilase.mc.currentScreen != null) Lilase.mc.thePlayer.closeScreen();
                    else Utils.sendServerMessage("/ah");
                } else if (InventoryUtils.inventoryNameContains("Auction House") && cooldown.passed()) {
                        InventoryUtils.clickOpenContainerSlot(15);
                        state = ClaimerState.START;
                        cooldown.schedule(500);
                }
            case START:
                if (InventoryUtils.inventoryNameContains("Manage Auctions") && cooldown.passed()) {
                    long claiming = Lilase.mc.thePlayer.openContainer.inventorySlots
                            .stream()
                            .filter(slot -> slot.getStack() != null)
                            .filter(slot -> InventoryUtils.getLore(slot.getStack()) != null)
                            .filter(slot -> ScoreboardUtils.cleanSB(Objects.requireNonNull(InventoryUtils.getLore(slot.getStack())).toString()).contains("Status: Sold")).count();
                    System.out.println(claiming + " items to claim");
                    if (claiming == 0) {
                        Utils.debugLog("No items to claim");
                        Utils.debugLog("Claimed all sold items");
                        toClaim.clear();
                        if (isOpen()) toggle();
                        if (ah_full) {
                            ah_full = false;
                            if (selling_queue.size() > 0) {
                                Utils.debugLog("Listing next item from queue");
                                selling_queue.get(0).sellItem();
                            } else {
                                Utils.debugLog("Continue sniping after claiming");
                                Lilase.cofl.toggleAuction();
                            }
                        }
                        return;
                    }
                    for (int i = 10; i <= 25; i++) {
                        if (i == 17 || i == 18) continue;
                        ItemStack is = Lilase.mc.thePlayer.openContainer.getSlot(i).getStack();
                        if (is == null) continue;
                        Item item = is.getItem();
                        if (item == Items.golden_horse_armor || item == Items.arrow || item == Item.getItemFromBlock(Blocks.hopper) || item == Item.getItemFromBlock(Blocks.stained_glass_pane) || item == Item.getItemFromBlock(Blocks.cauldron))
                            continue;
                        NBTTagList list = InventoryUtils.getLore(is);
                        if (list != null) {
                            System.out.println("Item NBT: " + ScoreboardUtils.cleanSB(list.toString()));
                            if (ScoreboardUtils.cleanSB(list.toString()).contains("Status: Sold")) {
                                toClaim.add(i);
                                try {
                                    Thread.sleep(300);
                                    clickWindow(Lilase.mc.thePlayer.openContainer.windowId, i);
                                    Thread.sleep(300);
                                    clickWindow(Lilase.mc.thePlayer.openContainer.windowId + 1, 31);
                                    if (SEND_MESSAGE) {
                                        try {
                                            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                                    .setTitle("Just claimed an item!")
                                                    .setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                                                    .addField("Item:", ScoreboardUtils.cleanSB(is.getDisplayName()), true)
                                                    .setColor(Color.decode("#003153"))
                                            );
                                            webhook.execute();
                                            Utils.debugLog("Notified Webhook");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Utils.debugLog("Failed to send webhook");
                                        }
                                    }
                                    cooldown.schedule(500);
                                    break;
                                } catch (InterruptedException ignore) {
                                }
                            }
                        }
                    }
                    Utils.debugLog("Claimed " + toClaim.size() + " sold items");
                    state = ClaimerState.OPENING;
                } else if (InventoryUtils.inventoryNameContains("Create BIN Auction") && cooldown.passed()) {
                    Utils.debugLog("You don't have any items in the auction house, stopping");
                    toggle();
                }
            case NONE:
                break;
        }
    }

    @Override
    public void toggle() {
        if (isOpen()) {
            Utils.sendMessage("Stopped Auto Claimer");
            Lilase.mc.thePlayer.closeScreen();
            state = ClaimerState.NONE;
            stopMovement();
            setOpen(false);
            UngrabUtils.regrabMouse();
        } else {
            if (SEND_MESSAGE && Lilase.configHandler.getString("Webhook").equals("")) {
                Utils.sendMessage("Sending message to Webhook is on but Webhook is missing, stopping");
                setOpen(false);
                return;
            }
            if (Flipper.state != FlipperState.NONE) {
                Utils.sendMessage("Flipper is running, stopping");
                setOpen(false);
                return;
            }
            if (Lilase.relister.state != RelisterState.NONE) {
                Utils.sendMessage("Relister is running, stopping");
                setOpen(false);
                return;
            }
            if (Utils.cookie == EffectState.ON || Utils.checkInHub()) {
                Utils.sendMessage("Started Auto Claimer");
                setOpen(true);
                if (Utils.cookie != EffectState.ON) {
                    Utils.sendServerMessage("/hub");
                    state = ClaimerState.WALKING_TO_FIRST_POINT;
                } else {
                    Utils.sendServerMessage("/ah");
                    state = ClaimerState.OPENING;
                }
                UngrabUtils.ungrabMouse();
            } else {
                Utils.sendMessage("Detected not in hub, please go to hub to start");
            }
        }
    }
}
