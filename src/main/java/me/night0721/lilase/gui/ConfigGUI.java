package me.night0721.lilase.gui;

import cc.polyfrost.oneconfig.hud.TextHud;
import me.night0721.lilase.utils.PlayerUtils;

import java.util.List;

public class ConfigGUI extends TextHud {

    public ConfigGUI() {
        super(true, 0, 0);
    }
    @Override
    protected void getLines(List<String> lines, boolean example) {
        if (PlayerUtils.mc.thePlayer == null) return;
        lines.add("X: " + Math.round(PlayerUtils.mc.thePlayer.posX));
        lines.add("Y: " + Math.round(PlayerUtils.mc.thePlayer.posY));
        lines.add("Z: " + Math.round(PlayerUtils.mc.thePlayer.posZ));
        // get the time in real life and format it to xx:xx, eg 23:54

    }
}
