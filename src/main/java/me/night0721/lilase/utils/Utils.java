package me.night0721.lilase.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class Utils {

    public static boolean inDungeon;

    public static String translateAlternateColorCodes(String text) {
        char[] b = text.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrZz".indexOf(b[i + 1]) > -1) {
                b[i] = '§';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static void addTitle(String title) {
        S45PacketTitle p1 = new S45PacketTitle(0, 20, 0);
        S45PacketTitle p2 = new S45PacketTitle(S45PacketTitle.Type.TITLE, new ChatComponentText(Utils.translateAlternateColorCodes(title)));
        Minecraft.getMinecraft().thePlayer.sendQueue.handleTitle(p1);
        Minecraft.getMinecraft().thePlayer.sendQueue.handleTitle(p2);
        Minecraft.getMinecraft().thePlayer.playSound("spidey:sbesound", 1, 1);
    }

    public static void checkForDungeon() {
        List<String> scoreboard = ScoreboardUtils.getSidebarLines();
        for (String s : scoreboard) {
            String sCleaned = ScoreboardUtils.cleanSB(s);
            if (sCleaned.contains("The Catacombs") || (sCleaned.contains("Cleared:") && sCleaned.contains("% ("))) {
                inDungeon = true;
                return;
            }
        }
        inDungeon = false;
    }

    public static void checkInHub() {
        List<String> scoreboard = ScoreboardUtils.getSidebarLines();
        for (String s : scoreboard) {
            String sCleaned = ScoreboardUtils.cleanSB(s);
            if (sCleaned.contains("Hub")) {
                inDungeon = false;
                return;
            }
        }
        inDungeon = true;
    }
    public static void sendMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "Liliase" + EnumChatFormatting.RESET + EnumChatFormatting.DARK_GRAY + " » " + EnumChatFormatting.RESET + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + message));
    }
    public static void sendServerMessage(String message) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
    }
}
