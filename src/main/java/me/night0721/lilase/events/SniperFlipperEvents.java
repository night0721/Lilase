package me.night0721.lilase.events;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.features.cofl.QueueItem;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.features.flipper.FlipperState;
import me.night0721.lilase.features.sniper.PageFlipperState;
import me.night0721.lilase.gui.TextRenderer;
import me.night0721.lilase.player.EffectState;
import me.night0721.lilase.utils.*;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.night0721.lilase.config.AHConfig.*;
import static me.night0721.lilase.features.flipper.Flipper.*;
import static me.night0721.lilase.features.flipper.FlipperState.START;
import static me.night0721.lilase.utils.PlayerUtils.sendPacketWithoutEvent;

public class SniperFlipperEvents {
    private final Clock clock = new Clock();
    public static final ArrayList<Flipper> selling_queue = new ArrayList<>();
    private final Pattern AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) throws InterruptedException {
        String message = event.message.getUnformattedText();
        if (!message.contains(":")) {
            if (message.startsWith("§6[Auction]")) {
                // §6[Auction] §aphiinix_ §ebought §fImplosion Belt §efor §6900,000 coins §lCLICK
                Matcher matcher = AUCTION_SOLD_PATTERN.matcher(ScoreboardUtils.cleanSB(message));
                if (matcher.matches()) {
                    String purchaser;
                    try {
                        purchaser = matcher.group(1).split("\\[Auction] ")[1];
                    } catch (Exception ignored) {
                        purchaser = message.split("\\[Auction] ")[1].split(" bought")[0];
                    }
                    if (SEND_MESSAGE) {
                        try {
                            webhook.setUsername("Lilase");
                            webhook.setAvatarUrl(icon);
                            webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Someone bought an item!").setFooter("Purse: " + format.format(Utils.getPurse()), icon).addField("Item:", matcher.group(2), true).addField("Price:", matcher.group(3), true).addField("Purchaser:", purchaser, true).setColor(Color.decode("#003153")));
                            webhook.execute();
                            Utils.debugLog("Notified Webhook");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.debugLog("Failed to send webhook");
                        }
                    }
                    Lilase.sniper.incrementAuctionsFlipped();
                }
            } else if (message.equals("You didn't participate in this auction!")) {
                Utils.debugLog("Failed to buy item, not fast enough. Closing the menu");
                InventoryUtils.clickOpenContainerSlot(49);
            } else if (message.equals("You don't have enough coins to afford this bid!")) {
                Utils.debugLog("Failed to buy item, not enough money. Closing the menu");
                InventoryUtils.clickOpenContainerSlot(49);
            } else if (message.equals("Your starting bid must be at least 10 coins!") || message.contains("Can't create a BIN auction for this item for a PRICE this LOW!")) {
                InventoryUtils.clickOpenContainerSlot(13);
                Lilase.mc.thePlayer.closeScreen();
                Utils.debugLog("Cannot post item as the cost is too low, stopping fliiper and starting sniper");
                Lilase.cofl.toggleAuction();
                Flipper.state = FlipperState.NONE;
            } else if (message.contains("You were spawned in Limbo")) {
                Utils.debugLog("Detected in Limbo, stopping everything for 5 minutes");
                Utils.addTitle("You got sent to Limbo!");
                Flipper.state = FlipperState.NONE;
                if (Lilase.cofl.getOpen()) Lilase.cofl.toggleAuction();
                Thread.sleep(5000);
                Utils.sendServerMessage("/lobby");
                Thread.sleep(5000);
                Utils.sendServerMessage("/skyblock");
                Thread bzchillingthread = new Thread(bazaarChilling);
                bzchillingthread.start();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Lilase.mc.thePlayer == null || event.phase != TickEvent.Phase.START) return;
        try {
            if (selling_queue.get(0) != null) selling_queue.forEach(Flipper::switchStates);
        } catch (Exception ignored) {
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
            Lilase.cofl.toggleAuction();
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
                    selling_queue.get(0).sendInterrupt();
                    selling_queue.remove(0);
                    Lilase.mc.thePlayer.closeScreen();
                    Flipper.state = FlipperState.NONE;
                    Lilase.cofl.toggleAuction();
                }).start();
            } else if (Lilase.pageFlipper.state != PageFlipperState.NONE) {
                new Thread(() -> {
                    Utils.debugLog("[PageFlipper] Interrupting PageFlipper sniping");
                    Lilase.pageFlipper.toggleAuction();
                }).start();
            } else if (Lilase.cofl.getOpen()) {
                Lilase.cofl.toggleAuction();
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
            if (AHConfig.HKNO1) {
                TextRenderer.drawGradientString(Lilase.mc.fontRendererObj, "Lilase", 50, 100, 0x00FBAA, 0xFF3EFC);
                TextRenderer.drawAnimatedString(Lilase.mc.fontRendererObj, "Hong Kong No.1", 50, 110, 0x00FBAA, 0xFF3EFC, 0.5f);
            }
        }
    }

    private int latestWindowId = -1;

    @SubscribeEvent
    public void onPacketReceive(PacketReceivedEvent event) {
        if (event.packet instanceof S2FPacketSetSlot) {
            S2FPacketSetSlot packetSetSlot = (S2FPacketSetSlot) event.packet;
            ItemStack stack = packetSetSlot.func_149174_e();
            if (stack != null && packetSetSlot.func_149175_c() == 0) {
                try {
                    String uuid = stack.getTagCompound().getCompoundTag("ExtraAttributes").getString("uuid");
                    String uid = uuid.split("-")[4];
                    QueueItem item = Lilase.cofl.getQueue().getHistoryByUID(uid);
                    if (item != null) {
                        String unFormattedName = ScoreboardUtils.cleanSB(stack.getDisplayName());
                        int slot = packetSetSlot.func_149173_d();
                        NBTTagCompound tag = stack.getTagCompound();
                        System.out.println("Slot: " + slot + "\nStack Name: " + unFormattedName + " \nStack NBT: " + tag);
                        new Thread(() -> {
                            if (!ONLY_SNIPER) {
                                item.flipper = new Flipper(item.name, item.price, item.target, uuid);
                                item.flipper.sendBought();
                                Utils.debugLog("Bought an item, starting to sell");
                                System.out.println("Item Name: " + item.flipper.name);
                                System.out.println("Item Price: " + item.flipper.price);
                                System.out.println("Target Price: " + item.flipper.target);
                                selling_queue.add(item.flipper);
                                item.flipper.sellItem();
                            } else {
                                Utils.debugLog("Bought an item, not selling because only sniper is enabled");
                            }
                        }).start();
                    }
                } catch (Exception ignored) {
                }

            }
        }
        if (AHConfig.BED_SPAM && (Lilase.pageFlipper.getOpen() || Lilase.cofl.getOpen())) {
            if (event.packet instanceof S2DPacketOpenWindow && ((S2DPacketOpenWindow) event.packet).getGuiId().equals("minecraft:chest")) {
                S2DPacketOpenWindow packetOpenWindow = (S2DPacketOpenWindow) event.packet;
                if (packetOpenWindow.getWindowTitle().getUnformattedText().equals("BIN Auction View"))
                    latestWindowId = packetOpenWindow.getWindowId();
            }
            if (event.packet instanceof S2FPacketSetSlot) {
                S2FPacketSetSlot packetSetSlot = (S2FPacketSetSlot) event.packet;
                if (packetSetSlot.func_149173_d() == 31 && packetSetSlot.func_149174_e() != null && packetSetSlot.func_149175_c() == latestWindowId) {
                    ItemStack itemStack = packetSetSlot.func_149174_e();
                    Utils.debugLog("Slot 31: " + itemStack.getItem().getRegistryName());
                    if (itemStack.getItem() == Items.bed && clock.passed()) {
                        clickWindow(latestWindowId, 31);
                        clickWindow(latestWindowId + 1, 11);
                        clock.schedule(AHConfig.BED_SPAM_DELAY);
                    } else if (itemStack.getItem() == Items.gold_nugget || Item.getItemFromBlock(Blocks.gold_block) == itemStack.getItem()) {
                        clickWindow(latestWindowId, 31);
                        clickWindow(latestWindowId + 1, 11);
                    } else {
                        Utils.debugLog("Auction was bought by someone else, closing window");
                        Lilase.mc.thePlayer.closeScreen();
                    }
                }
            }
        }
        if (event.packet instanceof S33PacketUpdateSign && (Flipper.state.equals(START) || Lilase.cofl.getQueue().isRunning())) {
            if (Utils.cookie == EffectState.ON || (Utils.cookie == EffectState.OFF && Utils.checkInHub()))
                new Thread(() -> {
                    try {
                        S33PacketUpdateSign packetUpdateSign = (S33PacketUpdateSign) event.packet;
                        IChatComponent[] lines = packetUpdateSign.getLines();
                        Utils.debugLog("Target Price: " + selling_queue.get(0).target);
                        Thread.sleep(300);
                        lines[0] = IChatComponent.Serializer.jsonToComponent("{\"text\":\"" + selling_queue.get(0).target + "\"}");
                        sendPacketWithoutEvent(new C12PacketUpdateSign(packetUpdateSign.getPos(), lines));
                    } catch (RuntimeException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
        }
    }

    public void clickWindow(int window, int slot) {
        Lilase.mc.playerController.windowClick(window, slot, 0, 0, Lilase.mc.thePlayer);
    }
}
