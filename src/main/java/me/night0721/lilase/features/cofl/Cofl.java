package me.night0721.lilase.features.cofl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.night0721.lilase.Lilase;
import me.night0721.lilase.config.AHConfig;
import me.night0721.lilase.utils.UngrabUtils;
import me.night0721.lilase.utils.Utils;

import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cofl {
    private final Queue queue = new Queue();
    private boolean open = false;
    public int price = 0;
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
    private final Pattern commandPattern = Pattern.compile("/viewauction \\w+");
    private final Pattern pricePattern = Pattern.compile("§7Med: §b(\\d{1,3}(?:,\\d{3})*)");

    public void handleMessage(String str) {
        try {
            if (AHConfig.SNIPER_MODE != 2 || !getOpen() || !str.startsWith("Received:")) return;
            if (pattern.matcher(str).find()) {
                Matcher commandMacther = commandPattern.matcher(str);
                Matcher priceMatcher = pricePattern.matcher(str);
                String[] split = str.split("Received: ");
                String itemName = new JsonParser().parse(new JsonParser().parse(split[1]).getAsJsonObject().get("data").getAsString()).getAsJsonObject().get("auction").getAsJsonObject().get("itemName").getAsString();
                if (commandMacther.find() && priceMatcher.find() && itemName != null) {
                    String command = commandMacther.group();
                    Utils.debugLog("Adding auction to queue: " + command,
                            "Price: " + Integer.parseInt(priceMatcher.group(1).replaceAll(",", "")),
                            "Name: " + itemName);
                    price = Integer.parseInt(priceMatcher.group(1).replaceAll(",", ""));
                    getQueue().add(new QueueItem(command, itemName, price));
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
                boolean threadStatus = !thread.isAlive();
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
