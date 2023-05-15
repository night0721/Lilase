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
import java.util.stream.Collectors;

import static me.night0721.lilase.Lilase.mc;

public class Utils {
    public static IChatComponent header = null, footer = null;
    private static final Pattern PATTERN_HUB_LOCATIONS = Pattern.compile("(forest|village|farm|mountain|wilderness|community|graveyard|bazaar|auction)");
    private static final Pattern PATTERN_PURSE = Pattern.compile("(Purse|Piggy): (?:§.)?([0-9.,]+)");
    public static EffectState cookie;

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
        Lilase.mc.thePlayer.sendQueue.handleTitle(new S45PacketTitle(0, 20, 0));
        Lilase.mc.thePlayer.sendQueue.handleTitle(new S45PacketTitle(S45PacketTitle.Type.TITLE, new ChatComponentText(Utils.translateAlternateColorCodes(title))));
    }

    public static boolean checkInHub() {
        return ScoreboardUtils.getSidebarLines().stream().map(ScoreboardUtils::cleanSB).map(String::toLowerCase).anyMatch(line -> PATTERN_HUB_LOCATIONS.matcher(line).find());
    }

    public static void checkFooter() {
        if (footer != null) {
            for (String line : footer.getFormattedText().split("\n")) {
                if (line.contains("Not active! Obtain")) {
                    cookie = EffectState.OFF;
                } else {
                    cookie = EffectState.ON;
                }
            }
        }
    }

    public static int getPurse() {
        String purse = "";
        List<String> matches = ScoreboardUtils.getSidebarLines().stream()
                .map(ScoreboardUtils::cleanSB)
                .map(PATTERN_PURSE::matcher)
                .filter(Matcher::find)
                .map(Matcher::group)
                .collect(Collectors.toList());
        String purseline = matches.get(0);
        Matcher matcher = PATTERN_PURSE.matcher(purseline);
        if (matcher.find()) {
            System.out.println("Group: " + matcher.group());
            purse = matcher.group(2);
            purse = purse.replace(",", "");
            purse = purse.replaceAll("\\..*", "");
            Utils.debugLog("Purse: " + purse);
            return Integer.parseInt(purse);
        }
        return Integer.parseInt(purse);

    }


    public static void sendMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + String.valueOf(EnumChatFormatting.BOLD) + "[Lilase] " + EnumChatFormatting.RESET + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + message));
    }

    public static void debugLog(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "[Lilase] " + EnumChatFormatting.RESET + EnumChatFormatting.WHITE + message));
    }

    public static void debugLog(String... messages) {
        for (String message : messages) debugLog(message);
    }

    public static void sendServerMessage(String message) {
        mc.thePlayer.sendChatMessage(message);
    }
}

