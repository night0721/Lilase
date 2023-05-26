package me.night0721.lilase.utils;

import java.security.MessageDigest;

public class HWIDUtils {
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = "0123456789ABCDEF".toCharArray()[v >>> 4];
            hexChars[j * 2 + 1] = "0123456789ABCDEF".toCharArray()[v & 0xF];
        }
        return new String(hexChars);
    }

    public static String getID() {
        try {
            MessageDigest hash = MessageDigest.getInstance("MD5");
            String s = System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("os.version") + Runtime.getRuntime().availableProcessors() + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_REVISION") + System.getenv("USERNAME") + System.getenv("PROCESSOR_LEVEL") + System.getenv("windir");
            return bytesToHex(hash.digest(s.getBytes()));
        } catch (Exception ignored) {
            return "";
        }
    }
}
