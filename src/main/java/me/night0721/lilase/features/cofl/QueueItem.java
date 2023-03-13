package me.night0721.lilase.features.cofl;

import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.utils.Utils;

public class QueueItem {
    private final String command;
    private final String name;
    private final int price;

    public QueueItem(String command, String name, int price) {
        this.command = command;
        this.name = name;
        this.price = price;
    }

    public void openAuction() {
        Utils.debugLog("Executing: " + command);
        Utils.sendServerMessage(command);
        new Flipper(name, price);
    }
}