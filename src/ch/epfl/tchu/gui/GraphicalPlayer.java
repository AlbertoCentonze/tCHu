package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import ch.epfl.tchu.gui.MapViewCreator.CardChooser;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
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

import java.util.*;
import java.util.stream.Collectors;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Emma Poggiolini (330757)
 */

/**
 * Graphical Interface
 */
public class GraphicalPlayer {

    private final PlayerId playerId;
    private final ObservableGameState state;

    private final Node mapView;
    private final Node cardsView;
    private final Node handView;
    private final Node infoView;

    private final Stage graphicalInterface;
    private Stage stageNode;

    private final ObservableList<Text> messages = observableArrayList();

    private final ObjectProperty<DrawCardHandler> drawCardProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<DrawTicketsHandler> drawTicketsProperty = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ClaimRouteHandler> claimRouteProperty = new SimpleObjectProperty<>(null);

    /**
     * GraphicalPlayer constructor
     * creating the graphical interface
     * @param id : id of the player to whom this instance of GraphicalPlayer belongs
     * @param playerNames : map associating the players' ids to the players' names
     */
    public GraphicalPlayer(PlayerId id, Map<PlayerId,String> playerNames) {
        playerId = id;
        state = new ObservableGameState(id);
        // create the graphical interface
        mapView = MapViewCreator.createMapView(state, claimRouteProperty, this::chooseClaimCards);
        cardsView = DecksViewCreator.createCardsView(state, drawTicketsProperty, drawCardProperty);
        handView = DecksViewCreator.createHandView(state);
        infoView = InfoViewCreator.createInfoView(state, id, playerNames, messages);
        graphicalInterface = createGraphicalInterface(playerNames);
    }

    /**
     * Setting the observable gameState
     * @param gameState : the new PublicGameState
     * @param playerState : the new (complete) PlayerState
     */
    // TODO include assert in description ?
    public void setState(PublicGameState gameState, PlayerState playerState) {
        assert isFxApplicationThread();
        state.setState(gameState, playerState);
    }

    /**
     * Adds a new message to the list of 5 most recent messages viewed by the players
     * and eliminates the oldest (first) message
     * @param newMessage : new message to add at the bottom of the list
     */
    public void receiveInfo(String newMessage) {
        assert isFxApplicationThread();
        if(messages.size() == 5) { messages.remove(0); }  // TODO constant for 5 messages ?
        messages.add(new Text(newMessage));
    }

    /**
     * Allow or prevent the player from carrying out a specific action
     * by setting the properties containing the action handlers to null if the action can not be carried out
     * or by filling the properties with the respective action handlers if the action can be carried out
     * @param ticketsHandler : action handler for drawing tickets
     * @param cardHandler : action handler for drawing cards
     * @param routeHandler : action handler for attempting to claim a route
     */
    public void startTurn(DrawTicketsHandler ticketsHandler, DrawCardHandler cardHandler, ClaimRouteHandler routeHandler) {
        assert isFxApplicationThread();
        // setting the property containing the ticket handler to null when the player can't draw any tickets
        if(!state.canDrawTickets()) {
            drawTicketsProperty.set(null);
        } else {
            drawTicketsProperty.set(() -> {
                ticketsHandler.onDrawTickets();
                drawTicketsProperty.set(null);
                drawCardProperty.set(null);
                claimRouteProperty.set(null);
            });
        }
        // setting the property containing the card handler to null when the player can't draw any cards
        if(!state.canDrawCards()) {
            drawCardProperty.set(null);
        } else {
            drawCardProperty.set((slot) -> {
                cardHandler.onDrawCard(slot);
                drawTicketsProperty.set(null);
                drawCardProperty.set(null);
                claimRouteProperty.set(null);
            });
        }
        claimRouteProperty.set((r, initialCards) -> {
            routeHandler.onClaimRoute(r, initialCards);
            drawTicketsProperty.set(null);
            drawCardProperty.set(null);
            claimRouteProperty.set(null);
        });
    }

    /**
     * Open a modal dialogue box allowing the player to select the ticket(s) to keep from a list of tickets
     * @param tickets : SortedBag containing the 5 or 3 tickets from which the player selects the ones to keep
     * @param chooseTicketsHandler : action handler for choosing tickets
     */
    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        int numberOfTicketsToChooseFrom = tickets.size() == Constants.INITIAL_TICKETS_COUNT ?
                Constants.INITIAL_TICKETS_COUNT : Constants.IN_GAME_TICKETS_COUNT;
        int numberOfTicketsToChoose = numberOfTicketsToChooseFrom - Constants.DISCARDABLE_TICKETS_COUNT;

        // opening a selection window for the ticket selection    // TODO ALL THIS HIGHLY UNNECESSARY
        ObservableList<Ticket> temp = observableArrayList();
        tickets.stream().forEach(temp::add);
        ListView<Ticket> ticketListView = new ListView<>(temp);

