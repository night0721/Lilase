package me.night0721.lilase.features.cofl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.night0721.lilase.Lilase;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.utils.UngrabUtils;
import me.night0721.lilase.utils.Utils;

import java.io.PrintStream;
import java.util.regex.Pattern;

public class Cofl {
    private final Queue queue = new Queue();
    private boolean open = false;
    public static int price = 0;
    public Thread thread = new Thread(() -> {
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
            if (AHConfig.SNIPER_MODE != 2 || !getOpen() || !str.startsWith("Received:")) return;
            if (pattern.matcher(str).find()) {
                String[] split = str.split("Received: ");
                JsonObject auction = new JsonParser().parse(new JsonParser().parse(split[1]).getAsJsonObject().get("data").getAsString()).getAsJsonObject();
                String itemName = auction.get("auction").getAsJsonObject().get("itemName").getAsString();
                String id = auction.get("auction").getAsJsonObject().get("uuid").getAsString();
                price = auction.get("target").getAsInt();
//                Utils.debugLog("Item Name: " + itemName);
//                Utils.debugLog("ID: " + id);
//                Utils.debugLog("Price: " + price);
                if (itemName != null && id != null && price != 0) {
                    Utils.debugLog("Adding auction to queue: " + id, "Target Price: " + price, "Name: " + itemName);
                    getQueue().add(new QueueItem(id, itemName, price));
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
            if (Utils.checkInHub()) {
                Utils.sendMessage("Started COFL Sniper");
                setOpen(true);
                boolean threadStatus = thread.isAlive();
                stopThread();
                if (!threadStatus) {
                    thread.start();
                }
                UngrabUtils.ungrabMouse();
            } else Utils.sendMessage("Detected not in hub, please go to hub to start");
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
