package com.paintapp;

import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        // Use Frame (AWT) as the main window
        Frame frame = new Frame("Paint Application");

        PaintCanvas canvas = new PaintCanvas();
        ToolSidebar toolSidebar = new ToolSidebar(canvas);
        ToolbarPanel toolbar = new ToolbarPanel(canvas);
        
        // Link canvas to toolbar for updates (like showing the Text field)
        canvas.setToolChangeListener(toolbar); 

        frame.setLayout(new BorderLayout());
        
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(toolSidebar, BorderLayout.WEST);
        frame.add(canvas, BorderLayout.CENTER);

        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);

        // Standard closing procedure for AWT Frame
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
    }
}