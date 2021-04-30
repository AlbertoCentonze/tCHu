package ch.epfl.tchu.gui;


import ch.epfl.tchu.game.*;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

public class ObservableGameState {
    // player to whom this instance of ObservableGameState corresponds
    PlayerId playerId;

    // property containing remaining percentage of tickets in the deck
    private final IntegerProperty ticketPercentage;
    // property containing remaining percentage of cards in the deck
    private final IntegerProperty cardPercentage;
    // list of 5 properties containing the faceUpCards
    private final List<ObjectProperty<Card>> faceUpCards;
    // list of properties for all routes containing the owner of each route
    private final List<ObjectProperty<PlayerId>> routesOwners;

    // TODO List<IntegerProperty> publicStatePlayer1 & 2 ?     Map
    // property containing the number of tickets of PLAYER_1
    private final IntegerProperty ticketCountPlayer1;
    // property containing the number of cards of PLAYER_1
    private final IntegerProperty cardCountPlayer1;
    // property containing the number of wagons of PLAYER_1
    private final IntegerProperty wagonCountPlayer1;
    // property containing the number of construction points of PLAYER_1
    private final IntegerProperty constructionPointsPlayer1;
    // property containing the number of tickets of PLAYER_2
    private final IntegerProperty ticketCountPlayer2;
    // property containing the number of cards of PLAYER_2
    private final IntegerProperty cardCountPlayer2;
    // property containing the number of wagons of PLAYER_2
    private final IntegerProperty wagonCountPlayer2;
    // property containing the number of construction points of PLAYER_2
    private final IntegerProperty constructionPointsPlayer2;

