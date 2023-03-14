package me.night0721.lilase.gui;

import me.night0721.lilase.Lilase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
        drawString(text, x - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2, y - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, scale);
    }

    public static void drawGradientString(FontRenderer fontRenderer, String text, int x, int y, int colorStart, int colorEnd) {
        int textWidth = fontRenderer.getStringWidth(text);
        int startX = x - textWidth / 2;
        int startY = y - fontRenderer.FONT_HEIGHT / 2;

        // calculate color differences
        float rDiff = ((colorEnd >> 16) & 255) - ((colorStart >> 16) & 255);
        float gDiff = ((colorEnd >> 8) & 255) - ((colorStart >> 8) & 255);
        float bDiff = (colorEnd & 255) - (colorStart & 255);

        // render text with gradient colors
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charWidth = fontRenderer.getCharWidth(c);
            float ratio = (float) i / (float) text.length();
            int color = ((int) ((float) ((colorStart >> 16) & 255) + rDiff * ratio) << 16) |
                    ((int) ((float) ((colorStart >> 8) & 255) + gDiff * ratio) << 8) |
                    ((int) ((float) (colorStart & 255) + bDiff * ratio));

            fontRenderer.drawString(Character.toString(c), startX, startY, color);
            startX += charWidth;
        }
    }

    public static void drawAnimatedString(FontRenderer fontRenderer, String text, int x, int y, int startColor, int endColor, float speed) {
        float time = (float) (System.currentTimeMillis() % 10000) / 1000.0f;
        int textWidth = fontRenderer.getStringWidth(text);
        int startX = x - textWidth / 2;
        int startY = y - fontRenderer.FONT_HEIGHT / 2;

        // calculate color differences
        float rDiff = ((endColor >> 16) & 255) - ((startColor >> 16) & 255);
        float gDiff = ((endColor >> 8) & 255) - ((startColor >> 8) & 255);
        float bDiff = (endColor & 255) - (startColor & 255);

        // render text with gradient colors
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charWidth = fontRenderer.getCharWidth(c);
            float ratio = (float) i / (float) text.length();

            // calculate color at current position
            float cyclePosition = (time * speed + ratio) % 1.0f;
            float r = ((startColor >> 16) & 255) + rDiff * cyclePosition;
            float g = ((startColor >> 8) & 255) + gDiff * cyclePosition;
            float b = (startColor & 255) + bDiff * cyclePosition;
            int color = ((int) r << 16) | ((int) g << 8) | (int) b;

            fontRenderer.drawString(Character.toString(c), startX, startY, color);
            startX += charWidth;
        }
    }
}