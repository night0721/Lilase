package me.night0721.lilase.utils;

import java.awt.*;

/**
 * @author Gabagooooooooooool (ily)
 * @version 1.1
 * Basic utility for aurora theming engine.
 */

public class CurrentColor {
    public static int currentColorGet(float offset) {
        switch (0) {
            case 0:
                return getRainbow(-4000, (int) (offset * 3000)).getRGB();
            case 1:
                return getGradientOffset(new Color(223, 94, 255), new Color(0, 170, 255).brighter(), Math.abs(System.currentTimeMillis() / 16L) / 100.0 + offset).getRGB();
            case 2:
                return getGradientOffset(new Color(255, 0, 255), new Color(255, 255, 0), Math.abs(System.currentTimeMillis() / 16L) / 100.0 + offset).getRGB();
            case 3:
                return getGradientOffset(new Color(0, 132, 99), new Color(255, 255, 99), Math.abs(System.currentTimeMillis() / 16L) / 100.0 + offset).getRGB();
            case 4:
                return getGradientOffset(new Color(255, 0, 0), new Color(255, 255, 0), Math.abs(System.currentTimeMillis() / 16L) / 100.0 + offset).getRGB();
            case 5:
                return getGradientOffset(new Color(0, 0, 255), new Color(0, 255, 132), Math.abs(System.currentTimeMillis() / 16L) / 100.0 + offset).getRGB();
            case 6:
                return getGradientOffset(new Color(250, 250, 250), new Color(5, 5, 5), Math.abs(System.currentTimeMillis() / 16L) / 100.0 + offset).getRGB();
            case 7:
                return getGradientOffset(new Color(231, 239, 239), new Color(16, 16, 24), Math.abs(System.currentTimeMillis() / 16L) / 100.0 + offset).getRGB();
        }
        return Color.WHITE.getRGB();
    }

    public static float getFloatValue(float offset, int color) {
        Color tempC = new Color(CurrentColor.currentColorGet(offset));
        int tempC_ = 0;
        switch (color) {
            case 0:
                tempC_ = tempC.getRed();
                break;
            case 1:
                tempC_ = tempC.getGreen();
                break;
            case 2:
                tempC_ = tempC.getBlue();
                break;

        }
        return (((float) tempC_) / 255F);
    }

    /**
     * Following method has been circulating in Minecraft Hacking Community for a while, making it impossible to trace original author.
     */
    protected static Color getGradientOffset(final Color color1, final Color color2, double offset) {
        if (offset > 1.0) {
            final double left = offset % 1.0;
            final int off = (int) offset;
            offset = ((off % 2 == 0) ? left : (1.0 - left));
        }
        final double inverse_percent = 1.0 - offset;
        final int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
        final int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
        final int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
        return new Color(redPart, greenPart, bluePart);
    }

    /**
     * Following method has been circulating in Minecraft Hacking Community for a while, making it impossible to trace original author.
     */
    protected static Color getRainbow(final int speed, final int offset) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        return Color.getHSBColor(hue / speed, 0.9f, 1f);
    }
}