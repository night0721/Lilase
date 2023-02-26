package me.night0721.lilase.gui;

import me.night0721.lilase.Lilase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class TextRenderer {
    public static void drawString(String text, int x, int y, double scale) {
        double scaleReset = Math.pow(scale, -1);
        GL11.glScaled(scale, scale, scale);
        y -= Lilase.mc.fontRendererObj.FONT_HEIGHT * scale;
        for (String line : text.split("\n")) {
            y += Lilase.mc.fontRendererObj.FONT_HEIGHT * scale;
            Lilase.mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), 0xFFFFFF, true);
        }
        GL11.glScaled(scaleReset, scaleReset, scaleReset);
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawString(String text, int x, int y, double scale, int color) {
        double scaleReset = Math.pow(scale, -1);

        GL11.glScaled(scale, scale, scale);
        y -= Lilase.mc.fontRendererObj.FONT_HEIGHT * scale;
        for (String line : text.split("\n")) {
            y += Lilase.mc.fontRendererObj.FONT_HEIGHT * scale;
            Lilase.mc.fontRendererObj.drawString(line, (int) Math.round(x / scale), (int) Math.round(y / scale), color, false);
        }
        GL11.glScaled(scaleReset, scaleReset, scaleReset);
        GlStateManager.color(1, 1, 1, 1);
    }

    public void drawCenteredString(String text, int x, int y, double scale) {
        drawString(text, x - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2, y - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, 1);
    }
}