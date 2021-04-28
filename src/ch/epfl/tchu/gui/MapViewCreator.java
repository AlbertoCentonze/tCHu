package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class MapViewCreator {
    private MapViewCreator() {}

    public static Node createMapView(){
        Pane gameMap = new Pane();
        gameMap.getStylesheets()
                .addAll("colors.css", "map.css");
        ImageView backgroundImage = new ImageView();
        gameMap.getChildren().add(backgroundImage);
        Group route = new Group();
        gameMap.getChildren().add(route);
        route.setId("AT1_STG_1");
        route.getStyleClass()
                .addAll("route", "UNDERGROUND",
                        "NEUTRAL");
        Group casewtf = new Group();
        casewtf.setId("AT1_STG_1_1");
        route.getChildren().add(casewtf);
        Rectangle railNotRail = new Rectangle(36, 12);
        railNotRail.getStyleClass()
                .addAll("track", "filled");
        casewtf.getChildren().add(railNotRail);
        Group wagonGroup = new Group();
        wagonGroup.getStyleClass().add("car");
        Circle wagonCircle1 = new Circle(3);
        Circle wagonCircle2 = new Circle(3);
        Rectangle wagonRectangle = new Rectangle(36, 12);
        wagonRectangle.getStyleClass().add("filled");
        wagonGroup.getChildren()
                .addAll(wagonCircle1, wagonCircle2, wagonRectangle);
        return gameMap;
    }
}
