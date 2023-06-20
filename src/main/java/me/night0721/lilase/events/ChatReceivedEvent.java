package me.night0721.lilase.events;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.features.flipper.FlipperState;
import me.night0721.lilase.remotecontrol.BotUtils;
import me.night0721.lilase.utils.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.night0721.lilase.config.AHConfig.*;
import static me.night0721.lilase.events.SniperFlipperEvents.selling_queue;
import static me.night0721.lilase.features.flipper.Flipper.*;

public class ChatReceivedEvent {
    private final Pattern AUCTION_SOLD_PATTERN = Pattern.compile("^(.*?) bought (.*?) for ([\\d,]+) coins CLICK$");

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
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
                    HashMap<String, String> sold_item = new HashMap<>();
                    sold_item.put("item", matcher.group(2));
                    sold_item.put("price", matcher.group(3));
                    Lilase.cofl.sold_items.add(sold_item);
                    if (SEND_MESSAGE) {
                        try {
                            webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Someone bought an item!").setFooter("Purse: " + format.format(Utils.getPurse()), icon).addField("Item:", matcher.group(2), true).addField("Price:", matcher.group(3), true).addField("Purchaser:", purchaser, true).setColor(Color.decode("#003153")));
                            webhook.execute();
                            Utils.debugLog("Notified Webhook");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Utils.debugLog("Failed to send webhook");
                        }
                    }
                    if (AUTO_CLAIM) {
                        if (state == FlipperState.NONE) {
                            Utils.debugLog("Claiming items as if Claimer and Flipper aren't open");
                            if (!Lilase.claimer.isOpen()) Lilase.claimer.toggle();
                        } else {
                            Utils.debugLog("Claiming items after Flipper is done");
                        }
                    }
                }
            }
            if (message.contains("You didn't participate in this auction!")) {
                Utils.debugLog("Failed to buy item, not fast enough. Closing the menu");
                InventoryUtils.clickOpenContainerSlot(49);
            }
            if (message.contains("You don't have enough coins to afford this bid!")) {
                Utils.debugLog("Failed to buy item, not enough money. Closing the menu");
                InventoryUtils.clickOpenContainerSlot(49);
            }
            if (message.contains("You don't have enough coins to afford this!")) {
                Utils.debugLog("Failed to sell item, not enough money. Closing the menu");
                new Thread(() -> {
                    try {
                        selling_queue.get(0).sendNotEnoughCoins();
                        selling_queue.remove(0);
                        Lilase.mc.thePlayer.closeScreen();
                        Flipper.state = FlipperState.NONE;
                        Lilase.cofl.toggleAuction();
                    } catch (Exception ignored) {
                    }
                }).start();
            }
            if (message.contains("Your starting bid must be at least 10 coins!") || message.contains("Can't create a BIN auction for this item for a PRICE this LOW!")) {
                InventoryUtils.clickOpenContainerSlot(13);
                Lilase.mc.thePlayer.closeScreen();
                selling_queue.get(0).sendNotEnoughCoins();
                selling_queue.remove(0);
                Utils.debugLog("Cannot post item as the cost is too low, stopping fliper and starting sniper");
                Flipper.state = FlipperState.NONE;
                Lilase.cofl.toggleAuction();
            }
            if (message.contains("You were spawned in Limbo") || message.contains("return from AFK")) {
                try {
                    Utils.debugLog("Detected in Limbo or Lobby, sending you back to skyblock");
                    Utils.addTitle("You got sent to Limbo or Lobby!");
                    Flipper.state = FlipperState.NONE;
                    if (Lilase.cofl.isOpen()) Lilase.cofl.toggleAuction();
                    Thread.sleep(30000 + new Random().nextInt(5000));
                    Utils.sendServerMessage("/lobby");
                    Thread.sleep(30000 + new Random().nextInt(5000));
                    Utils.sendServerMessage("/skyblock");
                    Thread.sleep(30000 + new Random().nextInt(5000));
                    Utils.sendServerMessage("/hub");
                    if (!Lilase.cofl.isOpen()) Lilase.cofl.toggleAuction();
//                     Thread bzchillingthread = new Thread(bazaarChilling);
//                     bzchillingthread.start();
                } catch (Exception ignored) {
                }
            }
            if (message.contains("Hello there, you acted suspiciously like a macro bot")) {
                Utils.debugLog("Detected macro");
                if (REMOTE_CONTROL) {
                    TextChannel channel = Lilase.remoteControl.bot.getTextChannelById(Long.parseLong(LOG_CHANNEL));
                    if (channel != null) {
                        FileUpload screenshot = FileUpload.fromData(new File(Objects.requireNonNull(BotUtils.takeScreenShot())), "image.png");
                        channel.sendMessageEmbeds(
                                new EmbedBuilder()
                                        .setTitle("Detected as macro").
                                        setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                                        .setDescription("Detected as macro, please login to solve the captcha as soon as possible")
                                        .setColor(Color.decode("#003153"))
                                        .setImage("attachment://image.png")
                                        .build()
                        ).addFiles(screenshot).queue();
                    }
                }
                if (SEND_MESSAGE) {
                    try {
                        webhook.addEmbed(
                                new DiscordWebhook.EmbedObject()
                                        .setTitle("Detected as macro")
                                        .setFooter("Purse: " + format.format(Utils.getPurse()), icon)
                                        .setDescription("Detected as macro, please login to solve the captcha as soon as possible")
                                        .setColor(Color.decode("#003153")));
                        webhook.execute();
                        Utils.debugLog("Notified Webhook");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.debugLog("Failed to send webhook");
                    }
                }
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
            Random random = new Random();
            if (!timedOut) {
                // about 5 minutes
                for (int i = 0; i < 50; i++) {
                    Thread.sleep(6000);
                    KeyBindingManager.rightClick();
                    Thread.sleep(3000);
                    InventoryUtils.clickOpenContainerSlot(random.nextInt(20));
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

}
