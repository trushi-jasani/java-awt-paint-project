package com.paintapp;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class ImageUtils {
    public static BufferedImage deepCopy(BufferedImage src) {
        if (src == null) return null;
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return copy;
    }

    public static void floodFill(BufferedImage img, int startX, int startY, int targetColorARGB) {
        int width = img.getWidth(), height = img.getHeight();
        int baseColor = img.getRGB(startX, startY);
        if (baseColor == targetColorARGB) return;
        java.util.ArrayDeque<int[]> stack = new java.util.ArrayDeque<>();
        stack.push(new int[]{startX, startY});
        while (!stack.isEmpty()) {
            int[] p = stack.pop();
            int x = p[0], y = p[1];
            if (x < 0 || x >= width || y < 0 || y >= height) continue;
            if (img.getRGB(x, y) != baseColor) continue;
            img.setRGB(x, y, targetColorARGB);
            stack.push(new int[]{x+1,y});
            stack.push(new int[]{x-1,y});
            stack.push(new int[]{x,y+1});
            stack.push(new int[]{x,y-1});
        }
    }
}
