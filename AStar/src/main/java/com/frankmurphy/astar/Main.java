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

public class Main {

    class Node implements Comparable<Node> {
        private final int x;
        private final int y;
        public double f, g, h;
        private List<Node> neighbors;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.f = 0;
            this.g = 0;
            this.h = 0;
        }
        public double getX() {return x;}
        public double getY() {return y;}
        public void addNeighbors(Node[][] nodeGrid) {
            int width = nodeGrid.length;
            int height = nodeGrid[0].length;
            if (x < width-1) {
                neighbors.add(nodeGrid[x+1][y]);
            }
            if (x > 0) {
                neighbors.add(nodeGrid[x-1][y]);
            }
            if (y < height-1) {
                neighbors.add(nodeGrid[x][y + 1]);
            }
            if (y > 0) {
                neighbors.add(nodeGrid[x][y-1]);
            }
        }


        @Override
        public int compareTo(Node otherNode) {
            // Allows the Arrays.sort() function to sort instances of Node
            // one of the class fields (market cap).
            // src: https://www.codejava.net/java-core/collections/sorting-arrays-examples-with-comparable-and-comparator
            double difference = this.f - otherNode.f;
            if (difference > 0) return 1;
            if (difference < 0) return -1;
            return 0;
        }
    }

    private void findPathAStar(Node startNode, Node endNode, int[][] occupancyGrid) {
        int width = occupancyGrid.length;
        int height = occupancyGrid[0].length;

        Node[][] nodeGrid = new Node[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodeGrid[x][y] = new Node(x, y);
                //nodeGrid[x][y].addNeighbors(occupancyGrid);
            }
        }

        Node start = new Node(0, 0);
        List<Node> openSet = new ArrayList<>();
        openSet.add(start);

        List<Node> closedSet = new ArrayList<>();

        while (!openSet.isEmpty()) {
            Collections.sort(openSet);
            Node lowestCostNode = openSet.get(0);
            if (lowestCostNode == endNode) {
                System.out.println("DONE!");
            }

            openSet.remove(lowestCostNode);
            closedSet.add(lowestCostNode);
        }
    }

    public static void main(String[] args) {
        BufferedImage image;
        try {
            File imageFile = new File("occupancy_grid_binary.png"); // Replace with the path to your image file
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] pixels = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = image.getRGB(x, y);
                int red = (pixel >> 16) & 0xFF;
                if (red > 0) pixels[x][y] = 1;
                else pixels[x][y] = 0;
            }
        }
        System.out.println(Arrays.toString(pixels[0]));
    }
}