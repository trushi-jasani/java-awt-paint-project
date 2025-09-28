package com.paintapp;

import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        Frame frame = new Frame("Paint Application");

        PaintCanvas canvas = new PaintCanvas();
        ToolbarPanel toolbar = new ToolbarPanel(canvas);

        frame.setLayout(new BorderLayout());
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(canvas, BorderLayout.CENTER);

        // Frame size matches canvas + toolbar automatically
        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
    }
}