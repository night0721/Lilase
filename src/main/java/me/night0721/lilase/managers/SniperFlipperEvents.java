package me.night0721.lilase.managers;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.ah.AuctionHouse;
import me.night0721.lilase.features.ah.States;
import me.night0721.lilase.features.flip.Flipper;
import me.night0721.lilase.features.flip.FlipperState;
import me.night0721.lilase.utils.*;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import static me.night0721.lilase.features.flip.Flipper.rotation;

public class SniperFlipperEvents {
    Thread bzchillingthread;

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) throws InterruptedException {
        String message = event.message.getUnformattedText();
        if (!message.contains(":")) {
            if (message.equals("You didn't participate in this auction!")) {
                Utils.debugLog("[Sniper] Failed to buy item, not fast enough. Closing the menu");
                PlayerUtils.mc.playerController.windowClick(PlayerUtils.mc.thePlayer.openContainer.windowId, 49, 0, 0, PlayerUtils.mc.thePlayer); // Close the window as could not buy
            } else if (message.equals("You don't have enough coins to afford this bid!")) {
                Utils.debugLog("[Sniper] Failed to buy item, not enough money. Closing the menu");
                AuctionHouse.clickState = States.NONE;
                PlayerUtils.mc.playerController.windowClick(PlayerUtils.mc.thePlayer.openContainer.windowId, 49, 0, 0, PlayerUtils.mc.thePlayer); // Close the window as could not buy
            } else if (message.contains("Your new API key is")) {
                Utils.debugLog("[Sniper] Detected new API key, saving it to config");
                Utils.debugLog("[Sniper] Saved new API key to config");
                String apiKey = message.replace("Your new API key is ", "");
                ConfigUtils.writeStringConfig("main", "APIKey", apiKey);
            } else if (message.equals("Claiming BIN auction...")) {
                AuctionHouse.clickState = States.EXECUTE;
            } else if (message.equals("This BIN sale is still in its grace period!")) {
                AuctionHouse.clickState = States.CLICK;
            } else if (message.equals("Your starting bid must be at least 10 coins!")) {
                InventoryUtils.clickOpenContainerSlot(13);
                PlayerUtils.mc.thePlayer.closeScreen();
                Utils.debugLog("[Flipper] Cannot post item as the cost is too low, stopping fliiper and starting sniper");
                Lilase.auctionHouse.toggleAuction();
                Flipper.state = FlipperState.NONE;
            } else if (message.contains("Can't create a BIN auction for this item for a PRICE this LOW!")) {
                PlayerUtils.mc.thePlayer.closeScreen();
                Utils.debugLog("[Flipper] Cannot post item as the cost is too low, stopping fliiper and starting sniper");
                Lilase.auctionHouse.toggleAuction();
                Flipper.state = FlipperState.NONE;
            } else if (message.contains("You were spawned in Limbo")) {
                Utils.sendMessage("Detected in Limbo, stopping everything for 5 minutes");
                Flipper.state = FlipperState.NONE;
                AuctionHouse.clickState = States.NONE;
                if (Lilase.auctionHouse.open)
                    Lilase.auctionHouse.toggleAuction();
                Thread.sleep(5000);
                Utils.sendServerMessage("/hub");
                bzchillingthread = new Thread(bazaarChilling);
                bzchillingthread.start();
            }
        }
    }

    Runnable bazaarChilling = () -> {
        try {
            rotation.reset();
            rotation.easeTo(103f, -11f, 1000);
            Thread.sleep(1500);
            KeyBindingManager.updateKeys(true, false, false, false, false, true, false);
            long timeout = System.currentTimeMillis();
            boolean timedOut = false;
            while (BlockUtils.getRelativeBlock(0, 0, 1) != Blocks.spruce_stairs) {
                if ((System.currentTimeMillis() - timeout) > 10000) {
                    Utils.debugLog("[Sniper] Couldn't find bz, gonna chill here");
                    timedOut = true;
                    break;
                }
            }
            KeyBindingManager.stopMovement();

            if (!timedOut) {
                // about 5 minutes
                for (int i = 0; i < 15; i++) {
                    Thread.sleep(6000);
                    KeyBindingManager.rightClick();
                    Thread.sleep(3000);
                    InventoryUtils.clickOpenContainerSlot(11);
                    Thread.sleep(3000);
                    InventoryUtils.clickOpenContainerSlot(11);
                    Thread.sleep(3000);
                    InventoryUtils.clickOpenContainerSlot(10);
                    Thread.sleep(3000);
                    InventoryUtils.clickOpenContainerSlot(10);
                    Thread.sleep(3000);
                    PlayerUtils.mc.thePlayer.closeScreen();
                }
            } else {
                Thread.sleep(1000 * 60 * 5);
            }
            PlayerUtils.mc.thePlayer.sendChatMessage("/hub");
            Thread.sleep(6000);
            Lilase.auctionHouse.toggleAuction();
        } catch (Exception ignore) {
        }

    };

    @SubscribeEvent
    public void onLastRender(RenderWorldLastEvent event) {
        if (rotation.rotating) {
            rotation.update();
        }
    }

    @SubscribeEvent
    public void OnKeyPress(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
            if (Flipper.state != FlipperState.NONE) {
                new Thread(() -> {
                    try {
                        Utils.debugLog("[Flipper] Interrupting Flipper selling");
                        Thread.sleep(500);
                        PlayerUtils.mc.thePlayer.closeScreen();
                        Flipper.state = FlipperState.NONE;
                        Lilase.auctionHouse.toggleAuction();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        }
    }
}
