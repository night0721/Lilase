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

    public void handleMessage(String str) {
        if (AHConfig.SNIPER_MODE != 2) return;
        if (!str.startsWith("Received:")) return;
        Pattern pattern = Pattern.compile("type[\":]*flip");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            Pattern commandPattern = Pattern.compile("/viewauction \\w+");
            Matcher commandMacther = commandPattern.matcher(str);
            Pattern pricePattern = Pattern.compile("§7Med: §b(\\d{1,3}(?:,\\d{3})*)");
            Matcher priceMatcher = pricePattern.matcher(str);
            String[] split = str.split("Received: ");
            JsonObject strJson = new JsonParser().parse(split[1]).getAsJsonObject();
            String data = strJson.get("data").getAsString();
            String itemName = new JsonParser().parse(data).getAsJsonObject().get("itemName").getAsString();
            System.out.println(itemName);
            Pattern namePattern = Pattern.compile("\"itemName\":\"([^\"]+)\"");//Pattern.compile("\"itemName\":\"([^\"]+)\"", Pattern.MULTILINE);
            Matcher nameMatcher = namePattern.matcher(str);
            System.out.println("Command: " + commandMacther.find());
            System.out.println("Price: " + priceMatcher.find());
            System.out.println("Name: " + nameMatcher.find());
            if (commandMacther.find() && priceMatcher.find() && nameMatcher.find()) {
                String command = commandMacther.group();
                Utils.debugLog("Adding auction to queue: " + command);
                Utils.debugLog("Price: " + Integer.parseInt(priceMatcher.group(1).replaceAll(",", "")));
                Utils.debugLog("Name: " + nameMatcher.group(1));
                price = Integer.parseInt(priceMatcher.group(1).replaceAll(",", ""));
                getQueue().add(new QueueItem(command,nameMatcher.group(1), price));
                getQueue().scheduleClear();

            }
        }
    }

    public void toggleAuction() {
        if (getOpen()) {
            Utils.sendMessage("Stopped COFL Sniper");
            Lilase.mc.thePlayer.closeScreen();
            if (thread.isAlive()) {
                queue.clear();
                queue.setRunning(false);
                thread.interrupt();
            }
            setOpen(false);
            UngrabUtils.regrabMouse();
        } else {
            if (Utils.checkInHub()) {
                Utils.sendMessage("Started COFL Sniper");
                setOpen(true);
                if (thread.isAlive()) {
                    queue.clear();
                    queue.setRunning(false);
                    thread.interrupt();
                } else {
                    thread.start();
                }
                UngrabUtils.ungrabMouse();
            } else Utils.sendMessage("Detected not in hub, please go to hub to start");
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
