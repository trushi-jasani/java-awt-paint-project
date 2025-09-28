package com.paintapp;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ToolbarPanel extends Panel implements PaintCanvas.ToolChangeListener {
    private final PaintCanvas canvas;
    private Panel textPanel; 
    private static final Color TOOLBAR_BG = new Color(229, 228, 226); 

    public ToolbarPanel(PaintCanvas canvas) {
        this.canvas = canvas;

        setLayout(new BorderLayout());
        setBackground(TOOLBAR_BG); 
        setPreferredSize(new Dimension(Constants.CANVAS_WIDTH, 70));

        Panel controlsPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        controlsPanel.setBackground(TOOLBAR_BG); 

        // --- A. Actions Group ---
        controlsPanel.add(createButton("New", e -> canvas.newFile()));
        controlsPanel.add(createButton("Save", e -> handleSave()));
        controlsPanel.add(createButton("Undo", e -> canvas.undo()));
        controlsPanel.add(createButton("Redo", e -> canvas.redo()));
        controlsPanel.add(createButton("Clear", e -> canvas.clearCanvas()));
        
        // --- B. Grouped Controls (Stroke, Font, Size) ---
        controlsPanel.add(createLabeledChoicePanel("Stroke:", Arrays.asList(1, 2, 3, 5, 8, 12, 20), "3", size -> canvas.setStrokeSize(Integer.parseInt(size))));
        
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        List<String> fontList = Arrays.asList(Arrays.copyOf(fontNames, Math.min(fontNames.length, 10)));
        controlsPanel.add(createLabeledChoicePanel("Font:", fontList, canvas.getFontName(), canvas::setFontName));

        List<String> fSizeList = new java.util.ArrayList<>();
        for (int i = 8; i <= 48; i += 2) fSizeList.add(String.valueOf(i));
        controlsPanel.add(createLabeledChoicePanel("Size:", fSizeList, "24", size -> canvas.setFontSize(Integer.parseInt(size))));

        // --- C. Text Field Group ---
        textPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        textPanel.add(new Label("Text:"));
        TextField tf = new TextField("Hello", 8);
        tf.addTextListener(e -> canvas.setText(tf.getText()));
        textPanel.add(tf);
        controlsPanel.add(textPanel);
        textPanel.setVisible(false); // Hidden by default

        // --- D. Color Swatches ---
        Panel colorGroupPanel = new Panel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        colorGroupPanel.add(new Label("Colors:")); 
        Panel colorGrid = createColorGrid();
        colorGroupPanel.add(colorGrid);
        controlsPanel.add(colorGroupPanel);
        
        add(controlsPanel, BorderLayout.CENTER);
    }
    
    @Override
    public void toolChanged(Tool newTool) {
        // Show/hide the text panel based on the selected tool
        boolean isTextTool = (newTool == Tool.TEXT);
        
        if (textPanel.isVisible() != isTextTool) {
            textPanel.setVisible(isTextTool);
            getParent().validate();
        }
    }
    
    private Button createButton(String label, ActionListener listener) {
        Button b = new Button(label);
        //b.setBackground(new Color(229, 242, 201));
        b.setForeground(Color.BLACK);
        b.addActionListener(listener);
        return b;
    }
    
    private Panel createLabeledChoicePanel(String labelText, List<?> items, String defaultItem, java.util.function.Consumer<String> action) {
        Panel p = new Panel(new FlowLayout(FlowLayout.LEFT, 2, 0)); 
        p.add(new Label(labelText));
        Choice choice = new Choice();
        for (Object item : items) {
            choice.add(String.valueOf(item));
        }
        try { choice.select(defaultItem); } catch (Exception ignored) {}
        choice.addItemListener(e -> action.accept(choice.getSelectedItem()));
        p.add(choice);
        return p;
    }
    
    private void handleSave() {
        // Need to find the main Frame from the parent chain
        Frame mainFrame = null;
        Component current = getParent();
        while (current != null) {
            if (current instanceof Frame) {
                mainFrame = (Frame) current;
                break;
            }
            current = current.getParent();
        }

        if (mainFrame == null) return;

        FileDialog fd = new FileDialog(mainFrame, "Save image", FileDialog.SAVE);
        fd.setFile("image.png");
        fd.setVisible(true);
        String dir = fd.getDirectory(), file = fd.getFile();
        if (dir != null && file != null) {
            try { canvas.saveToFile(new File(dir, file)); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private Panel createColorGrid() {
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
        return colorGrid;
    }
}