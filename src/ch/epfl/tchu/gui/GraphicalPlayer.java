package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

public class GraphicalPlayer {

    PlayerId playerId;
    ObservableGameState state;

    Node mapView;
    Node cardsView;
    Node handView;
    Node infoView;

    Stage graphicalInterface;

    ObjectProperty<DrawCardHandler> drawCardProperty = new SimpleObjectProperty<>(null);
    ObjectProperty<DrawTicketsHandler> drawTicketsProperty = new SimpleObjectProperty<>(null);
    ObjectProperty<ClaimRouteHandler> claimRouteProperty = new SimpleObjectProperty<>(null);

    ChooseTicketsHandler chooseTicketsHandler;

    public GraphicalPlayer(PlayerId id, Map<PlayerId,String> playerNames) {
        playerId = id;
        state = new ObservableGameState(id);
        // create the graphical interface
        Node mapView = MapViewCreator.createMapView(state, new SimpleObjectProperty<ClaimRouteHandler>(), chooseClaimCards());// TODO
        Node cardsView = DecksViewCreator.createCardsView(state, drawTicketsProperty, drawCardProperty);
        Node handView = DecksViewCreator.createHandView(state);
        Node infoView = InfoViewCreator.createInfoView(state, id, playerNames, null);
        graphicalInterface = createGraphicalInterface();

        // TODO
    }

    /**
     * Setting the observable gameState
     * @param gameState : the new PublicGameState
     * @param playerState : the new (complete) PlayerState
     */
    public void setState(PublicGameState gameState, PlayerState playerState) {
        state.setState(gameState, playerState);
    }

    public void receiveInfo(String newMessage) {
        infoView.getAccessibleText(). // TODO
    }

    public void startTurn(DrawTicketsHandler ticketsHandler, DrawCardHandler cardHandler, ClaimRouteHandler routeHandler) {
        // setting the property containing the ticket handler to null when the player can't draw tickets
        if(!state.canDrawTickets()) {
            drawTicketsProperty.set(null);
        } else {
            drawTicketsProperty.set(ticketsHandler);
        }
        // setting the property containing the card handler to null when the player can't draw card
        if(!state.canDrawCards()) {
            drawCardProperty.set(null);
        } else {
            drawCardProperty.set(cardHandler);
        }

        claimRouteProperty.set(routeHandler);
        // TODO vider les proprietes
    }

    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        this.chooseTicketsHandler = chooseTicketsHandler;
        // checking the size of the bag of tickets is either 5 or 3
        Preconditions.checkArgument(tickets.size() == Constants.INITIAL_TICKETS_COUNT ||
                tickets.size() == Constants.IN_GAME_TICKETS_COUNT); // TODO allowed ?
        int numberOfTicketsToChooseFrom = tickets.size() == Constants.INITIAL_TICKETS_COUNT ? Constants.INITIAL_TICKETS_COUNT :
                Constants.IN_GAME_TICKETS_COUNT;
        int numberOfTicketsToChoose = numberOfTicketsToChooseFrom - Constants.DISCARDABLE_TICKETS_COUNT;
        // opening a selection window for the ticket selection
        createSelectionWindow(StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS,
                numberOfTicketsToChoose, StringsFr.plural(numberOfTicketsToChoose)), observableArrayList(tickets)); // TODO
        chooseTicketsHandler.onChooseTickets(chosen);
    }

    public void drawCard(DrawCardHandler drawCardHandler) {
        // TODO disable ticket node and route nodes and enable card nodes
        drawCardProperty.set(drawCardHandler); // TODO vider les properties
        drawCardHandler.onDrawCard(chosenSlot);
    }

    public void chooseClaimCards(SortedBag<Card> initialCards, ChooseCardsHandler chooseCardsHandler) {
        // opening a selection window for the cards-to-claim selection
        createSelectionWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, observableArrayList(initialCards)); // TODO
        chooseCardsHandler.onChooseCards(chosen);
    }

    public void chooseAdditionalCards(SortedBag<Card> additionalCards, ChooseCardsHandler chooseCardsHandler) {
        // opening a selection window for the additional cards' selection
        createSelectionWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS,
                observableArrayList(additionalCards)); // TODO
        chooseCardsHandler.onChooseCards(chosen);
    }



    private Stage createGraphicalInterface() { // TODO return the Node ?
        Stage interfaceNode = new Stage();
        interfaceNode.setTitle("tCHu \u2014 " + playerId.name());
        BorderPane borderPaneNode = new BorderPane(mapView, null, cardsView, handView, infoView);
        interfaceNode.setScene(new Scene(borderPaneNode));
        return interfaceNode;
    }

    private void createSelectionWindow(String title, String message, ObservableList<Object> list) {
        // modal dialogue box
        Stage stageNode = new Stage(StageStyle.UTILITY);
        stageNode.initOwner(graphicalInterface);
        stageNode.initModality(Modality.WINDOW_MODAL);

        stageNode.setTitle(title);
        // prevent the player from closing the selection window
        stageNode.setOnCloseRequest(Event::consume);

        VBox vBoxNode = new VBox();
        // creating the node for the text
        TextFlow textFlowNode = new TextFlow();
        Text textNode = new Text(message); // TODO
        textFlowNode.getChildren().add(textNode);

        ListView<Object> listViewNode = new ListView<>(list);
        Button buttonNode = new Button(StringsFr.CHOOSE);
        // when the player is choosing tickets
        if(message.equals(StringsFr.CHOOSE_TICKETS)) {
            // allow selection of multiple elements in the list
            listViewNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            // disabling the button node as long as the player hasn't chosen at least 2
            // tickets fewer than the ones present in the list of tickets
            buttonNode.disableProperty().bind(new SimpleBooleanProperty(listViewNode
                    .getSelectionModel().getSelectedItems().size()
                    < (list.size() - Constants.DISCARDABLE_TICKETS_COUNT))); // TODO ???
            buttonNode.setOnAction(e -> {
                stageNode.hide();
                switch (message) {                      // TODO switch-case here or outside ??
                    case StringsFr.CHOOSE_TICKETS:
                        chooseTicketsHandler.onChooseTickets(SortedBag.of(new ArrayList<Object>(listViewNode.getSelectionModel().getSelectedItems())));
                        // TODO
                }
            });
        }
        // when choosing the initial cards with which to claim a route
        if(message.equals(StringsFr.CHOOSE_CARDS)) {
            // changing the String format of SortedBags of cards
            listViewNode.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter())); // TODO why ??
            // disabling the button node as long as the player hasn't chosen an option
            buttonNode.disableProperty().bind(new SimpleBooleanProperty(listViewNode
                    .getSelectionModel().getSelectedItem() == null)); // TODO ?
        }

        vBoxNode.getChildren().addAll(textFlowNode, listViewNode, buttonNode);

        Scene sceneNode = new Scene(vBoxNode);
        sceneNode.getStylesheets().add("chooser.css");

        stageNode.setScene(sceneNode);
    }

    public static class CardBagStringConverter extends StringConverter<SortedBag<Card>> { // TODO test
        @Override
        public String toString(SortedBag<Card> object) {
            return Info.cardsInSortedBag(object);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}
