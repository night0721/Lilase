package me.night0721.lilase.events;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.features.cofl.QueueItem;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.features.flipper.FlipperState;
import me.night0721.lilase.utils.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.*;

import static me.night0721.lilase.config.AHConfig.*;
import static me.night0721.lilase.features.flipper.Flipper.*;
import static me.night0721.lilase.utils.InventoryUtils.clickWindow;

public class SniperFlipperEvents {
    public static final ArrayList<Flipper> selling_queue = new ArrayList<>();
    public static boolean ah_full = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Lilase.mc.thePlayer == null || event.phase != TickEvent.Phase.START) return;
        try {
            if (selling_queue.get(0) != null) selling_queue.forEach(Flipper::onTick);
        } catch (Exception ignored) {
        }
    }

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
                        Utils.debugLog("Interrupting Flipper selling");
                        selling_queue.get(0).sendInterrupt();
                        selling_queue.remove(0);
                        Lilase.mc.thePlayer.closeScreen();
                        Flipper.state = FlipperState.NONE;
                        Lilase.cofl.toggleAuction();
                    } catch (Exception ignored) {
                    }
                }).start();
//            } else if (Lilase.pageFlipper.state != PageFlipperState.NONE) {
//                new Thread(() -> {
//                    Utils.debugLog("[PageFlipper] Interrupting PageFlipper sniping");
//                    Lilase.pageFlipper.toggleAuction();
//                }).start();
            } else if (Lilase.cofl.isOpen()) {
                Lilase.cofl.toggleAuction();
            }
        }
    }

    private int latestWindowId = -1;
    private Thread spam;

    @SubscribeEvent
    public void onPacketReceive(PacketReceivedEvent event) {
        if (AHConfig.BED_SPAM && Lilase.cofl.isOpen()) {
//        if (AHConfig.BED_SPAM && (Lilase.pageFlipper.isOpen() || Lilase.cofl.isOpen())) {
            if (event.packet instanceof S2DPacketOpenWindow && ((S2DPacketOpenWindow) event.packet).getGuiId().equals("minecraft:chest")) {
                S2DPacketOpenWindow packetOpenWindow = (S2DPacketOpenWindow) event.packet;
                if (packetOpenWindow.getWindowTitle().getUnformattedText().equals("BIN Auction View"))
                    latestWindowId = packetOpenWindow.getWindowId();
            }
            if (event.packet instanceof S2FPacketSetSlot) {
                S2FPacketSetSlot packetSetSlot = (S2FPacketSetSlot) event.packet;
                ItemStack stack = packetSetSlot.func_149174_e();
                if (packetSetSlot.func_149173_d() == 31 && stack != null && packetSetSlot.func_149175_c() == latestWindowId) {
                    ItemStack itemStack = packetSetSlot.func_149174_e();
                    Utils.debugLog("Slot 31: " + itemStack.getItem().getRegistryName());
                    if (itemStack.getItem() == Items.bed) {
                        spam = new Thread(() -> {
                            int tries = 0;
                            try {
                                while (tries < 50) {
                                    if (InventoryUtils.inventoryNameStartsWith("BIN Auction View")) {
                                        clickWindow(latestWindowId, 31);
                                        Thread.sleep(300 + new Random().nextInt(100));
                                        clickWindow(latestWindowId, 11);
                                        tries++;
                                        Thread.sleep(BED_SPAM_DELAY);
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        });
                        spam.start();
                        new Thread(() -> {
                            try {
                                Thread.sleep(4000);
                                spam.interrupt();
                            } catch (Exception ignored) {
                            }
                        }).start();
                    } else if (itemStack.getItem() == Items.gold_nugget || Item.getItemFromBlock(Blocks.gold_block) == itemStack.getItem()) {
                        if (spam != null && spam.isAlive()) {
                            spam.interrupt();
                        }
                        try {
                            clickWindow(latestWindowId, 31);
                            Thread.sleep(300 + new Random().nextInt(100));
                            clickWindow(latestWindowId, 11);
                        } catch (Exception ignored) {

                        }
                    } else {
                        Utils.debugLog("Auction was bought by someone else, closing window");
                        Lilase.mc.thePlayer.closeScreen();
                    }
                } else if (stack != null && packetSetSlot.func_149175_c() == 0 && Lilase.cofl.isOpen()) {
//                } else if (stack != null && packetSetSlot.func_149175_c() == 0 && (Lilase.pageFlipper.isOpen() || Lilase.cofl.isOpen())) {
                    try {
                        String uuid = stack.getTagCompound().getCompoundTag("ExtraAttributes").getString("uuid");
                        String uid = uuid.split("-")[4];
                        QueueItem item = Lilase.cofl.getQueue().getHistoryByUID(uid);
                        if (item != null) {
                            String unFormattedName = ScoreboardUtils.cleanSB(stack.getDisplayName());
                            int slot = packetSetSlot.func_149173_d();
                            NBTTagCompound tag = stack.getTagCompound();
                            System.out.println("Slot: " + slot + "\nStack Name: " + unFormattedName + " \nStack NBT: " + tag);
                            HashMap<String, String> map = new HashMap<>();
                            map.put("name", unFormattedName);
                            map.put("price", format.format(item.price));
                            Lilase.cofl.bought_items.add(map);
                            new Thread(() -> {
                                if (!ONLY_SNIPER) {
                                    item.flipper = new Flipper(unFormattedName, item.price, item.target, uuid);
                                    item.flipper.sendBought();
                                    Utils.debugLog("Bought an item, starting to sell");
                                    System.out.println("Item Name: " + item.flipper.name);
                                    System.out.println("Item Price: " + item.flipper.price);
                                    System.out.println("Target Price: " + item.flipper.target);
                                    selling_queue.add(item.flipper);
                                    item.flipper.sellItem();
                                }
                            }).start();
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
