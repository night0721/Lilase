package me.night0721.lilase.utils;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.player.EffectState;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean inHub = false;
    public static IChatComponent header = null;
    public static IChatComponent footer = null;
    private static final Pattern PATTERN_ACTIVE_EFFECTS = Pattern.compile(
            "§r§r§7You have a §r§cGod Potion §r§7active! §r§d([0-9]*?:?[0-9]*?:?[0-9]*)§r");
    public static EffectState cookie;
    public static EffectState godPot;

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

    public static void checkFooter() {
        //
        boolean foundGodPot = false;
        boolean foundCookieText = false;
        boolean loaded = false;

        if (footer != null) {
            String formatted = footer.getFormattedText();
            for (String line : formatted.split("\n")) {
                Matcher activeEffectsMatcher = PATTERN_ACTIVE_EFFECTS.matcher(line);
                if (activeEffectsMatcher.matches()) {
                    foundGodPot = true;
                } else if (line.contains("§d§lCookie Buff")) {
                    foundCookieText = true;
                } else if (foundCookieText && line.contains("Not active! Obtain")) {
                    foundCookieText = false;
                    cookie = EffectState.OFF;
                } else if (foundCookieText) {
                    foundCookieText = false;
                    cookie = EffectState.ON;
                }
                if (line.contains("Active")) {
                    loaded = true;
                }
            }
            godPot = foundGodPot ? EffectState.ON : EffectState.OFF;
            if (!loaded) {
                godPot = EffectState.INDETERMINABLE;
                cookie = EffectState.INDETERMINABLE;
            }
        }
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

