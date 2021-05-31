package ch.epfl.tchu.gui;

import ch.epfl.tchu.gui.ActionHandlers;
import ch.epfl.tchu.gui.GridManager;
import ch.epfl.tchu.gui.ObservableGameState;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

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
        gameSea.getChildren().add(backgroundImage);

        for(int i = 0; i < HEIGHT; ++i) {
            for(int j = 0; j < WIDTH; ++j) {
                if(nodes[i][j].station) {
                    Node n = new Circle(j,i, RADIUS); // TODO redefine pixels
                    n.getStyleClass().add("filled");
                    //Text name = new Text(j, i- TEXT_POSITION_SHIFT, station.name());
                    gameSea.getChildren().add(n);
                }
            }
        }
        return gameSea;
    }
}
