package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class GameMenu extends Application {
    final Map<PlayerId, String> names = Map.of(PlayerId.PLAYER_1, "Alberto", PlayerId.PLAYER_2, "Emma"); //TODO debug var to remove before submission

    public static void main(String[] args){ launch(args); }

    @Override
    public void start(Stage mainMenu) {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Cpu-Easy",
                        "Cpu-Medium",
                        "Cpu-Hard");
        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setId("cpu-options");
        comboBox.getSelectionModel().select(1);

        Button localButton = new Button("Play locally");
        localButton.setId("local-button");
        localButton.setOnAction((e) -> GameLauncher.launchLocal(PlayerType.AI_EASY, names)); //TODO ai from combobox

        Button serverButton = new Button("Host a game");
        serverButton.setId("server-button");
        serverButton.setOnAction((e) -> GameLauncher.launchServer(names));

        TextField ipAndPortField = new TextField();
        ipAndPortField.setPromptText("set.your.ip.address:port");
        BooleanBinding ipIsValid = Bindings.createBooleanBinding(() -> ipAndPortField.getText().isEmpty(), ipAndPortField.textProperty());

        Button clientButton = new Button("Join a game");
        clientButton.setId("client-button");
        clientButton.disableProperty().bind(ipIsValid);
        clientButton.setOnAction((e) -> {
            GameLauncher.launchRemote(ipAndPortField.getText());
        });

        Text title = new Text("Welcome to tCHu!");
        title.setId("title");

        Group buttonGroup = new Group(localButton, serverButton, clientButton);
        Pane mainPane = new BorderPane(buttonGroup, new StackPane(title),null, new StackPane(comboBox, ipAndPortField),null);
        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add("menu.css");

        mainMenu.setScene(scene);
        mainMenu.setTitle("tCHu");
        mainMenu.setHeight(500);
        mainMenu.setWidth(500);
        mainMenu.show();
    }
}