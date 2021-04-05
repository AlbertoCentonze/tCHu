package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState {
    // tickets
    private final Deck<Ticket> tickets;
    // map associating the player's id to his public and private state
    private final Map<PlayerId, PlayerState> privatePlayerState;
    // public and private cardState
    private final CardState cardState;

    /**
     * Crete a Map associating the player's ids to their public states
     * @param playerState : Map of the public and private states of the players
     * @return (Map<PlayerId, PublicPlayerState>) Map of the public states of the players
     */
    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerState) {
        Map<PlayerId, PublicPlayerState> publicPlayerState = new EnumMap<>(PlayerId.class);
        playerState.forEach((k,v) -> publicPlayerState.put(k, new PublicPlayerState(v.ticketCount(), v.cardCount(), v.routes())));
        return publicPlayerState;
    }

    /**
     * Internal constructor for GameState
     * @param tickets : deck of tickets
     * @param cardState : public and private state of the cards
     * @param currentPlayerId : id of current player
     * @param playerState : map associating the player's id to his public and private state
     * @param lastPlayer : player of the final turn
     */
    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, makePublic(playerState), lastPlayer);
        this.tickets = tickets;
        this.privatePlayerState = playerState;
        this.cardState = cardState;
    }

    /**
     * Create the initial GameState
     * @param tickets : tickets with which to form the tickets' deck
     * @param rng : randomizer
     * @return (GameState) initial GameState
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        // shuffled tickets
        Deck<Ticket> shuffledTickets = Deck.of(tickets, rng);

        // shuffled cards
        Deck<Card> shuffledCards = Deck.of(Constants.ALL_CARDS, rng);
        // 8 cards to distribute to the players
        List<Card> playersCards = (shuffledCards.topCards(Constants.INITIAL_CARDS_COUNT * 2)).toList();
        // remove top 8 cards (distributed to the two players)
        Deck<Card> cards = shuffledCards.withoutTopCards(Constants.INITIAL_CARDS_COUNT * 2);

        // choosing the first player
        PlayerId currentPlayerId = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));
        // for tests
        // PlayerId currentPlayerId = PlayerId.ALL.get(rng.nextInt(1));

        // creating a Map associating the players' ids to their player states
        Map<PlayerId, PlayerState> playerStateTemp = new EnumMap<>(PlayerId.class);
        playerStateTemp.put(PlayerId.PLAYER_1,
                PlayerState.initial(SortedBag.of(playersCards.subList(0, Constants.INITIAL_CARDS_COUNT))));
        playerStateTemp.put(PlayerId.PLAYER_2,
                PlayerState.initial(SortedBag.of(playersCards.subList(Constants.INITIAL_CARDS_COUNT, Constants.INITIAL_CARDS_COUNT*2))));

        return new GameState(shuffledTickets, CardState.of(cards), currentPlayerId, playerStateTemp, null);
    }

    @Override
    public PlayerState playerState(PlayerId playerId) {
        return this.privatePlayerState.get(playerId);
    }

    @Override
    public PlayerState currentPlayerState() {
        return this.privatePlayerState.get(currentPlayerId());
    }

    /**
     * Return top count tickets from the deck of tickets
     * @param count : number of tickets
     * @return (SortedBag<Ticket>) top count tickets
     */
    public SortedBag<Ticket> topTickets(int count) {
        // check count is between 0 and the size of the deck of cards
        if (count > tickets.size()){
            System.out.println("sadlkkfh");
        }
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return this.tickets.topCards(count);
    }

    /**
     * Take away top count tickets from the deck of tickets
     * @param count : number of tickets taken away
     * @return (GameState) new GameState without top count tickets
     */
    public GameState withoutTopTickets(int count) {
        // check count is between 0 and the size of the deck of cards
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(tickets.withoutTopCards(count), this.cardState, currentPlayerId(), this.privatePlayerState, lastPlayer());
    }

    /**
     * Return top card from the deck of cards
     * @return (Card) top card
     */
    public Card topCard() {
        // check deck of cards isn't empty
        Preconditions.checkArgument(cardState().deckSize() != 0);
        return this.cardState.topDeckCard();
    }

    /**
     * Take away top card from the deck of cards
     * @return (GameState) new GameState without top card
     */
    public GameState withoutTopCard() {
        // check deck of cards isn't empty
        Preconditions.checkArgument(cardState().deckSize() != 0);
        return new GameState(this.tickets, this.cardState.withoutTopDeckCard(), currentPlayerId(), this.privatePlayerState, lastPlayer());
    }

    /**
     * Add cards to discard pile
     * @param discardedCards : cards to add to discards
     * @return (GameState) new GameState with cards added to discards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(this.tickets, this.cardState.withMoreDiscardedCards(discardedCards),currentPlayerId(), this.privatePlayerState, lastPlayer());
    }

    /**
     * Recreate deck of cards from the discard pile if the deck of cards is empty
     * @param rng : randomizer
     * @return (GameState) same GameState if the the deck isn't empty, new GameState with deck recreated from discards otherwise
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return cardState().deckSize() != 0 ? this
                : new GameState(this.tickets, this.cardState.withDeckRecreatedFromDiscards(rng), currentPlayerId(), this.privatePlayerState, lastPlayer());
    }

    /**
     * Add the chosen tickets to the player's tickets
     * @param playerId : id of the player
     * @param chosenTickets : tickets kept by player
     * @return (GameState) new GameState with additional player's tickets
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        // check player doesn't own any tickets
        Preconditions.checkArgument(playerState(playerId).tickets().isEmpty());
        // mutable copy of privatePlayerState
        Map<PlayerId, PlayerState> privatePlayerStateTemp = new EnumMap<>(this.privatePlayerState);
        privatePlayerStateTemp.replace(playerId, playerState(playerId).withAddedTickets(chosenTickets));
        return new GameState(this.tickets, this.cardState, currentPlayerId(), privatePlayerStateTemp, lastPlayer());
    }

    /**
     * Add chosen tickets to the player's tickets and remove drawn tickets from the deck of tickets
     * @param drawnTickets : tickets drawn by the player
     * @param chosenTickets : tickets chosen by the player
     * @return (GameState) new GameState with additional player's tickets
     * and deck of tickets without the drawn tickets
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        // check that the chosen tickets are found among the drawn tickets
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        // mutable copy of privatePlayerState
        Map<PlayerId, PlayerState> privatePlayerStateTemp = new EnumMap<>(this.privatePlayerState);
        privatePlayerStateTemp.replace(currentPlayerId(), playerState(currentPlayerId()).withAddedTickets(chosenTickets));
        return new GameState(this.tickets.withoutTopCards(drawnTickets.size()), this.cardState, currentPlayerId(), privatePlayerStateTemp, lastPlayer());
    }

    /**
     * Add a faceUpCard to the player's cards and replace the taken faceUpCard with the top card from the deck of cards
     * @param slot : slot from which the player has taken a faceUpCard
     * @return (GameState) new GameState with the faceUpCard at the slot indicated added to the player's cards
     * and the faceUpCard slot replaced with the top card from the deck of cards
     */
    public GameState withDrawnFaceUpCard(int slot) {
        // check that a card can be drawn
        Preconditions.checkArgument(canDrawCards());
        // mutable copy of privatePlayerState
        Map<PlayerId, PlayerState> privatePlayerStateTemp = new EnumMap<>(this.privatePlayerState);
        privatePlayerStateTemp.replace(currentPlayerId(), playerState(currentPlayerId()).withAddedCard(cardState().faceUpCard(slot)));
        return new GameState(this.tickets, this.cardState.withDrawnFaceUpCard(slot), currentPlayerId(), privatePlayerStateTemp, lastPlayer());
    }

    /**
     * Add the topCard to the player's cards and take it away from the deck of cards
     * @return (GameState) new GameState with the topCard from the deck of cards added to the player's cards
     * and removed from the deck of cards
     */
    public GameState withBlindlyDrawnCard() {
        // check that a card can be drawn
        Preconditions.checkArgument(canDrawCards());
        // mutable copy of privatePlayerState
        Map<PlayerId, PlayerState> privatePlayerStateTemp = new EnumMap<>(this.privatePlayerState);
        privatePlayerStateTemp.replace(currentPlayerId(), playerState(currentPlayerId()).withAddedCard(topCard()));
        return new GameState(this.tickets, this.cardState.withoutTopDeckCard(), currentPlayerId(), privatePlayerStateTemp, lastPlayer());
    }

    /**
     * Add a route to the player's routes
     * and put the cards used by the player to claim the route in the discard pile
     * @param route : route claimed by the player
     * @param cards : cards used to claim the route
     * @return (GameState) new GameState with claimed route added to the player's routes
     * and cards used to claim the route added to the discard pile
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        // mutable copy of privatePlayerState
        Map<PlayerId, PlayerState> privatePlayerStateTemp = new EnumMap<>(this.privatePlayerState);
        privatePlayerStateTemp.replace(currentPlayerId(), playerState(currentPlayerId()).withClaimedRoute(route, cards));
        return new GameState(this.tickets, this.cardState.withMoreDiscardedCards(cards), currentPlayerId(), privatePlayerStateTemp, lastPlayer());
    }

    /**
     * Determine whether the last turn begins
     * @return (boolean) true if the current player has 2 wagons left or fewer
     */
    public boolean lastTurnBegins() {
        return playerState(currentPlayerId()).carCount() <= 2;
    }

    /**
     * End the turn of the current player
     * establish the last player if the last turn is beginning
     * @return (GameState) new GameState where the current player has switched
     */
    public GameState forNextTurn() {
        PlayerId lastPlayer = lastTurnBegins() ? currentPlayerId() : lastPlayer();
        return new GameState(this.tickets, this.cardState, currentPlayerId().next(), this.privatePlayerState, lastPlayer);
    }
}
