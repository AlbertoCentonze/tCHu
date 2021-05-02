package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.effect.ColorAdjust;

class MapViewCreator { // TODO package-private --> no public
    // non-instantiable class
    private MapViewCreator() {}

    private static Node createNodeFromRoute(Route r){
        Group routeNode = new Group();
        routeNode.setId(r.id());
        routeNode.getStyleClass()
                .addAll("route",
                        r.level().name(),
                        r.color() == null ? "NEUTRAL" : r.color().name());
        for (int i = 1; i <= r.length(); ++i){
            Group case1 = new Group(); //TODO find a better name for case
            Group wagonGroup = new Group();
            Circle wagonCircle1 = new Circle(3);
            Circle wagonCircle2 = new Circle(3);
            Rectangle wagonRectangle = new Rectangle(36, 12);
            Rectangle rail = new Rectangle(36, 12);
            case1.setId(String.format("%s_%s", r.id(), i));
            rail.getStyleClass()
                    .addAll("track", "filled");
            wagonGroup.getStyleClass().add("car");
            wagonRectangle.getStyleClass().add("filled");
            wagonGroup.getChildren()
                    .addAll(wagonCircle1, wagonCircle2, wagonRectangle);
            case1.getChildren().addAll(wagonGroup, rail);
            routeNode.getChildren().add(case1);

            Text infoText = new Text(10, 90, (r.level().toString() == "UNDERGROUND" ?  "tunnel, " : "") + "length: " + r.length());
            routeNode.hoverProperty().addListener((obs, oldVal, newValue) -> {


                if (newValue) {
                    ColorAdjust c = new ColorAdjust(); // creating the instance of the ColorAdjust effect
                    c.setBrightness(0.2); // setting the brightness of the color wagons will assume when hovered
                    routeNode.setEffect(c); //applying the effect on the wagon

                    routeNode.getChildren().add(infoText); // adding the info label
                } else {
                    routeNode.setEffect(null); //removing the effect on the wagon
                    routeNode.getChildren().remove(infoText);
                }
            });}
        return routeNode;
    }

    public static Node createMapView(){
        Pane gameMap = new Pane();
        gameMap.getStylesheets()
                .addAll("colors.css", "map.css");
        ImageView backgroundImage = new ImageView();
        gameMap.getChildren().add(backgroundImage);
        List<Node> nodes = ChMap.routes().stream().map(MapViewCreator::createNodeFromRoute).collect(Collectors.toList());
        gameMap.getChildren().addAll(nodes);
        return gameMap;
    }
}
