package com.paintapp;

import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

public class ImageUtils {
    
    public static BufferedImage deepCopy(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        copy.setData(bi.getData());
        return copy;
    }

    public static void floodFill(BufferedImage image, int x, int y, int newRgb) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        int targetRgb;
        try {
            targetRgb = image.getRGB(x, y);
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }

        if (targetRgb == newRgb) {
            return;
        }

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));
        
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            int curX = current.x;
            int curY = current.y;

            if (curX >= 0 && curX < width && curY >= 0 && curY < height && image.getRGB(curX, curY) == targetRgb) {
                
                image.setRGB(curX, curY, newRgb);

                for (int i = 0; i < 4; i++) {
                    int nextX = curX + dx[i];
                    int nextY = curY + dy[i];
                    
                    if (nextX >= 0 && nextX < width && nextY >= 0 && nextY < height) {
                        if (image.getRGB(nextX, nextY) == targetRgb) {
                            queue.add(new Point(nextX, nextY));
                        }
                    }
                }
            }
        }
    }
}