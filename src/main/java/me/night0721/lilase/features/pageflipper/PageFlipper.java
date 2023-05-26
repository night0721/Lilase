package me.night0721.lilase.features.pageflipper;

import lombok.Getter;
import lombok.Setter;
import me.night0721.lilase.Lilase;
import me.night0721.lilase.player.EffectState;
import me.night0721.lilase.utils.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Objects;

import static me.night0721.lilase.features.flipper.Flipper.*;

public class PageFlipper {
    public PageFlipperState state = PageFlipperState.NONE;
    public final Clock cooldown = new Clock();
    public @Getter @Setter boolean open = false;
    private Thread loop;

    public void start() {
        Utils.checkFooter();
        if (Utils.cookie == EffectState.ON) state = PageFlipperState.OPENING;
        else {
            Utils.sendServerMessage("/hub");
            state = PageFlipperState.WALKING_TO_FIRST_POINT;
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
                    state = PageFlipperState.WALKING_INTO_AUCTION_HOUSE;
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
                    state = PageFlipperState.OPENING;
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
                } else if (InventoryUtils.inventoryNameContains("Auction House") && cooldown.passed()) {
                    InventoryUtils.clickOpenContainerSlot(11);
                    state = PageFlipperState.START;
                }
            case START:
                if (InventoryUtils.inventoryNameContains("Auctions")) {
                    ItemStack is = InventoryUtils.getStackInOpenContainerSlot(49);
                    if (is != null && is.getItem() == Items.arrow) {
                        if (loop == null) {
                            loop = new Thread(() -> {
                                for (int i = 11; i <= 43; i++) {
                                    if (!Objects.equals(Lilase.mc.thePlayer.openContainer.getSlot(i).getStack().getDisplayName(), " ")) {
                                        byte[] b = Lilase.mc.thePlayer.openContainer.getSlot(i).getStack().getTagCompound().getCompoundTag("display").getByteArray("Lore");
//                                        System.out.println(Arrays.toString(b).replaceAll("§[0-9a-z]", ""));
                                    }
                                }
                                loop = null;
                            });
                            loop.start();
                        }
                    } else {
                        Utils.debugLog("Not in auction house GUI, stopping");
//                        toggleAuction();
                    }
                } else {
                    Utils.debugLog("No GUI open, stopping");
                }
            case NONE:
                break;
        }
    }

    public void toggleAuction() {
        if (isOpen()) {
            Utils.sendMessage("Stopped Page Flipper");
            Lilase.mc.thePlayer.closeScreen();
            state = PageFlipperState.NONE;
            setOpen(false);
            UngrabUtils.regrabMouse();
        } else {
            if (Utils.checkInHub()) {
                Utils.sendMessage("Started Page Flipper");
                start();
                setOpen(true);
                UngrabUtils.ungrabMouse();
            } else Utils.sendMessage("Detected not in hub, please go to hub to start");
        }
    }
}
