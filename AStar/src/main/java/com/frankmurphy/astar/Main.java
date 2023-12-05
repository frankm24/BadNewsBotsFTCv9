package com.frankmurphy.astar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main {
    static int[][] occupancyGrid;

    public static void main(String[] args) {
        loadImage();
        AStar aStar = new AStar();
        aStar.findPathAStar(occupancyGrid);
        /*
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(new DemoPanel());

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

         */
    }

    private static void loadImage() {
        BufferedImage image;
        try {
            File imageFile = new File("occupancy_grid_binary.png"); // Replace with the path to your image file
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        occupancyGrid = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xFF;
                if (red > 0) occupancyGrid[x][y] = 1;
                else occupancyGrid[x][y] = 0;
            }
        }
        System.out.println(Arrays.toString(occupancyGrid[0]));
    }
}