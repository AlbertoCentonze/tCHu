package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alberto Centonze (327267)
 * Creates the left side of the graphical interface
 */
final class InfoViewCreator {
    private static final int CIRCLE_RADIUS = 5;

    private InfoViewCreator() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates the left side of the graphical interface
     * @param state the observable game state
     * @param playerId the id of the current player
     * @param names the names of all the players
     * @param infos the infos to be displayed
     * @return the node for the left side of screen
     */
    public static Node createInfoView(ObservableGameState state, PlayerId playerId,
                               Map<PlayerId, String> names, ObservableList<Text> infos) {
        // containers
        VBox infoNode = new VBox();
        infoNode.getStylesheets().addAll("info.css", "colors.css");
        VBox playerStats = new VBox();
        playerStats.setId("player-stats");
        Separator separator = new Separator();

        // player stats
        List<Node> stats = List.of(playerId, playerId.next()).stream()
                .map(id -> createStatsTextFlowFromPlayer(id, names, state))
                .collect(Collectors.toList());
        playerStats.getChildren().addAll(stats);

        // messages
        TextFlow messagesTextFlow = new TextFlow();
        messagesTextFlow.setId("game-info");
        Bindings.bindContent(messagesTextFlow.getChildren(), infos);

        infoNode.getChildren().addAll(playerStats, separator, messagesTextFlow);
        return infoNode;
    }

    private static Node createStatsTextFlowFromPlayer(PlayerId id, Map<PlayerId, String> names, ObservableGameState state){
        TextFlow statsTextFlow = new TextFlow();
        statsTextFlow.getStyleClass().add(id.name());

        // creates a circle according to the color of the player
        Circle playerCircle = new Circle(CIRCLE_RADIUS);
        playerCircle.getStyleClass().addAll(id.name(), "filled");

        // Creating the stats for each player
        Text statsText = new Text();
        StringExpression stats = Bindings.format(
                StringsFr.PLAYER_STATS,
                names.get(id),
                state.ticketCount(id),
                state.cardCount(id),
                state.wagonCount(id),
                state.constructionPoints(id));
        statsText.textProperty().bind(stats);

        statsTextFlow.getChildren().addAll(playerCircle, statsText);
        return statsTextFlow;
    }
}
