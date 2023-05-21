package me.night0721.lilase.features.cofl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.night0721.lilase.Lilase;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.features.flipper.FlipperState;
import me.night0721.lilase.player.EffectState;
import me.night0721.lilase.utils.UngrabUtils;
import me.night0721.lilase.utils.Utils;

import java.io.PrintStream;
import java.util.Random;
import java.util.regex.Pattern;

import static me.night0721.lilase.config.AHConfig.SEND_MESSAGE;

public class Cofl {
    public final Queue queue = new Queue();
    private boolean open = false;
    public final Thread thread = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) {
            }
            if (getOpen()) {
                if (!this.queue.isEmpty()) {
                    if (!this.queue.isRunning()) {
                        this.queue.setRunning(true);
                        QueueItem item = this.queue.get();
                        item.openAuction();
                    }
                }
            }
        }
    });

    public void onOpen() {
        System.setOut(new PrintStream(System.out) {
            public void println(String str) {
                handleMessage(str);
                super.println(str);
            }
        });
    }

    private final Pattern pattern = Pattern.compile("type[\":]*flip");

    public void handleMessage(String str) {
        try {
            if (!getOpen() || !str.startsWith("Received:")) return;
            if (pattern.matcher(str).find()) {
                Random random = new Random();
                Lilase.mc.thePlayer.inventory.currentItem = random.nextInt(9);
                String[] split = str.split("Received: ");
                JsonObject received = new JsonParser().parse(split[1]).getAsJsonObject();
                if (!received.get("type").getAsString().equals("flip")) return;
                JsonObject auction = new JsonParser().parse(received.get("data").getAsString()).getAsJsonObject();
                String itemName = auction.get("auction").getAsJsonObject().get("itemName").getAsString();
                String id = auction.get("auction").getAsJsonObject().get("uuid").getAsString();
                int price = auction.get("auction").getAsJsonObject().get("startingBid").getAsInt();
                String uid = auction.get("auction").getAsJsonObject().get("flatNbt").getAsJsonObject().get("uid").getAsString();
                int target = auction.get("target").getAsInt();
                if (itemName != null && id != null && price != 0 && target != 0 && uid != null) {
                    Utils.debugLog("Adding auction to queue: " + id, "Price: " + price, "Target Price: " + target, "Name: " + itemName, "UID: " + uid);
                    getQueue().add(new QueueItem(id, itemName, price, target, uid));
                    getQueue().scheduleClear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void toggleAuction() {
        if (getOpen()) {
            Utils.sendMessage("Stopped COFL Sniper");
            Lilase.mc.thePlayer.closeScreen();
            stopThread();
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
            if (Utils.cookie == EffectState.ON || Utils.checkInHub()) {
                Utils.sendMessage("Started COFL Sniper");
                setOpen(true);
                boolean threadStatus = thread.isAlive();
                stopThread();
                if (!threadStatus) {
                    thread.start();
                }
                UngrabUtils.ungrabMouse();
            } else {
                Utils.sendMessage("Detected not in hub, please go to hub to start");
            }
        }
    }

    private void stopThread() {
        if (thread.isAlive()) {
            queue.clear();
            queue.setRunning(false);
            thread.interrupt();
        }
    }

    public boolean getOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Queue getQueue() {
        return this.queue;
    }
}
