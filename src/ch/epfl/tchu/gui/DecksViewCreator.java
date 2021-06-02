package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * @author Emma Poggiolini (330757)
 * Creator of the Hand View and the Cards' View
 */
final class DecksViewCreator {
    private static final int CARD_WIDTH_IMAGE = 100;
    private static final int CARD_HEIGHT_IMAGE = 64;

    private static final int SHOW_NUMBER_THRESHOLD = 1;

    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 5;
    private static final int GAUGE_COEFFICIENT = 100;

    // package-private and non-instantiable class
    private DecksViewCreator() { throw new UnsupportedOperationException(); }

    /**
     * Crete the hand view displaying the player's tickets and cards
     * @param state : observable instance of game state
     * @return (HBox) hand view
     */
     public static HBox createHandView(ObservableGameState state) {
         HBox handViewNode = new HBox();
         handViewNode.getStylesheets().addAll("decks.css", "colors.css");

         // creating the node that shows the tickets
         ListView<Ticket> ticketNode = new ListView<>(state.tickets());
         ticketNode.setId("tickets");

         // points assigned by the tickets
         TextFlow ticketPoints = new TextFlow();
         ObservableList<Text> points = observableArrayList(new Text("   Les billets t'apportent : \n   "
                 + currentPoints(state) + " points au total \n"));
         Bindings.bindContent(ticketPoints.getChildren(), points); // TODO not updating

         HBox childHandViewNode = new HBox();
         childHandViewNode.setId("hand-pane");

         List<Node> nodes = Card.ALL.stream().map(c -> createNodeFromCard(c, state)).collect(Collectors.toList());
         childHandViewNode.getChildren().addAll(nodes);

         BorderPane pane = new BorderPane(null, ticketPoints,childHandViewNode, null, ticketNode);
         handViewNode.getChildren().add(pane);

         return handViewNode;
     }

     private static int currentPoints(ObservableGameState state) {
         return state.tickets().stream().mapToInt(t -> t.points(state.getPlayerState().connections())).sum();
     }

     private static Node createNodeFromCard(Card card, ObservableGameState state) {
         StackPane cardNode = new StackPane();
         String color = "";
         cardNode.getStyleClass().addAll(card == null ? color : card.toCssClass(), "card");

         // card
         Rectangle imageNode = new Rectangle(CARD_WIDTH_IMAGE, CARD_HEIGHT_IMAGE);
         imageNode.getStyleClass().add("train-image");

         cardNode.getChildren().addAll(imageNode);

         if(card != null) {
             // creating the node of the text
             Text countNode = new Text();
             countNode.getStyleClass().add("count");
             // showing the card only if the player owns at least one of this type
             ReadOnlyIntegerProperty count = state.numberOfEachCard(card);
             cardNode.visibleProperty().bind(Bindings.greaterThan(count, 0));
             // displaying the number of cards of this type if count > 1
             countNode.textProperty().bind(Bindings.convert(count));
             countNode.visibleProperty().bind(Bindings.greaterThan(count, SHOW_NUMBER_THRESHOLD));
             cardNode.getChildren().add(countNode);
         }
         return cardNode;
     }


    /**
     * Create the cards' view displaying the deck of tickets, the 5 faceUpCards and the deck of cards
     * @param state : observable instance of game state
     * @param ticketsHandler : property containing the action handler for drawing tickets
     * @param cardsHandler : property containing the action handler for drawing cards
     * @return (VBox) cards' view
     */
     public static VBox createCardsView(ObservableGameState state, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler,
                                        ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler) {
         VBox cardsViewNode = new VBox();
         cardsViewNode.setId("card-pane");
         cardsViewNode.getStylesheets().addAll("decks.css", "colors.css");

         // creating the node for the deck of tickets
         Button ticketDeckNode = createButtonNode(StringsFr.TICKETS, state.ticketPercentage());
         // calling onDrawTickets of the ticket handler when the player presses on the tickets' button
         ticketDeckNode.setOnMouseClicked((e) -> {
             ticketsHandler.get().onDrawTickets();
             Audio.play("bip.wav");
         });
         disableAndAdd(cardsViewNode, ticketDeckNode, ticketsHandler);

         // creating the nodes of the faceUpCards
         for(int slot : Constants.FACE_UP_CARD_SLOTS) {
             Node cardNode = createNodeFromCard(null, state);
             cardNode.setId("drawable-card");
             // attaching a listener to every cardNode to modify its style class
             state.faceUpCard(slot).addListener((p, o, n) -> cardNode.getStyleClass().set(0, n.toCssClass()));
             // calling onDrawCards of the card handler when the player presses on a faceUpCard
             cardNode.setOnMouseClicked((e) -> {
                 cardsHandler.get().onDrawCard(slot);
                 Audio.play("bip.wav");
             });
             disableAndAdd(cardsViewNode, cardNode, cardsHandler);
         }

         // creating the node for the deck of cards
         Button cardDeckNode = createButtonNode(StringsFr.CARDS, state.cardPercentage());
         // calling onDrawCards of the card handler when the player presses on the deck of cards
         cardDeckNode.setOnMouseClicked((e) -> {
             cardsHandler.get().onDrawCard(Constants.DECK_SLOT);
             Audio.play("bip.wav");
         });
         disableAndAdd(cardsViewNode, cardDeckNode, cardsHandler);

         return cardsViewNode;
     }

     private static Button createButtonNode(String name, ReadOnlyIntegerProperty pctProperty) {
         Button deckNode = new Button("");
         deckNode.getStyleClass().add("gauge");

         Group groupNode = new Group();

         // node representing the gauge
         // background node
         Rectangle backgroundNode = new Rectangle(CARD_WIDTH_IMAGE, CARD_HEIGHT_IMAGE);
         if (name.equals("Billets")) {
                 backgroundNode.getStyleClass().add("cards-deck");
         } else {
                 backgroundNode.getStyleClass().add("ticket-deck");
         }

         // foreground node
         Rectangle foregroundNode = new Rectangle(CARD_WIDTH_IMAGE, BUTTON_HEIGHT);
         foregroundNode.getStyleClass().add("foreground");
         // changing the percentage displayed on the gauge
         foregroundNode.widthProperty().bind(pctProperty.multiply(GAUGE_COEFFICIENT).divide(100));

         groupNode.getChildren().addAll(backgroundNode, foregroundNode);
         deckNode.setGraphic(groupNode);

         return deckNode;
     }

     private static <T> void disableAndAdd(VBox cardsViewNode, Node node, ObjectProperty<T> handler) {
         node.disableProperty().bind(handler.isNull());
         cardsViewNode.getChildren().add(node);
     }

}
