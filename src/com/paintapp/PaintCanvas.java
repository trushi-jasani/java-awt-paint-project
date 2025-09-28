package com.paintapp;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Stack;
import javax.imageio.ImageIO;
import java.io.*;

public class PaintCanvas extends Canvas implements MouseListener, MouseMotionListener {
    private BufferedImage image;
    private Color currentColor = Color.BLACK;
    private int strokeSize = 3;
    private Tool tool = Tool.PENCIL;
    private String textToPlace = "Hello";
    private String fontName = "SansSerif";
    private int fontStyle = Font.PLAIN;
    private int fontSize = 24;
    private int startX, startY, curX, curY;
    private boolean dragging = false;
    private Stack<BufferedImage> undoStack = new Stack<>();
    private Stack<BufferedImage> redoStack = new Stack<>();

    public PaintCanvas() {
        // Note: setPreferredSize is fine for initial size, but we handle dynamic resizing now.
        setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT));
        initImage();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    private void initImage() {
        image = new BufferedImage(Constants.CANVAS_WIDTH, Constants.CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Constants.DEFAULT_BG);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        clearHistory();
    }

    // --- Dynamic Resizing Fix (Resolves the unresponsive right side) ---
    private void ensureImageMatchesCanvasSize() {
        int currentW = getWidth();
        int currentH = getHeight();
        
        // Ignore if not yet fully initialized or size is zero
        if (currentW <= 0 || currentH <= 0) return;

        // Check if the internal image dimensions are different from the canvas size
        if (image == null || image.getWidth() != currentW || image.getHeight() != currentH) {
            
            // 1. Create a new buffer with the current Canvas dimensions
            BufferedImage newImage = new BufferedImage(currentW, currentH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = newImage.createGraphics();
            
            // 2. Fill the new image with the background color
            g2.setColor(Constants.DEFAULT_BG);
            g2.fillRect(0, 0, currentW, currentH);
            
            // 3. Draw the old content onto the new image 
            // We draw the old image onto the top-left corner of the new image.
            if (image != null) {
                g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            }

            g2.dispose();
            image = newImage;
            
            // Clear history or push the new, resized state to history
            clearHistory(); // Simpler solution: clear history on resize
        }
    }


    // HISTORY
    private void pushUndo() {
        if (undoStack.size() >= Constants.UNDO_STACK_LIMIT) {
            undoStack.remove(0);
        }
        undoStack.push(ImageUtils.deepCopy(image));
        redoStack.clear();
    }

    private void clearHistory() {
        undoStack.clear();
        redoStack.clear();
        pushUndo();
    }

    public void undo() {
        if (undoStack.size() > 1) {
            redoStack.push(undoStack.pop());
            image = ImageUtils.deepCopy(undoStack.peek());
            repaint();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            image = ImageUtils.deepCopy(redoStack.pop());
            undoStack.push(ImageUtils.deepCopy(image));
            repaint();
        }
    }

    // setters
    public void setTool(Tool t) { this.tool = t; }
    public void setColor(Color c) { this.currentColor = c; }
    public void setStrokeSize(int s) { this.strokeSize = s; }
    public void setText(String t) { this.textToPlace = t; }
    public void setFontName(String fn) { this.fontName = fn; }
    public void setFontSize(int fs) { this.fontSize = fs; }
    public void setFontStyle(int style) { this.fontStyle = style; }

    public void newFile() {
        initImage();
        repaint();
    }

    public void clearCanvas() {
        pushUndo();
        Graphics2D g = image.createGraphics();
        g.setColor(Constants.DEFAULT_BG);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
        repaint();
    }

    public void saveToFile(File f) throws IOException {
        String fname = f.getName();
        if (!fname.toLowerCase().endsWith(".png")) {
            f = new File(f.getParentFile(), fname + ".png");
        }
        ImageIO.write(image, "PNG", f);
    }

    // Prevent flicker (Standard AWT double-buffering trick)
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    // --- Flickering Fix Applied Here ---
    @Override
    public void paint(Graphics g) {
        // 1. Ensure the internal image buffer size matches the canvas size
        ensureImageMatchesCanvasSize();

        // 2. Draw the saved image (This already contains the background)
        g.drawImage(image, 0, 0, this);
        
        // ⚠️ REMOVED: g.setColor(Constants.DEFAULT_BG); 
        // ⚠️ REMOVED: g.fillRect(0, 0, getWidth(), getHeight()); 
        // Removing these two lines fixes the blinking/flickering.

        // live preview while dragging shapes
        if (dragging && tool != Tool.PENCIL && tool != Tool.ERASER && tool != Tool.FILL && tool != Tool.TEXT) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setStroke(new BasicStroke(strokeSize));
            g2.setColor(currentColor);

            int x = Math.min(startX, curX);
            int y = Math.min(startY, curY);
            int w = Math.abs(curX - startX);
            int h = Math.abs(curY - startY);

            switch (tool) {
                case RECTANGLE -> g2.drawRect(x, y, w, h);
                // Note: SQUARE and CIRCLE logic needs to be careful with coordinate calculation 
                // when drawing from the middle. This is your original logic.
                case SQUARE -> { int s = Math.min(w, h); g2.drawRect(startX, startY, s, s); }
                case OVAL -> g2.drawOval(x, y, w, h);
                case CIRCLE -> { int c = Math.min(w, h); g2.drawOval(startX, startY, c, c); }
                case TRIANGLE -> {
                    int xm = (startX + curX) / 2;
                    int[] xs = {xm, startX, curX};
                    int[] ys = {startY, curY, curY};
                    g2.drawPolygon(xs, ys, 3);
                }
                default -> {}
            }
            g2.dispose();
        }
    }


    private void commitShape() {
        Graphics2D g = image.createGraphics();
        g.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(currentColor);
        int x = Math.min(startX, curX);
        int y = Math.min(startY, curY);
        int w = Math.abs(curX - startX);
        int h = Math.abs(curY - startY);
        switch (tool) {
            case RECTANGLE -> g.drawRect(x, y, w, h);
            case SQUARE -> { int s = Math.min(w, h); g.drawRect(startX, startY, s, s); }
            case OVAL -> g.drawOval(x, y, w, h);
            case CIRCLE -> { int c = Math.min(w, h); g.drawOval(startX, startY, c, c); }
            case TRIANGLE -> { int[] xs = {(startX + curX) / 2, startX, curX}; int[] ys = {startY, curY, curY}; g.drawPolygon(xs, ys, 3); }
            default -> {}
        }
        g.dispose();
    }

    public String getFontName() { return fontName; }

    private void drawLineOnImage(int x1, int y1, int x2, int y2) {
        Graphics2D g = image.createGraphics();
        g.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(tool == Tool.ERASER ? Constants.DEFAULT_BG : currentColor);
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Since the canvas is now dynamic, we clamp the coordinates to the image size
        startX = curX = Math.min(Math.max(0, e.getX()), getWidth());
        startY = curY = Math.min(Math.max(0, e.getY()), getHeight());
        
        dragging = true;

        if (tool == Tool.PENCIL || tool == Tool.ERASER) {
            pushUndo();
            drawLineOnImage(startX, startY, startX, startY);
        } else if (tool == Tool.FILL) {
            pushUndo();
            int rgb = currentColor.getRGB();
            // Bounds check is now implicitly handled by the clamping above, but a safety check remains useful
            if (startX >= 0 && startY >= 0 && startX < image.getWidth() && startY < image.getHeight())
                ImageUtils.floodFill(image, startX, startY, rgb);
            repaint();
            dragging = false;
        } else if (tool == Tool.TEXT) {
            pushUndo();
            Graphics2D g = image.createGraphics();
            g.setColor(currentColor);
            Font font = new Font(fontName, fontStyle, fontSize);
            g.setFont(font);
            g.drawString(textToPlace, startX, startY);
            g.dispose();
            repaint();
            dragging = false;
        } else {
            pushUndo();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
        // Clamp coordinates on release as well
        curX = Math.min(Math.max(0, e.getX()), getWidth());
        curY = Math.min(Math.max(0, e.getY()), getHeight());
        
        if (tool == Tool.RECTANGLE || tool == Tool.SQUARE || tool == Tool.OVAL || tool == Tool.CIRCLE || tool == Tool.TRIANGLE) {
            commitShape();
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int prevX = curX, prevY = curY;
        
        // Clamp coordinates on drag to prevent drawing outside the bounds
        curX = Math.min(Math.max(0, e.getX()), getWidth());
        curY = Math.min(Math.max(0, e.getY()), getHeight());
        
        if (tool == Tool.PENCIL || tool == Tool.ERASER) {
            drawLineOnImage(prevX, prevY, curX, curY);
        }
        repaint();
    }

    // unused
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}