    // property containing the player's list of tickets
    private final ObservableList<Object> tickets;
    // list of 9 properties corresponding to the 9 types of cards
    // containing the number of the player's cards of each type
    private final List<IntegerProperty> numberOfEachCard;
    // list of properties for all routes stating whether the player can attempt to claim each route
    private final List<BooleanProperty> canClaimEachRoute;

    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        // creating the properties concerning the publicGameState
        ticketPercentage = new SimpleIntegerProperty(0);
        cardPercentage = new SimpleIntegerProperty(0);
        faceUpCards = createFaceUpCards();
        routesOwners = createRoutesOwners();
        // creating the properties concerning the publicPlayerStates
        ticketCountPlayer1 = new SimpleIntegerProperty(0); // TODO do all these need to be regrouped in a list ? for each player --> would allow to modularize
        ticketCountPlayer2 = new SimpleIntegerProperty(0);
        cardCountPlayer1 = new SimpleIntegerProperty(0);
        cardCountPlayer2 = new SimpleIntegerProperty(0);
        wagonCountPlayer1 = new SimpleIntegerProperty(0);
        wagonCountPlayer2 = new SimpleIntegerProperty(0);
        constructionPointsPlayer1 = new SimpleIntegerProperty(0);
        constructionPointsPlayer2 = new SimpleIntegerProperty(0);
        // creating the properties concerning the playerState of the player
        tickets = observableArrayList(); // TODO leave empty ?
        numberOfEachCard = createNumberOfEachCard();
        canClaimEachRoute = createCanClaimEachRoute();
    }

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> properties = new ArrayList<>();
        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) {
            // each faceUpCard is initially set to null
            properties.add(new SimpleObjectProperty<>(null));
        }
        return properties;
    }

    private static List<ObjectProperty<PlayerId>> createRoutesOwners() {
        List<ObjectProperty<PlayerId>> properties = new ArrayList<>();
        for(Route r : ChMap.routes()) {
            // the player who owns the route is initially set to null
            properties.add(new SimpleObjectProperty<>(null));
        }
        return properties;
    }

    private static List<IntegerProperty> createNumberOfEachCard() {
        List<IntegerProperty> properties = new ArrayList<>();
        for(Card card : Card.ALL) {
            // the number of the player's cards of each type is initially set to null
            properties.add(new SimpleIntegerProperty(0));
        }
        return properties;
    }

    private static List<BooleanProperty> createCanClaimEachRoute() {
        List<BooleanProperty> properties = new ArrayList<>();
        for(Route r : ChMap.routes()) {
            // the player can not initially claim the route
            properties.add(new SimpleBooleanProperty(false));
        }
        return properties;
    }


    public void setState(PublicGameState gameState, PlayerState playerState) {
        // calculating the percentage of remaining tickets and cards in the respective decks
        ticketPercentage.set(Math.round(((float) gameState.ticketsCount())/ChMap.tickets().size())*100); // TODO no need to round ? or cast to double and then round
        cardPercentage.set(Math.round(((float) gameState.ticketsCount())/Constants.ALL_CARDS.size())*100); // TODO no need to round ? or cast to double and then round
        // updating the faceUpCards
        for(int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = gameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        // updating each route's owner
        for(Route route : ChMap.routes()) {
            // if the route has been claimed determine which player claimed it and set him as the owner
            if(gameState.claimedRoutes().contains(route)) {
                if (playerState.routes().contains(route)) {
                    routesOwners.get(routeIndex(route)).set(playerId);
                } else {
                    routesOwners.get(routeIndex(route)).set(playerId.next());
                }
            }
        }

        // public players' states


        // updating the player's list of tickets
        tickets.setAll(playerState.tickets());
        // updating the player's number of cards of each type
        for(Card card : Card.ALL) {
            numberOfEachCard.get(card.ordinal()).set(playerState.cards().countOf(card));
        }
        // updating whether the player can attempt to claim each route
        for(Route route : ChMap.routes()) {
            boolean neighborNotTaken = true;
            // finding, if it exists, the neighboring route of a double route
            List<Route> doubleRoute = ChMap.routes().stream()
                    .filter(r -> r.station1().equals(route.station1()) &&
                            r.station2().equals(route.station2()) && !r.equals(route))
                    .collect(Collectors.toList());
            if(!doubleRoute.isEmpty()) {
                // checking whether the neighboring route has been taken
                neighborNotTaken = routesOwners(doubleRoute.get(0)) == null;
            }
            // player can claim a route if he is the current player, if the route has not yet been claimed,
            // if, in case of a double route, the neighbor has not been claimed,
            // if he has the cards and wagons necessary to claim it
            boolean canClaim = playerId.equals(gameState.currentPlayerId()) && routesOwners(route) == null &&
                    neighborNotTaken && playerState.canClaimRoute(route);
            canClaimEachRoute.get(routeIndex(route)).set(canClaim);
        }
    }

    private static int routeIndex(Route r) { // TODO static
        return ChMap.routes().indexOf(r);
    }

    // getters
    public ReadOnlyIntegerProperty ticketPercentage() {
        return ticketPercentage;
    }

    public ReadOnlyIntegerProperty cardPercentage() {
        return cardPercentage;
    }

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    public ReadOnlyObjectProperty<PlayerId> routesOwners(Route route) { // TODO or index ?
        return routesOwners.get(ChMap.routes().indexOf(route));
    }

    public ReadOnlyIntegerProperty ticketCountPlayer1() {
        return ticketCountPlayer1;
    }

    public ReadOnlyIntegerProperty ticketCountPlayer2() {
        return ticketCountPlayer2;
    }

    public ReadOnlyIntegerProperty cardCountPlayer1() {
        return cardCountPlayer1;
    }

    public ReadOnlyIntegerProperty cardCountPlayer2() {
        return cardCountPlayer2;
    }

    public ReadOnlyIntegerProperty wagonCountPlayer1() {
        return wagonCountPlayer1;
    }

    public ReadOnlyIntegerProperty wagonCountPlayer2() {
        return wagonCountPlayer2;
    }

    public ReadOnlyIntegerProperty constructionPointsPlayer1() {
        return constructionPointsPlayer1;
    }

    public ReadOnlyIntegerProperty constructionPointsPlayer2() {
        return constructionPointsPlayer2;
    }

    // TODO no getter for unmodifiableObservableList ?

    public ReadOnlyIntegerProperty numberOfEachCard(Card card) {
        return numberOfEachCard.get(card.ordinal());
    }

    public ReadOnlyBooleanProperty canClaimEachRoute(Route route) {
        return canClaimEachRoute.get(ChMap.routes().indexOf(route));
    }

}
