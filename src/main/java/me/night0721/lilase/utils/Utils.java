package me.night0721.lilase.utils;

import me.night0721.lilase.Lilase;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class Utils {
    public static boolean inHub = false;

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
        Lilase.mc.thePlayer.sendQueue.handleTitle(p1);
        Lilase.mc.thePlayer.sendQueue.handleTitle(p2);
    }

    public static boolean checkInHub() {
        List<String> scoreboard = ScoreboardUtils.getSidebarLines();
        for (String s : scoreboard) {
            String sCleaned = ScoreboardUtils.cleanSB(s);
            if (sCleaned.contains("Forest") || sCleaned.contains("Village") || sCleaned.contains("Farm") || sCleaned.contains("Mountain") || sCleaned.contains("Wilderness") || sCleaned.contains("Community") || sCleaned.contains("Graveyard") || sCleaned.contains("Bazaar") || sCleaned.contains("Auction"))
                inHub = true;
        }
        return inHub;
    }

    public static void sendMessage(String message) {
        Lilase.mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "Liliase" + EnumChatFormatting.RESET + EnumChatFormatting.DARK_GRAY + " » " + EnumChatFormatting.RESET + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + message));
    }

    public static void debugLog(String message) {
        Lilase.mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + "Liliase" + EnumChatFormatting.RESET + EnumChatFormatting.DARK_GRAY + " » " + EnumChatFormatting.RESET + EnumChatFormatting.WHITE + message));
    }

    public static void sendServerMessage(String message) {
        Lilase.mc.thePlayer.sendChatMessage(message);
    }
}
