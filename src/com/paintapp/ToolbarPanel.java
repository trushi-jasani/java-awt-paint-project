package com.paintapp;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ToolbarPanel extends Panel {
    private final PaintCanvas canvas;

    public ToolbarPanel(PaintCanvas canvas) {
        this.canvas = canvas;

        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        setBackground(new Color(240, 240, 240));
        setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, 70));

        // --- Tool buttons
        for (Tool t : Tool.values()) {
            Button b = new Button(t.name());
            Tool tool = t;
            b.addActionListener(e -> canvas.setTool(tool));
            add(b);
        }

        // --- Actions
        Button newBtn = new Button("New");
        newBtn.addActionListener(e -> canvas.newFile());
        add(newBtn);

        Button saveBtn = new Button("Save");
        saveBtn.addActionListener(e -> {
            FileDialog fd = new FileDialog((Frame) getParent().getParent(), "Save image", FileDialog.SAVE);
            fd.setFile("image.png");
            fd.setVisible(true);
            String dir = fd.getDirectory(), file = fd.getFile();
            if (dir != null && file != null) {
                try { canvas.saveToFile(new File(dir, file)); } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
        add(saveBtn);

        Button undoBtn = new Button("Undo");
        undoBtn.addActionListener(e -> canvas.undo());
        add(undoBtn);

        Button redoBtn = new Button("Redo");
        redoBtn.addActionListener(e -> canvas.redo());
        add(redoBtn);

        Button clearBtn = new Button("Clear");
        clearBtn.addActionListener(e -> canvas.clearCanvas());
        add(clearBtn);

        // --- Stroke size
        add(new Label("Stroke:"));
        Choice sizes = new Choice();
        List<Integer> sizeList = Arrays.asList(1, 2, 3, 5, 8, 12, 20);
        for (Integer s : sizeList) sizes.add(String.valueOf(s));
        sizes.select("3");
        sizes.addItemListener(e -> canvas.setStrokeSize(Integer.parseInt(sizes.getSelectedItem())));
        add(sizes);

        // --- Font family
        add(new Label("Font:"));
        Choice fonts = new Choice();
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (int i = 0; i < Math.min(fontNames.length, 10); i++) fonts.add(fontNames[i]);
        try { fonts.select(canvas.getFontName()); } catch (Exception ignored) {}
        fonts.addItemListener(e -> canvas.setFontName(fonts.getSelectedItem()));
        add(fonts);

        // --- Font size
        add(new Label("Size:"));
        Choice fsize = new Choice();
        for (int i = 8; i <= 48; i += 2) fsize.add(String.valueOf(i));
        fsize.select("24");
        fsize.addItemListener(e -> canvas.setFontSize(Integer.parseInt(fsize.getSelectedItem())));
        add(fsize);

        // --- Text to place
        add(new Label("Text:"));
        TextField tf = new TextField("Hello", 8); 
        tf.addTextListener(e -> canvas.setText(tf.getText()));
        add(tf);

        // --- Color swatches
        add(new Label("Colors:"));
        Panel colorGrid = new Panel(new GridLayout(2, 25, 2, 2));
        Color[] swatches = {
            Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, new Color(220,220,220), Color.WHITE,
            Color.RED, new Color(255,102,102), Color.PINK, new Color(255,0,127), new Color(153,0,0), new Color(255,204,204), new Color(178,34,34), new Color(139,0,0),
            Color.ORANGE, new Color(255,153,51), new Color(255,178,102), new Color(255,128,0), new Color(210,105,30), new Color(128,64,0), new Color(153,102,51), new Color(244,164,96),
            Color.YELLOW, new Color(255,255,153), new Color(255,255,102), new Color(204,204,0), new Color(255,255,204), new Color(218,165,32),
            new Color(0,153,0), Color.GREEN, new Color(0,255,0), new Color(102,255,178), new Color(0,128,64), new Color(50,205,50), new Color(34,139,34), new Color(144,238,144),
            Color.BLUE, new Color(102,178,255), new Color(0,204,204), new Color(70,130,180), new Color(0,0,139), new Color(135,206,250), new Color(25,25,112), new Color(173,216,230),
            Color.MAGENTA, new Color(204,0,204), new Color(148,0,211), new Color(186,85,211), new Color(221,160,221), new Color(75,0,130)
        };
        for (Color c : swatches) {
            Button cb = new Button();
            cb.setBackground(c);
            cb.setPreferredSize(new Dimension(16, 16));
            cb.setFocusable(false);
            cb.addActionListener(e -> canvas.setColor(c));
            colorGrid.add(cb);
        }
        add(colorGrid);
    }
}