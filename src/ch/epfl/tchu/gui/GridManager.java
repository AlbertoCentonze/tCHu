package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;
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
    public List<Route> stations = new ArrayList<>();


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
        connect();
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

    private void connect(){
        getStations().forEach(startingStations -> {
            List<Station> stations = startingStations.findNearestNeighbors(2).stream().map(Node::toStation).collect(Collectors.toList());
            List<Route> routes = stations.stream().map(s -> new Route("", startingStations.toStation(), s, 4, null, null)).collect(Collectors.toList());
        });
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

    public class Node{
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

        private double distanceFrom(Node node){
            return Math.sqrt((node.x - x)^2 + (node.y - y)^2);
        }

        public int amountOfRailsFrom(Node node){
            return (int) Math.ceil(distanceFrom(node) * SCALE_FACTOR);
        }

        private List<Node> findNearestNeighbors(int neighborsAmount){
            List<Node> neighbors = new ArrayList<>();
            List<Node> stations = new ArrayList<>(getStations());
            for (int i = 0; i < neighborsAmount; ++i){
                List<Double> distanceList = stations.stream().map(n -> n.distanceFrom(this)).collect(Collectors.toList());
                double minDistance = distanceList.stream().mapToDouble(n->n).min().orElseThrow();
                int minIndex = distanceList.indexOf(minDistance);
                Node neighbor = stations.get(minIndex);
                stations.remove(minIndex);
                neighbors.add(neighbor);
            }
            assert neighbors.size() == 2;
            return neighbors;
        }

        public Station toStation(){
            return new Station(0, getRandomName());
        }

        public Pane connectWith(){
            return null;
        }
    }

    private static String getRandomName() {
        return "Laputa";
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
