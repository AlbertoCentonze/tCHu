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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.effect.ColorAdjust;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


final class MapViewCreator {
    // non-instantiable class
    private MapViewCreator() { throw new UnsupportedOperationException(); }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }

    private static Node createNodeFromRoute(Route r,
                                            ObjectProperty<ActionHandlers.ClaimRouteHandler> routeHandler,
                                            ObservableGameState state,
                                            CardChooser cardChooser){
        Group routeNode = new Group();
        routeNode.setId(r.id());
        routeNode.getStyleClass()
                .addAll("route",
                        r.level().name(),
                        r.color() == null ? "NEUTRAL" : r.color().name());

        // adding a listener to the property containing the owner of the route
        // when the owner changes (isn't null anymore),
        // the style class corresponding to the owner is added to the route
        state.routesOwners(r).addListener((ownerProperty, oldOwner, newOwner) ->
            routeNode.getStyleClass().add(newOwner.name()));

        for (int i = 1; i <= r.length(); ++i) {
            // Creating all the elements of a case1
            Group case1 = new Group(); //TODO find a better name for case
            Group wagonGroup = new Group();
            Circle wagonCircle1 = new Circle(12, 6, 3); // TODO circles not correctly shown on claim
            Circle wagonCircle2 = new Circle(24, 6, 3);
            Rectangle wagonRectangle = new Rectangle(36, 12);
            Rectangle rail = new Rectangle(36, 12);

            // applying the correct id to the case
            case1.setId(String.format("%s_%s", r.id(), i));

            // applying styling to all the elements that require it
            rail.getStyleClass()
                    .addAll("track", "filled");
            wagonGroup.getStyleClass().add("car");
            wagonRectangle.getStyleClass().add("filled");

            // adding all the elements in the correct hierarchy
            wagonGroup.getChildren()
                    .addAll(wagonRectangle, wagonCircle1, wagonCircle2);
            case1.getChildren().addAll(wagonGroup, rail);
            routeNode.getChildren().add(case1);

            // additional stuff
            Group infoLabel = new Group();
            Rectangle labelBackground = new Rectangle(60, 30);
            labelBackground.setArcWidth(30.0);
            labelBackground.setArcHeight(30.0);
            Text labelText = new Text(10, 10, (r.claimPoints() == 1 ? r.claimPoints() + " point" : r.claimPoints() + " points"));
            labelText.setFill(Paint.valueOf("#FFFFFF"));
            infoLabel.getChildren()
                    .addAll(labelBackground, labelText);

            routeNode.hoverProperty().addListener((obs, oldVal, newValue) -> {
                if (newValue) {
                    ColorAdjust c = new ColorAdjust(); // creating the instance of the ColorAdjust effect
                    c.setBrightness(0.2); // setting the brightness of the color wagons will assume when hovered
                    routeNode.setEffect(c); //applying the effect on the wagon
                    PointerInfo a = MouseInfo.getPointerInfo();
                    labelText.setX(a.getLocation().x + 10);
                    labelText.setY(a.getLocation().y);
                    labelBackground.setX(a.getLocation().x );
                    labelBackground.setY(a.getLocation().y - 20);
                    labelText.setStyle("-fx-background-color: white");
                    routeNode.getChildren().add(infoLabel); // adding the label
                    routeNode.toFront();
                } else {
                    routeNode.setEffect(null); //removing the effect on the wagon
                    routeNode.getChildren().remove(infoLabel); // removing the label
                }
            });}

        // disabling the route's node when the player can't claim the route or the routeHandler is null
        routeNode.disableProperty().bind(routeHandler.isNull().or(state.canClaimRoute(r).not()));

        // attempting to claim the route r when the player clicks on the route
        routeNode.setOnMouseClicked(e -> {
            // possible claim cards for route r
            List<SortedBag<Card>> options = state.possibleClaimCards(r);
            // if the player has multiple options
            if (options.size() > 1) {
                // cardsHandler calls onClaimRoute() of the routeHandler, passing the chosen cards as arguments
                ActionHandlers.ChooseCardsHandler cardsHandler = chosenCards -> routeHandler.get().onClaimRoute(r, chosenCards);
                // calls onChooseCards() of cardsHandler
                cardChooser.chooseCards(options, cardsHandler);
            } else { // only one option
                routeHandler.get().onClaimRoute(r, options.get(0));
            }
        });
        return routeNode;
    }

    public static Node createMapView(ObservableGameState state,
                                     ObjectProperty<ActionHandlers.ClaimRouteHandler> routeHandler,
                                     CardChooser cardChooser){
        Pane gameMap = new Pane();
        gameMap.getStylesheets()
                .addAll( "map.css", "colors.css");
        ImageView backgroundImage = new ImageView();
        gameMap.getChildren().add(backgroundImage);
        List<Node> nodes = ChMap.routes().stream().map(r -> createNodeFromRoute(r, routeHandler, state, cardChooser)).collect(Collectors.toList());
        gameMap.getChildren().addAll(nodes);
        return gameMap;
    }
}
