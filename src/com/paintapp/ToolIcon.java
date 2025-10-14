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
    private boolean isHovered = false;  // for hover effect
    
    private static final Dimension ICON_SIZE = new Dimension(35, 35);

    public ToolIcon(Tool tool, PaintCanvas canvas) {
        this.tool = tool;
        this.canvas = canvas;
        setPreferredSize(ICON_SIZE);

        // --- Load the icon image ---
        String imageName = "/icons/" + tool.name().toLowerCase() + ".png";
        try {
            iconImage = ImageIO.read(getClass().getResourceAsStream(imageName));
            if (iconImage == null) {
                System.err.println("Icon not found: " + imageName);
            }
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading icon: " + imageName);
        }

        // --- Mouse listener for click + hover ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Set the selected tool
                canvas.setTool(tool);

                // Repaint ALL ToolIcons in the same container
                Container parent = getParent();
                if (parent != null) {
                    for (Component c : parent.getComponents()) {
                        if (c instanceof ToolIcon) {
                            c.repaint(); // repaint each box individually
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Background color logic ---
        if (canvas.getTool() == tool) {
            g2.setColor(new Color(255, 245, 160)); // selected = yellow
        } else if (isHovered) {
            g2.setColor(new Color(230, 230, 230)); // hover = light gray
        } else {
            g2.setColor(new Color(245, 245, 245)); // default = off-white
        }
        g2.fillRect(0, 0, getWidth(), getHeight());

        // --- Draw icon or fallback text ---
        if (iconImage != null) {
            int x = (getWidth() - iconImage.getWidth()) / 2;
            int y = (getHeight() - iconImage.getHeight()) / 2;
            g2.drawImage(iconImage, x, y, this);
        } else {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            String label = tool.name().substring(0, 1);
            g2.drawString(label,
                (getWidth() - g2.getFontMetrics().stringWidth(label)) / 2,
                getHeight() / 2 + 5);
        }

        // --- Draw border ---
        g2.setColor(new Color(200, 200, 200));
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
