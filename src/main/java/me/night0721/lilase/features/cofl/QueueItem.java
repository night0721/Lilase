package me.night0721.lilase.features.cofl;

import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.utils.Utils;

// TODO: Implement Lombok Library for clean code
public class QueueItem {
    private final String command, name;
    private final int price;
    public static Flipper flipper = null;

    public QueueItem(String command, String name, int price) {
        this.command = command;
        this.name = name;
        this.price = price;
    }

    public void openAuction() {
        Utils.debugLog("Executing: " + command);
        Utils.sendServerMessage("/viewauction " + command);
        flipper = new Flipper(name, price);
    }
}