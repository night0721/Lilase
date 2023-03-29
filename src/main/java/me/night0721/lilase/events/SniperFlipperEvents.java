package me.night0721.lilase.events;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.features.flipper.FlipperState;
import me.night0721.lilase.features.sniper.PageFlipperState;
import me.night0721.lilase.gui.TextRenderer;
import me.night0721.lilase.player.EffectState;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.night0721.lilase.config.AHConfig.GUI_COLOR;
import static me.night0721.lilase.config.AHConfig.SEND_MESSAGE;
import static me.night0721.lilase.features.flipper.Flipper.rotation;
import static me.night0721.lilase.features.flipper.FlipperState.START;
import static me.night0721.lilase.features.sniper.Sniper.flipper;
import static me.night0721.lilase.utils.PlayerUtils.sendPacketWithoutEvent;

public class SniperFlipperEvents {
    private int windowId = 1, price;
    private boolean buying = false, bought = false;
    private final Clock clock = new Clock();
    private final Pattern
            AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$"),
            BOUGHT_PATTERN = Pattern.compile("You purchased (\\w+(?:\\s+\\w+)*) for ([\\d,]+)\\s*(\\w+)!"),
            BOUGHT_PATTERN_2 = Pattern.compile("You claimed (.+?) from (.+?)'s auction!"),
            BOUGHT_PATTERN_3 = Pattern.compile("You (purchased|claimed)( (\\\\d+x))? ([^\\\\s]+(\\\\s+[^\\\\d,]+)*)((,| for) (\\\\d+,?)+ coins?(!)?)?");
    // TODO: Split into one pattern
    public static final List<String> postedNames = new ArrayList<>();

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) throws InterruptedException {
        String message = event.message.getUnformattedText();
        Matcher matcher = AUCTION_SOLD_PATTERN.matcher(message);
        Matcher boughtMatcher = BOUGHT_PATTERN.matcher(message);
        Matcher boughtMatcher2 = BOUGHT_PATTERN_2.matcher(message);
        Matcher boughtMatcher3 = BOUGHT_PATTERN_3.matcher(message);
        if (!message.contains(":")) {
            if (message.equals("You didn't participate in this auction!")) {
                Utils.debugLog("Failed to buy item, not fast enough. Closing the menu");
                InventoryUtils.clickOpenContainerSlot(49);
            } else if (message.equals("You don't have enough coins to afford this bid!")) {
                Utils.debugLog("Failed to buy item, not enough money. Closing the menu");
                InventoryUtils.clickOpenContainerSlot(49);
            } else if (message.contains("Your new API key is")) {
                Utils.debugLog("Detected new API key, saving it to config");
                Utils.debugLog("Saved new API key to config");
                String apiKey = message.replace("Your new API key is ", "");
                Lilase.configHandler.setString("APIKey", apiKey);
            } else if ((boughtMatcher.matches() || boughtMatcher2.matches() || boughtMatcher3.matches()) && bought) {
                new Thread(() -> {
                    bought = false;
                    Utils.debugLog("Bought an item, starting to sell");
                    try {
                        if (SEND_MESSAGE) Lilase.sniper.webhook.execute();
                    } catch (Exception e) {
                        Utils.debugLog("Failed to send webhook");
                    }
                    if (flipper == null)
                        price = Lilase.cofl.price;
                    else price = flipper.getItemPrice();
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (!AHConfig.ONLY_SNIPER) flipper.sellItem();
                }).start();
            } else if (message.equals("Your starting bid must be at least 10 coins!")) {
                InventoryUtils.clickOpenContainerSlot(13);
                Lilase.mc.thePlayer.closeScreen();
                Utils.debugLog("Cannot post item as the cost is too low, stopping fliiper and starting sniper");
                Lilase.sniper.toggleAuction();
                Flipper.state = FlipperState.NONE;
            } else if (message.contains("Can't create a BIN auction for this item for a PRICE this LOW!")) {
                Lilase.mc.thePlayer.closeScreen();
                Utils.debugLog("Cannot post item as the cost is too low, stopping fliiper and starting sniper");
                Lilase.sniper.toggleAuction();
                Flipper.state = FlipperState.NONE;
            } else if (message.contains("You were spawned in Limbo")) {
                Utils.debugLog("Detected in Limbo, stopping everything for 5 minutes");
                Utils.addTitle("You got sent to Limbo!");
                Flipper.state = FlipperState.NONE;
                if (Lilase.sniper.getOpen()) Lilase.sniper.toggleAuction();
                Thread.sleep(5000);
                Utils.sendServerMessage("/lobby");
                Thread.sleep(5000);
                Utils.sendServerMessage("/skyblock");
                Thread bzchillingthread = new Thread(bazaarChilling);
                bzchillingthread.start();
            } else if (matcher.matches() && postedNames.contains(matcher.group(2))) {
                Lilase.sniper.incrementAuctionsFlipped();
            }
        }
    }

    private final Runnable bazaarChilling = () -> {
        try {
            rotation.reset();
            rotation.easeTo(103f, -11f, 1000);
            Thread.sleep(1500);
            KeyBindingManager.updateKeys(true, false, false, false, false, true, false);
            long timeout = System.currentTimeMillis();
            boolean timedOut = false;
            while (BlockUtils.getRelativeBlock(0, 0, 1) != Blocks.spruce_stairs) {
                if ((System.currentTimeMillis() - timeout) > 10000) {
                    Utils.debugLog("Couldn't find bz, gonna chill here");
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
                    // TODO: Remove duplication
                    Lilase.mc.thePlayer.closeScreen();
                }
            } else {
                Thread.sleep(1000 * 60 * 5);
            }
            Lilase.mc.thePlayer.sendChatMessage("/hub");
            Thread.sleep(6000);
            Lilase.sniper.toggleAuction();
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
                    Utils.debugLog("Interrupting Flipper selling");
                    Lilase.mc.thePlayer.closeScreen();
                    Flipper.state = FlipperState.NONE;
                    Lilase.sniper.toggleAuction();
                }).start();
            } else if (Lilase.pageFlipper.state != PageFlipperState.NONE) {
                new Thread(() -> {
                    Utils.debugLog("[PageFlipper] Interrupting PageFlipper sniping");
                    Lilase.pageFlipper.toggleAuction();
                }).start();
            } else if (Lilase.sniper.getOpen()) {
                Lilase.sniper.toggleAuction();
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
                int days = (int) (Lilase.mc.theWorld.getWorldTime() / 24000);
                String lines = "X: " + Math.round(Lilase.mc.thePlayer.posX) + "\n" + "Y: " + Math.round(Lilase.mc.thePlayer.posY) + "\n" + "Z: " + Math.round(Lilase.mc.thePlayer.posZ) + "\n" + time + "\n" + "FPS: " + Minecraft.getDebugFPS() + "\n" + "Day: " + days + "\n" + "Auctions Sniped: " + Lilase.sniper.getAuctionsSniped() + "\n" + "Auctions Posted: " + Lilase.sniper.getAuctionsPosted() + "\n" + "Auctions Flipped: " + Lilase.sniper.getAuctionsFlipped() + "\n";
                TextRenderer.drawString(lines, 0, 0, 0.9, GUI_COLOR.getRGB());
            }
        } else if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            TextRenderer.drawGradientString(Lilase.mc.fontRendererObj, "Lilase", 50, 100, 0x00FBAA, 0xFF3EFC);
            TextRenderer.drawAnimatedString(Lilase.mc.fontRendererObj, "Hong Kong No.1", 50, 110, 0x00FBAA, 0xFF3EFC, 0.5f);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInventoryRendering(GuiScreenEvent.BackgroundDrawnEvent event) {
        String windowName = InventoryUtils.getInventoryName();
        if (AHConfig.BED_SPAM && (Lilase.sniper.getOpen() || Lilase.pageFlipper.getOpen() || Lilase.cofl.getOpen())) {
            if ("BIN Auction View".equals(windowName)) {
                ItemStack is = InventoryUtils.getStackInOpenContainerSlot(31);
                if (is != null) {
                    System.out.println("Slot 31: " + is.getItem().getRegistryName());
                    buying = true;
                    windowId = Lilase.mc.thePlayer.openContainer.windowId;
                    if (is.getItem() == Items.bed && clock.passed()) {
                        Lilase.mc.playerController.windowClick(windowId, 31, 0, 0, Lilase.mc.thePlayer);
                        clock.schedule(AHConfig.BED_SPAM_DELAY);
                    } else if (is.getItem() == Items.gold_nugget)
                        Lilase.mc.playerController.windowClick(windowId, 31, 0, 0, Lilase.mc.thePlayer);
                    else if (is.getItem() == Items.potato) {
                        buying = false;
                        Lilase.mc.thePlayer.closeScreen();
                    } else if (is.getItem() == Items.feather) {
                        buying = false;
                        Lilase.mc.thePlayer.closeScreen();
                    } else {
                        System.out.println("Not potato, not bed, not nugget, not feather??");
                        buying = false;
                        Lilase.mc.thePlayer.closeScreen();
                    }
                } else {
                    System.out.println("No item in slot 31, ??");
//                    buying = false;
//                    Lilase.mc.thePlayer.closeScreen();
                }
            }
            if (buying && "Confirm Purchase".equals(windowName)) {
                System.out.println("Window ID: " + Lilase.mc.thePlayer.openContainer.windowId);
                Lilase.mc.playerController.windowClick(windowId + 1, 11, 0, 0, Lilase.mc.thePlayer);
                buying = false;
                if (Lilase.sniper.buying || Lilase.cofl.getQueue().isRunning()) bought = true;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceivedEvent event) {
        if (event.packet instanceof S33PacketUpdateSign && (Flipper.state.equals(START) || Lilase.cofl.getQueue().isRunning())) {
            if (Utils.cookie == EffectState.ON || (Utils.cookie == EffectState.OFF && Utils.checkInHub()))
                new Thread(() -> {
                    try {
                        S33PacketUpdateSign packetUpdateSign = (S33PacketUpdateSign) event.packet;
                        IChatComponent[] lines = packetUpdateSign.getLines();
                        Utils.debugLog("Item price should be " + price);
                        Thread.sleep(300);
                        lines[0] = IChatComponent.Serializer.jsonToComponent("{\"text\":\"" + price + "\"}");
                        C12PacketUpdateSign packetUpdateSign1 = new C12PacketUpdateSign(packetUpdateSign.getPos(), lines);
                        sendPacketWithoutEvent(packetUpdateSign1);
                    } catch (RuntimeException | InterruptedException e) {
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
