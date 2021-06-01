package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerAI;
import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GameMenu extends Application {
    final static Map<PlayerId, String> names = Map.of(PlayerId.PLAYER_1, "Alberto", PlayerId.PLAYER_2, "Emma"); //TODO debug var to remove before submission

    public static void main(String[] args){ launch(args); }

    @Override
    public void start(Stage mainMenu) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        GameManager game = new GameManager();

        Button localButton = new Button("Play locally");
        localButton.setId("local-button");
        localButton.setOnAction((e) -> localModal(mainMenu));//TODO ai from combobox

        Button hostButton = new Button("Host a game");
        hostButton.setId("server-button");
        hostButton.setOnAction((e) -> hostModal(mainMenu, game));

        Button joinButton = new Button("Join a game");
        joinButton.setId("client-button");
        joinButton.setOnAction((e) -> clientModal(mainMenu));

        Text title = new Text("Welcome to tCHu!");
        title.setId("title");

        Group buttonGroup = new Group(localButton, hostButton, joinButton);
        Pane mainPane = new BorderPane(buttonGroup, new StackPane(title),null,null,null);
        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add("menu.css");

        mainMenu.setScene(scene);
        mainMenu.setTitle("tCHu");
        mainMenu.setHeight(500);
        mainMenu.setWidth(500);
        mainMenu.setResizable(false);
        mainMenu.show();
    }

    private static void localModal(Stage parent) {
        // modal dialogue box
        Stage modal = new Stage(StageStyle.UTILITY);
        modal.initOwner(parent);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.setHeight(300);
        modal.setWidth(300);
        modal.setTitle("Customize your opponent");

        ObservableList<String> options =
                FXCollections.observableArrayList(PlayerType.AIS
                        .stream().map(PlayerType::toString)
                        .collect(Collectors.toList()));

        ComboBox<String> aiDropdown = new ComboBox<>(options);
        aiDropdown.getSelectionModel().select(PlayerType.AIS.indexOf(PlayerType.AI_MEDIUM));

        Text seedHint = new Text("Insert your seed"); //TODO make everything use the same generator or this thing doesn't have any sense
        TextField seedField = new TextField();
        seedField.setPromptText("leave it empty for a random seed");

        Button playButton = new Button("Start the game");
        playButton.setOnAction(e -> {
            int index = aiDropdown.getSelectionModel().getSelectedIndex();
            String seedText = seedField.getText();
            Integer seed = seedText.isEmpty() ? null : Integer.parseInt(seedText);
            PlayerAI ai = PlayerType.AIS.get(index).getAi(seed);
            modal.hide();
            GameManager.launchLocal(ai, names, new Random(Long.parseLong(seedText)));
        });
        VBox modalNode = new VBox(aiDropdown, seedHint, seedField, playButton);

        Scene modalScene = new Scene(modalNode);
        modalScene.getStylesheets().addAll("chooser.css", "menu.css");

        modal.setWidth(400);
        modal.setResizable(false);
        modal.setScene(modalScene);
        modal.show();
    }

    private static void clientModal(Stage parent) {
        // modal dialogue box
        Stage modal = new Stage(StageStyle.UTILITY);
        modal.initOwner(parent);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.setHeight(300);
        modal.setWidth(300);
        modal.setTitle("Connect to a remote game");

        Text ipHint = new Text("Insert the host's ip and port separated by a :"); //TODO make everything use the same generator or this thing doesn't have any sense
        TextField ipField = new TextField();
        Text ipDefaultHint = new Text("By leaving this blank localhost:5108 will be used as default"); //TODO make everything use the same generator or this thing doesn't have any sense

        Button playButton = new Button("Start the game");
        playButton.setOnAction(e -> {
            modal.hide();
            String ip = ipField.getText();
            ip = ip.isEmpty() ? "localhost:5108" : ip;
            GameManager.launchRemote(ip);
        });
        VBox modalNode = new VBox(ipHint, ipField, playButton, ipDefaultHint);

        Scene modalScene = new Scene(modalNode);
        modalScene.getStylesheets().addAll("chooser.css", "menu.css");

        modal.setWidth(400);
        modal.setResizable(false);
        modal.setScene(modalScene);
        modal.show();
    }

    private static void hostModal(Stage parent, GameManager game) {
        game.launchServer(names);

        // modal dialogue box
        Stage modal = new Stage(StageStyle.UTILITY);
        modal.initOwner(parent);
        modal.initModality(Modality.WINDOW_MODAL);
        modal.setHeight(300);
        modal.setWidth(300);
        modal.setTitle("Hosting a game");

        Text hostHint = new Text("Waiting for someone to join");
        VBox modalNode = new VBox(hostHint);

        Scene modalScene = new Scene(modalNode);
        modalScene.getStylesheets().addAll("chooser.css", "menu.css");

        modal.setWidth(400);
        modal.setOnCloseRequest((e) -> game.killServer()); //TODO fix it or remove it
        modal.setResizable(false);
        modal.setScene(modalScene);
        modal.show();
    }
}