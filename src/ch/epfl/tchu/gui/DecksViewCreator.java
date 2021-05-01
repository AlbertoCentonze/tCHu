package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.PublicGameState;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
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

         // TODO
         // creating the node of the text
         Text countNode = new Text();
         countNode.getStyleClass().add("count");

         if(card != null) {
             // showing the card only if the player owns at least one of this type
             ReadOnlyIntegerProperty count = state.numberOfEachCard(card);
             cardNode.visibleProperty().bind(Bindings.greaterThan(count, 0)); // TODO -1
             // displaying the number of cards of this type if count > 1
             countNode.textProperty().bind(Bindings.convert(count));
             countNode.visibleProperty().bind(Bindings.greaterThan(count, 1));
         }
         cardNode.getChildren().add(countNode);

         /*if(count > 0) {
             Text countNode = new Text(String.valueOf(count));
             countNode.getStyleClass().add("count");
             cardNode.getChildren().add(countNode);
         }*/
         cardNode.getChildren().addAll(outsideNode, insideNode, imageNode);
         return cardNode;
     }


     public static Pane createCardsView(ObservableGameState state, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler,
                                        ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler) { // TODO Pane
        // TODO return la vue des cartes
         VBox cardsViewNode = new VBox();
         cardsViewNode.setId("card-pane");
         cardsViewNode.getStylesheets().addAll("decks.css", "colors.css");

         // creating the node for the deck of tickets
         cardsViewNode.getChildren().add(createButtonNode());

         // creating the nodes of the faceUpCards
         for(int slot : Constants.FACE_UP_CARD_SLOTS) {
             cardsViewNode.getChildren().add(createNodeFromCard(state.faceUpCard(slot).get(), state));
         }

         // creating the node for the deck of cards
         cardsViewNode.getChildren().add(createButtonNode());

         return cardsViewNode;
     }

     private static Node createButtonNode() {
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

         groupNode.getChildren().addAll(backgroundNode, foregroundNode);
         deckNode.setGraphic(groupNode); // TODO or inverted ?

         return deckNode;
     }

}
