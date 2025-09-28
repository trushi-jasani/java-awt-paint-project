package com.paintapp;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.imageio.ImageIO;
import java.io.*;

public class PaintCanvas extends Canvas implements MouseListener, MouseMotionListener {
    
    public interface ToolChangeListener {
        void toolChanged(Tool newTool);
    }
    private ToolChangeListener toolChangeListener;

    public void setToolChangeListener(ToolChangeListener listener) {
        this.toolChangeListener = listener;
    }

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

    private void ensureImageMatchesCanvasSize() {
        int currentW = getWidth();
        int currentH = getHeight();
        
        if (currentW <= 0 || currentH <= 0) return;

        if (image == null || image.getWidth() != currentW || image.getHeight() != currentH) {
            
            BufferedImage newImage = new BufferedImage(currentW, currentH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = newImage.createGraphics();
            
            g2.setColor(Constants.DEFAULT_BG);
            g2.fillRect(0, 0, currentW, currentH);
            
            if (image != null) {
                g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            }

            g2.dispose();
            image = newImage;
            clearHistory(); 
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

    // Setters and Getters
    public void setTool(Tool t) { 
        this.tool = t; 
        if (toolChangeListener != null) { 
            toolChangeListener.toolChanged(t);
        }
    }
    public Tool getTool() { return this.tool; }
    public void setColor(Color c) { this.currentColor = c; }
    public void setStrokeSize(int s) { this.strokeSize = s; }
    public void setText(String t) { this.textToPlace = t; }
    public void setFontName(String fn) { this.fontName = fn; }
    public void setFontSize(int fs) { this.fontSize = fs; }
    public void setFontStyle(int style) { this.fontStyle = style; }
    public String getFontName() { return fontName; }


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

    // Painting logic
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        ensureImageMatchesCanvasSize();
        g.drawImage(image, 0, 0, this);

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

    private void drawLineOnImage(int x1, int y1, int x2, int y2) {
        Graphics2D g = image.createGraphics();
        g.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(tool == Tool.ERASER ? Constants.DEFAULT_BG : currentColor);
        g.drawLine(x1, y1, x2, y2);
        g.dispose();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startX = curX = Math.min(Math.max(0, e.getX()), getWidth());
        startY = curY = Math.min(Math.max(0, e.getY()), getHeight());
        
        dragging = true;

        if (tool == Tool.PENCIL || tool == Tool.ERASER) {
            pushUndo();
            drawLineOnImage(startX, startY, startX, startY);
        } else if (tool == Tool.FILL) {
            pushUndo();
            int rgb = currentColor.getRGB();
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
        
        curX = Math.min(Math.max(0, e.getX()), getWidth());
        curY = Math.min(Math.max(0, e.getY()), getHeight());
        
        if (tool == Tool.PENCIL || tool == Tool.ERASER) {
            drawLineOnImage(prevX, prevY, curX, curY);
        }
        repaint();
    }

    // unused listeners
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}