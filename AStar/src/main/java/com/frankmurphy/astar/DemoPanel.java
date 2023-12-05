package com.frankmurphy.astar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class DemoPanel extends JPanel {

    final int maxCol = 1080;
    final int maxRow = 1080;
    final int nodeSize = 1;
    final int screenWidth = nodeSize * maxCol;
    final int screenHeight = nodeSize * maxRow;

    public DemoPanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setLayout(new GridLayout(maxRow, maxCol));

    }
}
