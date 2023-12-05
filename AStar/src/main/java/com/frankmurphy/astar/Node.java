package com.frankmurphy.astar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class Node extends JButton implements ActionListener {
    Node parent;
    int col;
    int row;
    int gCost;
    int hCost;
    int fCost;
    boolean start;
    boolean goal;


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
