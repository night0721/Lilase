package me.night0721.lilase.features.cofl;

import lombok.Getter;
import me.night0721.lilase.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Queue {
    public final List<QueueItem> queue = new ArrayList<>();
    private final @Getter List<QueueItem> history = new ArrayList<>();
    private boolean running = false, clearTaskRunning = false;

    public void add(QueueItem item) {
        this.queue.add(item);
        this.history.add(item);
    }

    public QueueItem getHistoryByUID(String uid) {
        for (QueueItem item : this.history) {
            if (uid.contains(item.uid)) return item;
        }
        return null;
    }

    public QueueItem get() {
        QueueItem item = this.queue.get(0);
        queue.remove(0);
        return item;
    }

    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    public void setRunning(boolean isRunning) {
        this.running = isRunning;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void clear() {
        this.queue.clear();
    }

    public void scheduleClear() {
        if (!this.clearTaskRunning) {
            this.clearTaskRunning = true;
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException ignored) {
                }
                this.queue.clear();
                this.setRunning(false);
                this.clearTaskRunning = false;
                Utils.debugLog("Cleared queue.");
            }).start();
        }
    }
}