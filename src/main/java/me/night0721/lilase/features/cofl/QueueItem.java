package me.night0721.lilase.features.cofl;

import lombok.Getter;
import me.night0721.lilase.features.flipper.Flipper;
import me.night0721.lilase.utils.Utils;

// TODO: Implement Lombok Library for clean code
public class QueueItem {
    public @Getter
    final String command, name;
    public @Getter
    final int price;
    public @Getter
    final int target;
    public @Getter
    final String uid;
    public Flipper flipper;

    public QueueItem(String command, String name, int price, int target, String uid) {
        this.command = command;
        this.name = name;
        this.price = price;
        this.target = target;
        this.uid = uid;
    }

    public void openAuction() {
        Utils.debugLog("Executing: " + command);
        Utils.sendServerMessage("/viewauction " + command);
    }
}