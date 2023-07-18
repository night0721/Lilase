package me.night0721.lilase.features.relister;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.claimer.ClaimerState;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.features.flipper.FlipperState;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.night0721.lilase.config.AHConfig.SEND_MESSAGE;
import static me.night0721.lilase.events.SniperFlipperEvents.selling_queue;
import static me.night0721.lilase.features.flipper.Flipper.*;
import static me.night0721.lilase.utils.InventoryUtils.clickWindow;
import static me.night0721.lilase.utils.KeyBindingManager.stopMovement;

public class Relister extends Sniper {
    public RelisterState state = RelisterState.NONE;
    public boolean shouldBeRelisting = false;
    public final List<Integer> toRelist = new ArrayList<>();
    private final Pattern BUYITNOW = Pattern.compile("Buy it now: ([\\d,]+) coins");

    @Override
    public void onTick() {
        switch (state) {
            case WALKING_TO_FIRST_POINT:
                if (Lilase.mc.currentScreen != null) {
                    Lilase.mc.thePlayer.closeScreen();
                } else if (distanceToFirstPoint() < 0.7f) {
                    System.out.println("Moving to auction house");
                    KeyBindingManager.updateKeys(false, false, false, false, false);
                    state = RelisterState.WALKING_INTO_AUCTION_HOUSE;
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
                    state = RelisterState.OPENING;
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
                }
                if (InventoryUtils.inventoryNameContains("Auction House") && cooldown.passed()) {
                    InventoryUtils.clickOpenContainerSlot(15);
                    state = RelisterState.START;
                    cooldown.schedule(500);
                }
            case START:
                if (InventoryUtils.inventoryNameContains("Manage Auctions") && cooldown.passed()) {
                    long relisting = Lilase.mc.thePlayer.openContainer.inventorySlots
                            .stream()
                            .filter(slot -> slot.getStack() != null)
                            .filter(slot -> InventoryUtils.getLore(slot.getStack()) != null)
                            .filter(slot -> ScoreboardUtils.cleanSB(Objects.requireNonNull(InventoryUtils.getLore(slot.getStack())).toString()).contains("Status: Expired")).count();
                    System.out.println(relisting + " items to relist");
                    if (relisting == 0) {
                        Utils.debugLog("No items to relist");
                        Utils.debugLog("Relisted all expired items");
                        toRelist.clear();
                        if (isOpen()) toggle();
                        Utils.debugLog("Continue sniping after relisting");
                        Lilase.cofl.toggleAuction();
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
                            toRelist.add(i);
                            try {
                                String nbtString = ScoreboardUtils.cleanSB(list.toString());
                                System.out.println("Item NBT: " + nbtString);
                                Matcher matcher = BUYITNOW.matcher(nbtString);
                                if (nbtString.contains("Status: Expired") && matcher.find()) {
                                    String name = ScoreboardUtils.cleanSB(is.getDisplayName());
                                    int price = (int) Long.parseLong(matcher.group(1).replace(",", ""));
                                    int target = Math.round(price * 0.98f);
                                    String uuid = is.getTagCompound().getCompoundTag("ExtraAttributes").getString("uuid");
                                    clickWindow(Lilase.mc.thePlayer.openContainer.windowId, i);
                                    Thread.sleep(300);
                                    clickWindow(Lilase.mc.thePlayer.openContainer.windowId, 31);
                                    Thread.sleep(300);
                                    clickWindow(Lilase.mc.thePlayer.openContainer.windowId, 11);
                                    Thread.sleep(300);
                                    if (SEND_MESSAGE) {
                                        try {
                                            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                                                    .setTitle("Relisting an item!")
                                                    .setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                                                    .addField("Item:", ScoreboardUtils.cleanSB(is.getDisplayName()), true)
                                                    .addField("Target Price:", format.format(target), true)
                                                    .setColor(Color.decode("#003153"))
                                            );
                                            webhook.execute();
                                            Utils.debugLog("Notified Webhook");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Utils.debugLog("Failed to send webhook");
                                        }
                                    }
                                    Flipper flipper = new Flipper(name, price, target, uuid);
                                    System.out.println("Item Name: " + flipper.name);
                                    System.out.println("Item Price: " + flipper.price);
                                    System.out.println("Target Price: " + flipper.target);
                                    selling_queue.add(flipper);
                                    this.toggle();
                                    flipper.sellItem();
                                    break;
                                }
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                    Utils.debugLog("Relisted " + toRelist.size() + " sold items");
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
            Utils.sendMessage("Stopped Auto Relister");
            Lilase.mc.thePlayer.closeScreen();
            state = RelisterState.NONE;
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
            if (Lilase.claimer.state != ClaimerState.NONE) {
                Utils.sendMessage("Claimer is running, stopping");
                setOpen(false);
                return;
            }
            if (Utils.cookie == EffectState.ON || Utils.checkInHub()) {
                Utils.sendMessage("Started Auto Relister");
                setOpen(true);
                if (Utils.cookie != EffectState.ON) {
                    Utils.sendServerMessage("/hub");
                    state = RelisterState.WALKING_TO_FIRST_POINT;
                } else {
                    Utils.sendServerMessage("/ah");
                    state = RelisterState.OPENING;
                }
                UngrabUtils.ungrabMouse();
            } else {
                Utils.sendMessage("Detected not in hub, please go to hub to start");
            }
        }
    }
}
