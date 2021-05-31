package ch.epfl.tchu.map;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.gui.ActionHandlers;
import ch.epfl.tchu.gui.ObservableGameState;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ch.epfl.tchu.map.GridManager.HEIGHT;
import static ch.epfl.tchu.map.GridManager.WIDTH;

final class SeaViewCreator {
    private static final int RADIUS = 2;
    private static final int TEXT_POSITION_SHIFT = 3;

    private SeaViewCreator() { throw new UnsupportedOperationException(); }

    public static Node createSeaView(ObservableGameState state,
                                     ObjectProperty<ActionHandlers.ClaimRouteHandler> routeHandler) {
        GridManager grid = new GridManager();
        GridManager.Node[][] nodes = grid.nodes;

        Pane gameSea = new Pane();
        //gameSea.getStylesheets().addAll( "map.css", "colors.css");
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
