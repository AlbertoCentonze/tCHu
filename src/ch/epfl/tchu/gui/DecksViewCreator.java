package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PublicGameState;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.List;

class DecksViewCreator { // TODO package-private --> no public
    // non-instantiable class
    private DecksViewCreator() {}

    // TODO static
     public static HBox createHandView(ObservableGameState state) { // TODO return la vue de main
        HBox handViewNode = new HBox();
        handViewNode.getStylesheets().addAll("decks.css", "colors.css");

        // creating the node that shows the tickets
        Control ticketNode = new ListView<>(state.tickets()); // TODO types
        ticketNode.setId("tickets");
        handViewNode.getChildren().add(ticketNode);

        HBox childHandViewNode = new HBox();
        childHandViewNode.setId("hand-pane");

        // creating the nodes of the cards
        for(Card card : Card.ALL) {
            childHandViewNode.getChildren().add(createNodeFromCard(card, state));

            // only create the view of the cards owned by the player
            //if(state.numberOfEachCard(card).get() != 0) {
            //    handViewNode.getChildren().add(createNodeFromCard(card, state.numberOfEachCard(card).get()));
            //}
        }
        handViewNode.getChildren().add(childHandViewNode);
        return handViewNode; // TODO change
     }

     private static Node createNodeFromCard(Card card, ObservableGameState state) { // TODO count...
         StackPane cardNode = new StackPane();
         String color = null;
         if(card != null) {
             color = (card == Card.LOCOMOTIVE) ? "NEUTRAL" : card.name();
         }
         cardNode.getStyleClass().addAll(color, "card"); // TODO .name() for enum ?



         // card                                       // TODO null
         // outside of the card (rounded frame)
         Rectangle outsideNode = new Rectangle(60, 90);
         outsideNode.getStyleClass().add("outside");
         // inside of the card (colored)
         Rectangle insideNode = new Rectangle(40, 70);
         insideNode.getStyleClass().addAll("filled", "inside");
         // image on the card (wagon or locomotive)
         Rectangle imageNode = new Rectangle(40, 70);
         imageNode.getStyleClass().add("train-image");

         cardNode.getChildren().addAll(outsideNode, insideNode, imageNode);

         // TODO
         // creating the node of the text
         Text countNode = new Text();
         countNode.getStyleClass().add("count");

         if(card != null) {
             // showing the card only if the player owns at least one of this type
             ReadOnlyIntegerProperty count = state.numberOfEachCard(card); // TODO does this even for faceUpCards
             cardNode.visibleProperty().bind(Bindings.greaterThan(count, 0)); // TODO -1
             // displaying the number of cards of this type if count > 1
             countNode.textProperty().bind(Bindings.convert(count));
             countNode.visibleProperty().bind(Bindings.greaterThan(count, 1));
         }
         cardNode.getChildren().add(countNode);

         return cardNode;
     }


     public static Pane createCardsView(ObservableGameState state, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler,
                                        ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler) { // TODO Pane
        // TODO return la vue des cartes
         VBox cardsViewNode = new VBox();
         cardsViewNode.setId("card-pane");
         cardsViewNode.getStylesheets().addAll("decks.css", "colors.css");

         // creating the node for the deck of tickets
         Node ticketDeckNode = createButtonNode("tickets", state);
         // disabling the button for the deck of tickets when the player can't draw any tickets
         ticketDeckNode.disableProperty().bind(ticketsHandler.isNull());
         // calling onDrawTickets of the ticket handler when the player presses on the tickets' button
         ticketDeckNode.setOnMouseClicked((e) -> ticketsHandler.get().onDrawTickets());
         cardsViewNode.getChildren().add(ticketDeckNode);
         // TODO disableProperty here ?

         // creating the nodes of the faceUpCards
         for(int slot : Constants.FACE_UP_CARD_SLOTS) {
             Node cardNode = createNodeFromCard(state.faceUpCard(slot).get(), state);
             // attaching a listener to every cardNode to modify its style class
             state.faceUpCard(slot).addListener((p, o, n) -> cardNode.getStyleClass().set(0, n.name()));
             // disabling the node of the faceUpCard when the player can't draw a faceUpCard
             cardNode.disableProperty().bind(cardsHandler.isNull());
             // calling onDrawCards of the card handler when the player presses on a faceUpCard
             cardNode.setOnMouseClicked((e) -> cardsHandler.get().onDrawCard(slot));
             cardsViewNode.getChildren().add(cardNode);
         }

         // creating the node for the deck of cards
         Node cardDeckNode = createButtonNode("cards", state);
         // disabling the button for the deck of cards when the player can't draw any cards
         cardDeckNode.disableProperty().bind(ticketsHandler.isNull());
         // calling onDrawCards of the card handler when the player presses on the deck of cards
         cardDeckNode.setOnMouseClicked((e) -> cardsHandler.get().onDrawCard(Constants.DECK_SLOT));
         cardsViewNode.getChildren().add(cardDeckNode);

         return cardsViewNode;
     }

     // TODO names for buttons ?? not said in the explanation
     private static Node createButtonNode(String type, ObservableGameState state) { // TODO better way...
         Button deckNode = new Button();
         deckNode.getStyleClass().add("gauged");

         Group groupNode = new Group();

         // node representing the gauge
         // background node
         Rectangle backgroundNode = new Rectangle(50, 5);
         backgroundNode.getStyleClass().add("background");
         // foreground node
         Rectangle foregroundNode = new Rectangle(50, 5); // TODO changing width
         foregroundNode.getStyleClass().add("background");
         // changing the percentage displayed on the gauge
         ReadOnlyIntegerProperty pctProperty = type.equals("cards") ? state.cardPercentage() : state.ticketPercentage(); // TODO
         foregroundNode.widthProperty().bind(pctProperty.multiply(50).divide(100));

         groupNode.getChildren().addAll(backgroundNode, foregroundNode);
         deckNode.setGraphic(groupNode); // TODO or inverted ?

         return deckNode;
     }

}
