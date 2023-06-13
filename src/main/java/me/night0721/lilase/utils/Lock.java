package me.night0721.lilase.utils;


public class LockWithTimeReset {
    public boolean isLocked = false;

    public static void lock() {
        this.isLocked = true;
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                this.isLocked = false;
            } catch (Exception ignored) {
            }
        }).start();
    }
}
