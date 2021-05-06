package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

public interface ActionHandlers {
    /**
     * Nested interface
     * Handler for drawing tickets
     */
    interface DrawTicketsHandler {
        /**
         * Called when drawing tickets
         */
         void onDrawTickets();
    }

    /**
     * Nested interface
     * Handler for drawing cards
     */
    interface DrawCardHandler {
        /**
         * Called when drawing cards
         * @param slot : [0,4] for the faceUpCards or -1 for the deck
         */
         void onDrawCard(int slot);
    }

    /**
     * Nested interface
     * Handler for claiming a route
     */
    interface ClaimRouteHandler {
        /**
         * Called when attempting to claim a route
         * @param route : route to claim
         * @param initialClaimCards : cards intended to claim the route
         */
         void onClaimRoute(Route route, SortedBag<Card> initialClaimCards);
    }

    /**
     * Nested interface
     * Handler for choosing tickets
     */
    interface ChooseTicketsHandler {
        /**
         * Called when choosing tickets to keep
         * @param options : tickets from which to choose the ones to keep
         */
        void onChooseTickets(SortedBag<Ticket> options);
    }

    /**
     * Nested interface
     * Handler for choosing cards
     */
    interface ChooseCardsHandler {
        /**
         * Called when choosing tickets to keep
         * @param options : cards from which to choose the initialClaimCards with which to attempt to claim a route
         *                or the additionalClaimCards necessary for claiming a tunnel (if empty it signifies that
         *                the player does not want to claim the tunnel)
         */
         void onChooseCards(SortedBag<Card> options);
    }
}