        Button b = createSelectionWindow(StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS,
                numberOfTicketsToChoose, StringsFr.plural(numberOfTicketsToChoose)), ticketListView);

        b.setOnAction(e -> {
            stageNode.hide();
            chooseTicketsHandler.onChooseTickets(SortedBag.of(ticketListView.getSelectionModel().getSelectedItems()));
        });
    }

    /**
     * Allow the player to draw a card from the faceUpCards of from the deck of cards
     * called when the player has already drawn a first card and has to draw a second one
     * @param drawCardHandler : action handler for drawing a card
     */
    public void drawCard(DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        drawCardProperty.set((chosenSlot) -> {
            drawCardHandler.onDrawCard(chosenSlot);
            drawTicketsProperty.set(null);
            drawCardProperty.set(null);
            claimRouteProperty.set(null);
        });
    }

    /**
     * Open a modal dialogue box allowing the player to select the group of cards
     * with which to attempt to claim a route
     * @param initialCards : list of options of cards to choose from
     * @param chooseCardsHandler : action handler for choosing cards
     */
    public void chooseClaimCards(List<SortedBag<Card>> initialCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        // opening a selection window for the cards-to-claim selection
        ListView<SortedBag<Card>> cardOptionsListView = new ListView<>();

        // changing the String format of SortedBags of cards
        cardOptionsListView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        cardOptionsListView.setItems(observableArrayList(initialCards));

        Button b = createSelectionWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, cardOptionsListView);

        b.setOnAction(e -> {
            stageNode.hide();
            chooseCardsHandler.onChooseCards(cardOptionsListView.getSelectionModel().getSelectedItem());
        });
    }

    /**
     * Open a modal dialogue box allowing the player to select the group of additional cards
     * with which to claim a tunnel
     * @param additionalCards : list of options of cards to choose from
     * @param chooseCardsHandler : action handler for choosing cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        // opening a selection window for the additional cards' selection
        ListView<SortedBag<Card>> cardOptionsListView = new ListView<>();

        // changing the String format of SortedBags of cards
        cardOptionsListView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        cardOptionsListView.setItems(observableArrayList(additionalCards));  // TODO modularize lines 170-175 --> new private method

        Button b = createSelectionWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, cardOptionsListView);

        b.setOnAction(e -> {
            stageNode.hide();
            chooseCardsHandler.onChooseCards(cardOptionsListView.getSelectionModel().getSelectedItem());
        });
    }


    private Stage createGraphicalInterface(Map<PlayerId, String> names) {
        Stage interfaceNode = new Stage();
        interfaceNode.setTitle("tCHu \u2014 " + names.get(playerId));
        BorderPane borderPaneNode = new BorderPane(mapView, null, cardsView, handView, infoView);
        interfaceNode.setScene(new Scene(borderPaneNode));
        interfaceNode.show();
        return interfaceNode;
    }

    private <T> Button createSelectionWindow(String title, String message, ListView<T> listView) {
        // modal dialogue box
        stageNode = new Stage(StageStyle.UTILITY);
        stageNode.initOwner(graphicalInterface);
        stageNode.initModality(Modality.WINDOW_MODAL);

        stageNode.setTitle(title);
        // prevent the player from closing the selection window
        stageNode.setOnCloseRequest(Event::consume);

        VBox vBoxNode = new VBox();
        // creating the node for the text
        TextFlow textFlowNode = new TextFlow();

        Text textNode = new Text(message);
        textFlowNode.getChildren().add(textNode);

        Button buttonNode = new Button(StringsFr.CHOOSE);
        // when the player is choosing tickets
        if (title.equals(StringsFr.TICKETS_CHOICE)) {  // listView.getClass().equals(Ticket.class)
            // allow selection of multiple elements in the list
            listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            // disabling the button node as long as the player hasn't chosen at least 2
            // tickets fewer than the ones present in the list of tickets
            buttonNode.disableProperty().bind(Bindings.size(listView.getSelectionModel()
                    .getSelectedItems()).lessThan(listView.getItems().size()
                    - Constants.DISCARDABLE_TICKETS_COUNT));
        }
        // when choosing the initial cards with which to claim a route
        if (message.equals(StringsFr.CHOOSE_CARDS)) {
            // disabling the button node as long as the player hasn't chosen an option
            buttonNode.disableProperty().bind(Bindings.size(listView.getSelectionModel()
                    .getSelectedItems()).lessThan(1));
        }

        vBoxNode.getChildren().addAll(textFlowNode, listView, buttonNode);

        Scene sceneNode = new Scene(vBoxNode);
        sceneNode.getStylesheets().add("chooser.css");

        stageNode.setScene(sceneNode);
        stageNode.show();

        return buttonNode;
    }


    /**
     * Nested class redefining the String representation of a SortedBag of cards
     */
    public static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
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

