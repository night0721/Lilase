package me.night0721.lilase.features.cofl;

import me.night0721.lilase.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Queue {
    private final List<QueueItem> queue = new ArrayList<>();
    private boolean running = false, clearTaskRunning = false;
    public void add(QueueItem item) {
        this.queue.add(item);
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
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.queue.clear();
                this.setRunning(false);
                this.clearTaskRunning = false;
                Utils.debugLog("Cleared queue.");
            }).start();
        }
    }
}