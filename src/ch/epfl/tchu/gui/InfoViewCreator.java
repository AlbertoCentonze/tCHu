package ch.epfl.tchu.gui;
import ch.epfl.tchu.game.Player;
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

final class InfoViewCreator {
    private InfoViewCreator() {
        throw new UnsupportedOperationException();
    }

    public static Node createInfoView(ObservableGameState state, PlayerId playerId,
                               Map<PlayerId, String> names, ObservableList<Text> infos) {
        // containers
        VBox infoNode = new VBox();
        infoNode.getStylesheets().addAll("info.css", "colors.css");
        VBox playerStats = new VBox();
        playerStats.getStyleClass().add("player-stats");
        Separator separator = new Separator();

        // player stats
        List<Node> stats = List.of(playerId, playerId.next()).stream()
                .map(id -> createStatsTextFlowFromPlayer(id, names, state))
                .collect(Collectors.toList());
        playerStats.getChildren().addAll(stats);
        /*TextFlow playerStatsTextFlow = new TextFlow();
        playerStats.getChildren().add(playerStatsTextFlow);
        playerStatsTextFlow.setId(playerId.name());
        Circle playerCircle = new Circle(5, Paint.valueOf(playerId == PlayerId.PLAYER_1 ? "blue" : "pink")); // TODO set color class
        playerCircle.setId("filled");
        Text statsText = new Text();
        StringExpression stats = Bindings.format(
                StringsFr.PLAYER_STATS,
                names.get(playerId),
                state.ticketCount(playerId).get(),
                state.cardCount(playerId).get(),
                state.wagonCount(playerId).get(),
                state.constructionPoints(playerId).get());
        statsText.textProperty().bind(stats);*/

        // messages
        TextFlow messagesTextFlow = new TextFlow();
        messagesTextFlow.getStyleClass().add("game-info");
        Bindings.bindContent(messagesTextFlow.getChildren(), infos);

        infoNode.getChildren().addAll(playerStats, separator, messagesTextFlow);
        return infoNode;
    }

    private static Node createStatsTextFlowFromPlayer(PlayerId id, Map<PlayerId, String> names, ObservableGameState state){
        TextFlow statsTextFlow = new TextFlow();
        statsTextFlow.setId(id.name());
        Circle playerCircle = new Circle(5, Paint.valueOf(id == PlayerId.PLAYER_1 ? "blue" : "pink")); // TODO set color class
        playerCircle.setId("filled");
        Text statsText = new Text();
        StringExpression stats = Bindings.format(
                StringsFr.PLAYER_STATS,
                names.get(id),
                state.ticketCount(id).get(),
                state.cardCount(id).get(),
                state.wagonCount(id).get(),
                state.constructionPoints(id).get());
        statsText.textProperty().bind(stats);
        statsTextFlow.getChildren().addAll(playerCircle, statsText);
        return statsTextFlow;
    }
}
