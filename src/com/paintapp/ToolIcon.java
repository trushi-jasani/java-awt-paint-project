package com.paintapp;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ToolIcon extends Canvas {
    private BufferedImage iconImage;
    private Tool tool;
    private PaintCanvas canvas;
    
    private static final Dimension ICON_SIZE = new Dimension(35, 35);

    public ToolIcon(Tool tool, PaintCanvas canvas) {
        this.tool = tool;
        this.canvas = canvas;
        setPreferredSize(ICON_SIZE);
        
        // --- Icon Loading FIX: Use absolute path from classpath root (bin) ---
        // The leading '/' ensures the search starts from the 'bin' directory root.
        String imageName = "/icons/" + tool.name().toLowerCase() + ".png"; 
        
        try {
            // getClass().getResourceAsStream() is usually reliable for resources in the classpath.
            iconImage = ImageIO.read(getClass().getResourceAsStream(imageName));
            
            if (iconImage == null) {
                 System.err.println("Icon file not found or unreadable: " + imageName);
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("FATAL ERROR: Could not load icon: " + imageName + ". Using text fallback.");
        }

        // Add the click listener
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                canvas.setTool(tool);
                // Force the entire sidebar to redraw for highlight update
                getParent().repaint(); 
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        // Draw the background based on selection state
        if (canvas.getTool() == tool) {
            g2.setColor(new Color(255, 255, 102)); // Yellow highlight when selected
        } else {
            g2.setColor(new Color(240, 240, 240)); // Light gray background
        }
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw the actual image
        if (iconImage != null) {
            // Draw the image centered
            int x = (getWidth() - iconImage.getWidth()) / 2;
            int y = (getHeight() - iconImage.getHeight()) / 2;
            g2.drawImage(iconImage, x, y, this);
        } else {
            // Fallback: Draw the first letter if image is missing
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            // Ensure bounds check for safety
            String label = tool.name().substring(0, 1);
            g2.drawString(label, (getWidth() - g2.getFontMetrics().stringWidth(label)) / 2, getHeight() / 2 + 5);
        }
        
        // Draw a light border
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}