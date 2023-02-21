package me.night0721.lilase.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TextRenderer {
    public TextRenderer(Minecraft mc, String text, int x, int y, double scale) {
        double scaleReset = Math.pow(scale, -1);

        GL11.glScaled(scale, scale, scale);
        y -= mc.fontRendererObj.FONT_HEIGHT;
        for (String line : text.split("\n")) {
            y += mc.fontRendererObj.FONT_HEIGHT * scale;
            /*if (ToggleCommand.outlineTextToggled) {
                String noColorLine = StringUtils.stripControlCodes(line);
                mc.fontRendererObj.drawString(noColorLine, (int) Math.round(x / scale) - 1, (int) Math.round(y / scale), 0x000000, false);
                mc.fontRendererObj.drawString(noColorLine, (int) Math.round(x / scale) + 1, (int) Math.round(y / scale), 0x000000, false);
                mc.fontRendererObj.drawString(noColorLine, (int) Math.round(x / scale), (int) Math.round(y / scale) - 1, 0x000000, false);
                mc.fontRendererObj.drawString(noColorLine, (int) Math.round(x / scale), (int) Math.round(y / scale) + 1, 0x000000, false);
                mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), 0xFFFFFF, false);
            } else {*/
            mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), 0xFFFFFF, true);
            //}
        }
        GL11.glScaled(scaleReset, scaleReset, scaleReset);
        GlStateManager.color(1, 1, 1, 1);
    }

    public TextRenderer(Minecraft mc, String text, int x, int y, double scale, Color color) {
        double scaleReset = Math.pow(scale, -1);

        GL11.glScaled(scale, scale, scale);
        y -= mc.fontRendererObj.FONT_HEIGHT;
        for (String line : text.split("\n")) {
            y += mc.fontRendererObj.FONT_HEIGHT * scale;
            /*if (ToggleCommand.outlineTextToggled) {
                String noColorLine = StringUtils.stripControlCodes(line);
                mc.fontRendererObj.drawString(noColorLine, (int) Math.round(x / scale) - 1, (int) Math.round(y / scale), 0x000000, false);
                mc.fontRendererObj.drawString(noColorLine, (int) Math.round(x / scale) + 1, (int) Math.round(y / scale), 0x000000, false);
                mc.fontRendererObj.drawString(noColorLine, (int) Math.round(x / scale), (int) Math.round(y / scale) - 1, 0x000000, false);
                mc.fontRendererObj.drawString(noColorLine, (int) Math.round(x / scale), (int) Math.round(y / scale) + 1, 0x000000, false);
                mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), 0xFFFFFF, false);
            } else {*/
            mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), color.getRGB(), true);
            //}
        }
        GL11.glScaled(scaleReset, scaleReset, scaleReset);
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawString(String text, int x, int y, double scale) {
        new TextRenderer(Minecraft.getMinecraft(), text, x, y, scale);
    }

    public static void drawString(String text, int x, int y, double scale, Color color) {
        new TextRenderer(Minecraft.getMinecraft(), text, x, y, scale, color);
    }

    public void drawCenteredString(String text, int x, int y, double scale) {
        drawString(text, x - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2, y - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, 1);
    }
}