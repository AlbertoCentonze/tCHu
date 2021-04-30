package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import com.sun.javafx.scene.control.ReadOnlyUnbackedObservableList;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.unmodifiableObservableList;

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

    // map with properties containing the number of tickets of the players
    private final Map<PlayerId,IntegerProperty> ticketCount;
    // map with properties containing the number of cards of the players
    private final Map<PlayerId,IntegerProperty> cardCount;
    // map with properties containing the number of wagons of the players
    private final Map<PlayerId,IntegerProperty> wagonCount;
    // map with properties containing the number of construction points of the players
    private final Map<PlayerId,IntegerProperty> constructionPoints;

    // property containing the player's list of tickets
    private final ObservableList<Ticket> tickets;
    // list of 9 properties corresponding to the 9 types of cards
    // containing the number of the player's cards of each type
    private final List<IntegerProperty> numberOfEachCard;
    // list of properties for all routes stating whether the player can attempt to claim each route
    private final List<BooleanProperty> canClaimEachRoute;

    /**
     * ObservableGameState constructor
     * @param playerId : id of the player to whom this instance of ObservableGameState corresponds
     */
    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        // creating the properties concerning the publicGameState
        ticketPercentage = new SimpleIntegerProperty(0);
        cardPercentage = new SimpleIntegerProperty(0);
        faceUpCards = createFaceUpCards();
        routesOwners = createRoutesOwners();
        // creating the properties concerning the publicPlayerStates
        ticketCount = createMap();
        cardCount = createMap();
        wagonCount = createMap();
        constructionPoints = createMap();
        // creating the properties concerning the playerState of the player
        tickets = observableArrayList();
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

    private static Map<PlayerId,IntegerProperty> createMap() {
        Map<PlayerId,IntegerProperty> temp = new EnumMap<>(PlayerId.class);
        for(PlayerId id : PlayerId.ALL) {
            temp.put(id, new SimpleIntegerProperty(0));
        }
        return temp;
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


    /**
     * Update the content of the properties of ObservableGameState
     * @param gameState : the new PublicGameState
     * @param playerState : the new (complete) PlayerState
     */
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

        // updating the public players' states
        for(PlayerId id : PlayerId.ALL) {
            ticketCount.get(id).set(gameState.playerState(id).ticketCount());
            cardCount.get(id).set(gameState.playerState(id).cardCount());
            wagonCount.get(id).set(gameState.playerState(id).carCount());
            constructionPoints.get(id).set(gameState.playerState(id).claimPoints());
        }

        // updating the player's list of tickets
        tickets.setAll(playerState.tickets().toList());
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


    /**
     * Getter for the percentage of tickets left in the deck of tickets
     * @return (ReadOnlyIntegerProperty) (abstract) integer property that can only be read,
     * representing the percentage of tickets left
     */
    public ReadOnlyIntegerProperty ticketPercentage() {
        return ticketPercentage;
    }

    /**
     * Getter for the percentage of cards left in the deck of cards
     * @return (ReadOnlyIntegerProperty) (abstract) integer property that can only be read,
     * representing the percentage of cards left
     */
    public ReadOnlyIntegerProperty cardPercentage() {
        return cardPercentage;
    }

    /**
     * Getter for a faceUpCard
     * @param slot : slot of the desired faceUpCard
     * @return (ReadOnlyObjectProperty<Card>) (abstract) property that can only be read,
     * containing the desired card
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * Getter for the owner of a route
     * @param route : route whose owner you wish to know
     * @return (ReadOnlyObjectProperty<PlayerId>) (abstract) property that can only be read,
     * containing the owner of the specified route
     */
    public ReadOnlyObjectProperty<PlayerId> routesOwners(Route route) {
        return routesOwners.get(ChMap.routes().indexOf(route));
    }

    /**
     * Getter for the number of tickets of a player
     * @param id : id of the player
     * @return (ReadOnlyIntegerProperty) (abstract) integer property that can only be read,
     * representing the number of tickets owned by the specified player
     */
    public ReadOnlyIntegerProperty ticketCount(PlayerId id) {
        return ticketCount.get(id);
    }

    /**
     * Getter for the number of cards of a player
     * @param id : id of the player
     * @return (ReadOnlyIntegerProperty) (abstract) integer property that can only be read,
     * representing the number of cards of the specified player
     */
    public ReadOnlyIntegerProperty cardCount(PlayerId id) {
        return cardCount.get(id);
    }

    /**
     * Getter for the number of wagons of a player
     * @param id : id of the player
     * @return (ReadOnlyIntegerProperty) (abstract) integer property that can only be read,
     * representing the number of wagons of the specified player
     */
    public ReadOnlyIntegerProperty wagonCount(PlayerId id) {
        return wagonCount.get(id);
    }

    /**
     * Getter for the number of construction points of a player
     * @param id : id of the player
     * @return (ReadOnlyIntegerProperty) (abstract) integer property that can only be read,
     * representing the number of construction points gained by the specified player
     */
    public ReadOnlyIntegerProperty constructionPoints(PlayerId id) {
        return constructionPoints.get(id);
    }

    /**
     * Getter for the tickets of the player to whom this instance of ObservableGameState corresponds
     * @return (ObservableList<Ticket>) unmodifiable list of the player's tickets
     */
    public ObservableList<Ticket> tickets() {
        return unmodifiableObservableList(tickets);
    }

    /**
     * Getter for the player's number of cards of a specific type
     * @param card : type of card
     * @return (ReadOnlyIntegerProperty) (abstract) integer property that can only be read,
     * representing the number of cards of the specified type that the player possesses
     */
    public ReadOnlyIntegerProperty numberOfEachCard(Card card) {
        return numberOfEachCard.get(card.ordinal());
    }

    /**
     * Getter for the boolean value representing whether the player can claim a specific route
     * @param route : route
     * @return (ReadOnlyBooleanProperty) (abstract) boolean property that can only be read,
     * true if the player can claim the specified route
     */
    public ReadOnlyBooleanProperty canClaimEachRoute(Route route) {
        return canClaimEachRoute.get(ChMap.routes().indexOf(route));
    }

    /**
     * Establishing if there are enough tickets left to draw from the deck of tickets
     * @param gameState : current PublicGameState
     * @return (boolean) true if there is at least one ticket
     */
    public boolean canDrawTickets(PublicGameState gameState) { // TODO used where
        return gameState.canDrawTickets();
    }

    /**
     * Establishing if cards can be drawn from the deck of cards
     * @param gameState : current PublicGameState
     * @return (boolean) true if the sizes of the deck of cards and discard pile add up to at least five cards
     */
    public boolean canDrawCards(PublicGameState gameState) {
        return gameState.canDrawCards();
    }

    /**
     * List of all possible combinations of cards that the player could use to build the route
     * @param playerState : current (complete) PlayerState
     * @param route : route
     * @return (List<SortedBag<Card>>) list of possible combinations of cards
     */
    public List<SortedBag<Card>> possibleClaimCards(PlayerState playerState, Route route) {
        return playerState.possibleClaimCards(route);
    }

}
