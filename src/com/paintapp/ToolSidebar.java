package com.paintapp;

import java.awt.*;
import java.awt.event.*;

public class ToolSidebar extends Panel {
    
    private static final Color SIDEBAR_BG = new Color(255,255, 255); 

    public ToolSidebar(PaintCanvas canvas) {
        
        // Use FlowLayout set to vertical alignment 
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        setBackground(SIDEBAR_BG);
        
        setPreferredSize(new Dimension(50, 0)); 
        
        // Add all tool icons
        for (Tool t : Tool.values()) {
            ToolIcon icon = new ToolIcon(t, canvas);
            add(icon);
        }
    }
}