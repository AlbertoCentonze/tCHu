package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
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

import java.util.List;
import java.util.Map;

import static javafx.application.Platform.isFxApplicationThread;
import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Emma Poggiolini (330757)
 * Graphical Interface
 */
public final class GraphicalPlayer {

    private final PlayerId playerId;
    private final ObservableGameState state;

    private final Node mapView;
    private final Node cardsView;
    private final Node handView;
    private final Node infoView;

    private static final int MAX_MESSAGES_NUMBER = 5;

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
        if(messages.size() == MAX_MESSAGES_NUMBER) { messages.remove(0); }
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

        // opening a selection window for the ticket selection
        ObservableList<Ticket> temp = observableArrayList();
        tickets.stream().forEach(temp::add);
        ListView<Ticket> ticketListView = new ListView<>(temp);
        // allow selection of multiple elements in the list
        ticketListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button buttonNode = new Button(StringsFr.CHOOSE);
        // disabling the button node as long as the player hasn't chosen at least 2
        // tickets fewer than the ones present in the list of tickets
        buttonNode.disableProperty().bind(Bindings.size(ticketListView.getSelectionModel()
                .getSelectedItems()).lessThan(ticketListView.getItems().size()
                - Constants.DISCARDABLE_TICKETS_COUNT));

        createSelectionWindow(StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS,
                numberOfTicketsToChoose, StringsFr.plural(numberOfTicketsToChoose)), ticketListView, buttonNode);

        buttonNode.setOnAction(e -> {
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
        ListView<SortedBag<Card>> cardOptionsListView = new ListView<>();
        Button button = openCardSelectionWindow(initialCards, chooseCardsHandler, StringsFr.CHOOSE_CARDS, cardOptionsListView);
        // disabling the button node as long as the player hasn't chosen an option
        button.disableProperty().bind(Bindings.size(cardOptionsListView.getSelectionModel()
                .getSelectedItems()).lessThan(1));
    }

    /**
     * Open a modal dialogue box allowing the player to select the group of additional cards
     * with which to claim a tunnel
     * @param additionalCards : list of options of cards to choose from
     * @param chooseCardsHandler : action handler for choosing cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        ListView<SortedBag<Card>> cardOptionsListView = new ListView<>();
        openCardSelectionWindow(additionalCards, chooseCardsHandler, StringsFr.CHOOSE_ADDITIONAL_CARDS, cardOptionsListView);
    }


    private Stage createGraphicalInterface(Map<PlayerId, String> names) {
        Stage interfaceNode = new Stage();
        interfaceNode.setTitle("tCHu \u2014 " + names.get(playerId));
        BorderPane borderPaneNode = new BorderPane(mapView, null, cardsView, handView, infoView);
        interfaceNode.setScene(new Scene(borderPaneNode));
        interfaceNode.show();
        return interfaceNode;
    }

    private <T> void createSelectionWindow(String title, String message, ListView<T> listView, Button buttonNode) {
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

        vBoxNode.getChildren().addAll(textFlowNode, listView, buttonNode);

        Scene sceneNode = new Scene(vBoxNode);
        sceneNode.getStylesheets().add("chooser.css");

        stageNode.setScene(sceneNode);
        stageNode.show();
    }

    private Button openCardSelectionWindow(List<SortedBag<Card>> cards, ChooseCardsHandler cardsHandler,
                                           String message, ListView<SortedBag<Card>> cardOptionsListView) {
        // changing the String format of SortedBags of cards
        cardOptionsListView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        cardOptionsListView.setItems(observableArrayList(cards));

        Button buttonNode = new Button(StringsFr.CHOOSE);

        createSelectionWindow(StringsFr.CARDS_CHOICE, message, cardOptionsListView, buttonNode);

        buttonNode.setOnAction(e -> {
            stageNode.hide();
            cardsHandler.onChooseCards(cardOptionsListView.getSelectionModel().getSelectedItem() == null ?
                    SortedBag.of() : cardOptionsListView.getSelectionModel().getSelectedItem());
        });
        return buttonNode;
    }


    /**
     * Nested class redefining the String representation of a SortedBag of cards
     */
    public static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {
        /**
         * @see StringConverter#toString(Object)
         */
        @Override
        public String toString(SortedBag<Card> object) {
            return Info.cardsInSortedBag(object);
        }

        /**
         * @see StringConverter#fromString(String)
         */
        @Override
        public SortedBag<Card> fromString(String string) { throw new UnsupportedOperationException(); }
    }
}

