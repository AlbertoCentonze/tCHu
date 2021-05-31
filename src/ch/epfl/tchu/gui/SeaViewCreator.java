package ch.epfl.tchu.gui;

import ch.epfl.tchu.gui.ActionHandlers;
import ch.epfl.tchu.gui.GridManager;
import ch.epfl.tchu.gui.ObservableGameState;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.stream.Collectors;

import static ch.epfl.tchu.gui.GridManager.HEIGHT;
import static ch.epfl.tchu.gui.GridManager.WIDTH;

final class SeaViewCreator {
    private static final int RADIUS = 2;
    private static final int TEXT_POSITION_SHIFT = 3;

    private SeaViewCreator() { throw new UnsupportedOperationException(); }

    public static Node createSeaView(ObservableGameState state,
                                     ObjectProperty<ActionHandlers.ClaimRouteHandler> routeHandler) {
        GridManager grid = new GridManager();
        GridManager.Node[][] nodes = grid.nodes;

        Pane gameSea = new Pane();
        ImageView backgroundImage = new ImageView();
        backgroundImage.setId("sea");
        gameSea.getChildren().add(backgroundImage);

        List<Node> stations = grid.getStations().stream().map(GridManager.Node::toGraphicalNode).collect(Collectors.toList());
        gameSea.getChildren().addAll(stations);

        return gameSea;
    }
}
