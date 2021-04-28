package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;
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

     public HBox createHandView(ObservableGameState state) { // TODO return la vue de main
        HBox handViewNode = new HBox();
        handViewNode.setId("hand-pane");
        handViewNode.getStylesheets().addAll("decks.css", "colors.css");

        // creating the node that shows the tickets
        Control ticketNode = new ListView(); // TODO types
        ticketNode.setId("tickets");
        handViewNode.getChildren().add(ticketNode);

        // creating the nodes of the cards
        for(Card card : Card.ALL) { // TODO : state.ownState().cards()
            handViewNode.getChildren().add(createNodeFromCard(card, true));
        }

        return handViewNode; // TODO change
     }

     private Node createNodeFromCard(Card card, boolean text) {
        StackPane cardNode = new StackPane();
        cardNode.getStyleClass().addAll(card.name().equals("LOCOMOTIVE") ? "NEUTRAL" : card.name(), "card"); // TODO .name() for enum ?
         // card
         // outside of the card (rounded frame)
         Rectangle outsideNode = new Rectangle(60, 90);
         outsideNode.getStyleClass().add("outside");
         // inside of the card (colored)
         Rectangle insideNode = new Rectangle(40, 70);
         insideNode.getStyleClass().addAll("filled", "inside");
         // image on the card (wagon or locomotive)
         Rectangle imageNode = new Rectangle(40, 70);
         imageNode.getStyleClass().add("train-image");

         // counter
         if(text) { // TODO is this the best way ?
             Text countNode = new Text();
             countNode.getStyleClass().add("count");
             cardNode.getChildren().addAll(countNode);
         }

         cardNode.getChildren().addAll(outsideNode, insideNode, imageNode);
         return cardNode;
     }

     public Pane createCardsView(ObservableGameState state, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler,
                                 ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler) { // TODO Pane
        // TODO return la vue des cartes
         VBox cardsViewNode = new VBox();
         cardsViewNode.setId("card-pane");
         cardsViewNode.getStylesheets().addAll("decks.css", "colors.css");

         // creating the nodes of the faceUpCards
         for(Card card : state.faceUpCards) {
             cardsViewNode.getChildren().add(createNodeFromCard(card, false));
         }

         // creating the node for the deck of tickets and for the deck of cards
         for(int i = 0; i < 2; ++i) {
             cardsViewNode.getChildren().add(createButtonNode());
         }
         return cardsViewNode;
     }

     private Node createButtonNode() {
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
