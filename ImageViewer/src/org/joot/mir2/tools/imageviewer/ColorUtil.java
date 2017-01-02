package org.joot.mir2.tools.imageviewer;

import java.awt.*;

/**
 * Created by yang on 2016/11/5.
 */
public class ColorUtil {
    public static boolean rgbSimilar(int color1Rgb, int color2Rgb, int threshold){
        int[] ints = colorFromRgb(color1Rgb);
        Color color1 = new Color(ints[0], ints[1], ints[2]);
        int[] ints1 = colorFromRgb(color2Rgb);
        Color color2 = new Color(ints1[0], ints1[1], ints1[2]);
        return Math.abs(color1.getRed() - color2.getRed()) <= threshold &&
                Math.abs(color1.getGreen() - color2.getGreen()) <= threshold &&
                Math.abs(color1.getBlue() - color2.getBlue()) <= threshold;
    }

    public static int[] colorFromRgb(int rgbVale){
        int[] color = new int[3];
        color[0] = (rgbVale & 0x00ff0000) >> 16;//r
        color[1] = (rgbVale & 0x0000ff00) >> 8;//g
        color[2] = rgbVale & 0x000000ff;//b
        return color;
    }
    //000000000 00000000 00000000
    public static int rgbConvertTo2Byte(int rgb){
        int[] ints = colorFromRgb(rgb);
        int r = ints[0] & 0x1f;
        int g = ints[1] & 0x3f;
        int b = ints[2] & 0x1f;
        int result = 0xffff & (r<<11) & (g<<5) & (b);
        return result;
    }

    public static boolean isColorEqual(Color color1, Color color2){
        return color1.getRed() == color2.getRed() &&
                color1.getGreen() == color2.getGreen() &&
                color1.getBlue() == color2.getBlue();
    }
    public static boolean isWhiteColor(int rgb){
        int[] ints = ColorUtil.colorFromRgb(rgb);
        Color color = new Color(ints[0], ints[1], ints[2]);
        return color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255;
    }
}
