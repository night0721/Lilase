package me.night0721.lilase.utils;

import me.night0721.lilase.Lilase;
import me.night0721.lilase.player.EffectState;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.regex.Pattern;

import static me.night0721.lilase.Lilase.mc;

public class Utils {
    public static IChatComponent header = null, footer = null;
    private static final Pattern
            PATTERN_ACTIVE_EFFECTS = Pattern.compile("§r§r§7You have a §r§cGod Potion §r§7active! §r§d([0-9]*?:?[0-9]*?:?[0-9]*)§r"),
            PATTERN_HUB_LOCATIONS = Pattern.compile("(forest|village|farm|mountain|wilderness|community|graveyard|bazaar|auction)");
    public static EffectState cookie, godPot;

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
        return ScoreboardUtils.getSidebarLines().stream().map(ScoreboardUtils::cleanSB).anyMatch(line -> PATTERN_HUB_LOCATIONS.matcher(line).find());
    }

    public static void checkFooter() {
        if (footer != null) {
            for (String line : footer.getFormattedText().split("\n")) {
                boolean foundGodPot = PATTERN_ACTIVE_EFFECTS.matcher(line).matches();
                godPot = (foundGodPot) ? EffectState.ON : EffectState.OFF;
                if (!foundGodPot) {
                    cookie = (line.contains("Not active! Obtain")) ? EffectState.OFF : EffectState.ON;
                    if (!line.contains("Active")) {
                        godPot = EffectState.INDETERMINABLE;
                        cookie = EffectState.INDETERMINABLE;
                    }
                }
            }
        }
    }

    public static void sendMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "[Lilase] " + EnumChatFormatting.RESET + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + message));
    }

    public static void debugLog(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE + "" + "[Lilase] " + message));
    }

    public static void debugLog(String... messages){
        for (String message : messages) debugLog(message);
    }

    public static void sendServerMessage(String message) {
        mc.thePlayer.sendChatMessage(message);
    }
}

