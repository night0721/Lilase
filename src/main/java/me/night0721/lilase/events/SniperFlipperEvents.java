package me.night0721.lilase.events;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.ah.AHConfig;
import me.night0721.lilase.features.flip.Flipper;
import me.night0721.lilase.features.flip.FlipperState;
import me.night0721.lilase.gui.TextRenderer;
import me.night0721.lilase.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import static me.night0721.lilase.features.ah.AHConfig.GUI_COLOR;
import static me.night0721.lilase.features.ah.AuctionHouse.flipper;
import static me.night0721.lilase.features.flip.Flipper.rotation;
import static me.night0721.lilase.features.flip.FlipperState.START;
import static me.night0721.lilase.utils.PlayerUtils.sendPacketWithoutEvent;

public class SniperFlipperEvents {
    private int windowId = 1;
    private boolean buying = false;

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) throws InterruptedException, IOException {
        String message = event.message.getUnformattedText();
        if (!message.contains(":")) {
            if (message.equals("You didn't participate in this auction!")) {
                Utils.debugLog("[Sniper] Failed to buy item, not fast enough. Closing the menu");
                InventoryUtils.clickOpenContainerSlot(49);
            } else if (message.equals("You don't have enough coins to afford this bid!")) {
                Utils.debugLog("[Sniper] Failed to buy item, not enough money. Closing the menu");
                InventoryUtils.clickOpenContainerSlot(49);
            } else if (message.contains("Your new API key is")) {
                Utils.debugLog("[Sniper] Detected new API key, saving it to config");
                Utils.debugLog("[Sniper] Saved new API key to config");
                String apiKey = message.replace("Your new API key is ", "");
                ConfigUtils.writeStringConfig("main", "APIKey", apiKey);
            } else if (message.equals("Claiming BIN auction...") && buying) {
                Utils.debugLog("[Sniper] Bought an item, starting to sell");
                Lilase.auctionHouse.webhook.execute();
                flipper.sellItem();
            } else if (message.equals("Your starting bid must be at least 10 coins!")) {
                InventoryUtils.clickOpenContainerSlot(13);
                Lilase.mc.thePlayer.closeScreen();
                Utils.debugLog("[Flipper] Cannot post item as the cost is too low, stopping fliiper and starting sniper");
                Lilase.auctionHouse.toggleAuction();
                Flipper.state = FlipperState.NONE;
            } else if (message.contains("Can't create a BIN auction for this item for a PRICE this LOW!")) {
                Lilase.mc.thePlayer.closeScreen();
                Utils.debugLog("[Flipper] Cannot post item as the cost is too low, stopping fliiper and starting sniper");
                Lilase.auctionHouse.toggleAuction();
                Flipper.state = FlipperState.NONE;
            } else if (message.contains("You were spawned in Limbo")) {
                Utils.sendMessage("Detected in Limbo, stopping everything for 5 minutes");
                Flipper.state = FlipperState.NONE;
                if (Lilase.auctionHouse.getOpen()) Lilase.auctionHouse.toggleAuction();
                Thread.sleep(5000);
                Utils.sendServerMessage("/hub");
                Thread bzchillingthread = new Thread(bazaarChilling);
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
                    Lilase.mc.thePlayer.closeScreen();
                }
            } else {
                Thread.sleep(1000 * 60 * 5);
            }
            Lilase.mc.thePlayer.sendChatMessage("/hub");
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
                        Lilase.mc.thePlayer.closeScreen();
                        Flipper.state = FlipperState.NONE;
                        Lilase.auctionHouse.toggleAuction();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            if (AHConfig.GUI) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getDefault());
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                String time = String.format("%02d:%02d", hour, minute);
                String lines = "X: " + Math.round(Lilase.mc.thePlayer.posX) + "\n" +
                        "Y: " + Math.round(Lilase.mc.thePlayer.posY) + "\n" +
                        "Z: " + Math.round(Lilase.mc.thePlayer.posZ) + "\n" +
                        time + "\n" +
                        "FPS: " + Minecraft.getDebugFPS() + "\n" +
                        "Auctions Sniped: " + Lilase.auctionHouse.getAuctionsSniped();
                TextRenderer.drawString(lines, 0, 0, 1.5, GUI_COLOR.getRGB());
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInventoryRendering(GuiScreenEvent.BackgroundDrawnEvent event) {
        String windowName = InventoryUtils.getInventoryName();
        if ("BIN Auction View".equals(windowName)) {
            ItemStack is = InventoryUtils.getStackInOpenContainerSlot(31);
            if (is != null && (is.getItem() == Items.gold_nugget || (AHConfig.BED_SPAM && is.getItem() == Items.bed))) {
                buying = true;
                windowId = Lilase.mc.thePlayer.openContainer.windowId;
                Lilase.mc.playerController.windowClick(windowId, 31, 0, 0, Lilase.mc.thePlayer);
            } else if (is != null && is.getItem() == Items.potato) {
                buying = false;
                Lilase.mc.thePlayer.closeScreen();
            }
        }
        if (AHConfig.BED_SPAM) {
            if (buying && "Confirm Purchase".equals(windowName)) {
                Lilase.mc.playerController.windowClick(windowId + 1, 11, 0, 0, Lilase.mc.thePlayer);
                buying = false;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceivedEvent event) {
        if (event.packet instanceof S33PacketUpdateSign && Utils.checkInHub() && Flipper.state.equals(START)) {
            new Thread(() -> {
                try {
                    S33PacketUpdateSign packetUpdateSign = (S33PacketUpdateSign) event.packet;
                    IChatComponent[] lines = packetUpdateSign.getLines();
                    Utils.debugLog("[Flipper] Item price should be " + flipper.getItemPrice());
                    lines[0] = IChatComponent.Serializer.jsonToComponent("{\"text\":\"" + flipper.getItemPrice() + "\"}");
                    Thread.sleep(1500);
                    C12PacketUpdateSign packetUpdateSign1 = new C12PacketUpdateSign(packetUpdateSign.getPos(), lines);
                    sendPacketWithoutEvent(packetUpdateSign1);
                } catch (IOException | InterruptedException | RuntimeException e) {
                    e.printStackTrace();
                }

            }).start();
        }

    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Unload event) {
        buying = false;
    }


}
