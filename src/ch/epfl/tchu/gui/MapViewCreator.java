package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.effect.ColorAdjust;

class MapViewCreator { // TODO package-private --> no public
    // non-instantiable class
    private MapViewCreator() {
        throw new UnsupportedOperationException(); //TODO to this to all non-instantiable classes
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }

    private static Node createNodeFromRoute(Route r,
                                            ObjectProperty<ActionHandlers.ClaimRouteHandler> routeHandler,
                                            ObservableGameState state){
        Group routeNode = new Group();
        routeNode.setId(r.id());
        routeNode.getStyleClass()
                .addAll("route",
                        r.level().name(),
                        r.color() == null ? "NEUTRAL" : r.color().name());
        routeNode.disableProperty().bind(routeHandler.isNull().or(state.canClaimRoute(r).not()));
        state.routesOwners(r).addListener((observableValue, playerId, t1) -> {
            if (t1 != null && )
        }); //TODO
        routeNode.setOnMouseClicked(e ->
                routeHandler.get().onClaimRoute(r, state.possibleClaimCards()));
        for (int i = 1; i <= r.length(); ++i){
            // Creating all the elements of a case1
            Group case1 = new Group(); //TODO find a better name for case
            Group wagonGroup = new Group();
            Circle wagonCircle1 = new Circle(12, 6, 3);
            Circle wagonCircle2 = new Circle(24, 6, 3);
            Rectangle wagonRectangle = new Rectangle(36, 12);
            Rectangle rail = new Rectangle(36, 12);

            // applying the correct id to the case
            case1.setId(String.format("%s_%s", r.id(), i));

            // applying styling to all the elements that requires it
            rail.getStyleClass()
                    .addAll("track", "filled");
            wagonGroup.getStyleClass().add("car");
            wagonRectangle.getStyleClass().add("filled");

            // adding all the elements in the correct hierarchy
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
                    PointerInfo a = MouseInfo.getPointerInfo();
                    infoText.setX(a.getLocation().x );
                    infoText.setY(a.getLocation().y - 20); // positioning the info label
                    infoText.setStyle("-fx-background-color: white");
                    routeNode.getChildren().add(infoText); // adding the label
                } else {
                    routeNode.setEffect(null); //removing the effect on the wagon
                    routeNode.getChildren().remove(infoText); // removing the label
                }
            });}
        return routeNode;
    }

    public static Node createMapView(ObservableGameState state,
                                     ObjectProperty<ActionHandlers.ClaimRouteHandler> routeHandler,
                                     CardChooser selector){
        Pane gameMap = new Pane();
        gameMap.getStylesheets()
                .addAll("colors.css", "map.css");
        ImageView backgroundImage = new ImageView();
        gameMap.getChildren().add(backgroundImage);
        List<Node> nodes = ChMap.routes().stream().map(r -> createNodeFromRoute(r, routeHandler, state)).collect(Collectors.toList());
        gameMap.getChildren().addAll(nodes);
        return gameMap;
    }
}
