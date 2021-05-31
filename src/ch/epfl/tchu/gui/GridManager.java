package ch.epfl.tchu.gui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class GridManager {
    public final static int WIDTH = 112;
    public final static int HEIGHT = 73;
    public final static int SCALE_FACTOR = 10;
    public Node[][] nodes = new Node[HEIGHT][WIDTH];


    public static void main(String[] args){
        var grid = new GridManager();
        grid.populate();
        for (int i = 0; i < HEIGHT; ++i) {
            for (int j = 0; j < WIDTH; ++j) { //TODO length
                System.out.print((grid.nodes[i][j].toString()) + " ");
            }
            System.out.print("\n");
        }
    }

    public GridManager(){
        for (int x = 0; x < WIDTH; ++x){
            for (int y = 0; y < HEIGHT; ++y){
                nodes[y][x] = new Node(x, y);
            }
        }
        populate();
    }

    private void populate(){
        for (Node[] row: nodes){
            for (Node n : row){
                if (getRandomBoolean(0.05f) && !n.taken){
                    disableNeighborhood(n, 2);
                    n.station = true;
                }
            }
        }
    }

    public boolean getRandomBoolean(float p){
        return new Random().nextFloat() < p; //TODO
    }

    private void disableNeighborhood(Node n, int radius) {
        int targetX = n.x;
        int targetY = n.y;
        List<Coords> coords = new ArrayList<>();
        for (int x = targetX - radius; x <= targetX + radius; ++x){
            for (int y = targetY - radius; y <= targetY + radius; ++y){
                coords.add(new Coords(x, y));
            }
        }
        coords = coords.stream().filter(c -> !c.isOutOfBound(HEIGHT, WIDTH)).collect(Collectors.toList());
        coords.forEach(c -> nodes[c.y][c.x].taken = true);
    }

    public List<Node> getStations() {
        List<Node> stations = new ArrayList<>();
        for (Node[] row: nodes){
            stations.addAll(Arrays.stream(row).filter(n -> n.station).collect(Collectors.toList()));
        }
        return stations;
    }

    public static class Node{
        public static final int RADIUS = 5;
        int x;
        int y;
        boolean station;
        boolean connection;
        boolean taken;

        public Node(int x, int y){
            this.x = x;
            this.y = y;
            this.station = false;
            this.connection = false;
            this.taken = false;
        }

        @Override
        public String toString() {
            return (this.taken ? "t" : "f") + (this.station ? "+" : "-");
        }

        public Pane toGraphicalNode(){
            Circle circle = new Circle(WIDTH * SCALE_FACTOR, HEIGHT * SCALE_FACTOR, RADIUS);
            Text name = new Text("Lorem Ipsum");
            return new Pane(circle, name);
        }
    }

    public static class Coords{
        int x;
        int y;

        public Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean isOutOfBound(int maxHeight, int maxWidth){
            return x < 0 || y < 0 || y >= maxHeight || x >= maxWidth;
        }
    }
}
