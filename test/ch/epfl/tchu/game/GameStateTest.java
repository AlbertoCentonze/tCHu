package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.awt.geom.NoninvertibleTransformException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    private static final Station BAD = new Station(0, "Baden");
    private static final Station BAL = new Station(1, "Bâle");
    private static final Station BEL = new Station(2, "Bellinzone");
    private static final Station BER = new Station(3, "Berne");
    private static final Station BRI = new Station(4, "Brigue");
    private static final Station BRU = new Station(5, "Brusio");
    private static final Station COI = new Station(6, "Coire");
    private static final Station DAV = new Station(7, "Davos");
    private static final Station DEL = new Station(8, "Delémont");
    private static final Station FRI = new Station(9, "Fribourg");
    private static final Station GEN = new Station(10, "Genève");
    private static final Station INT = new Station(11, "Interlaken");
    private static final Station KRE = new Station(12, "Kreuzlingen");
    private static final Station LAU = new Station(13, "Lausanne");
    private static final Station LCF = new Station(14, "La Chaux-de-Fonds");
    private static final Station LOC = new Station(15, "Locarno");
    private static final Station LUC = new Station(16, "Lucerne");
    private static final Station LUG = new Station(17, "Lugano");
    private static final Station MAR = new Station(18, "Martigny");
    private static final Station NEU = new Station(19, "Neuchâtel");
    private static final Station OLT = new Station(20, "Olten");
    private static final Station PFA = new Station(21, "Pfäffikon");
    private static final Station SAR = new Station(22, "Sargans");
    private static final Station SCE = new Station(23, "Schaffhouse");
    private static final Station SCZ = new Station(24, "Schwyz");
    private static final Station SIO = new Station(25, "Sion");
    private static final Station SOL = new Station(26, "Soleure");
    private static final Station STG = new Station(27, "Saint-Gall");
    private static final Station VAD = new Station(28, "Vaduz");
    private static final Station WAS = new Station(29, "Wassen");
    private static final Station WIN = new Station(30, "Winterthour");
    private static final Station YVE = new Station(31, "Yverdon");
    private static final Station ZOU = new Station(32, "Zoug");
    private static final Station ZUR = new Station(33, "Zürich");

    // Stations - countries
    private static final Station DE1 = new Station(34, "Allemagne");
    private static final Station DE2 = new Station(35, "Allemagne");
    private static final Station DE3 = new Station(36, "Allemagne");
    private static final Station DE4 = new Station(37, "Allemagne");
    private static final Station DE5 = new Station(38, "Allemagne");
    private static final Station AT1 = new Station(39, "Autriche");
    private static final Station AT2 = new Station(40, "Autriche");
    private static final Station AT3 = new Station(41, "Autriche");
    private static final Station IT1 = new Station(42, "Italie");
    private static final Station IT2 = new Station(43, "Italie");
    private static final Station IT3 = new Station(44, "Italie");
    private static final Station IT4 = new Station(45, "Italie");
    private static final Station IT5 = new Station(46, "Italie");
    private static final Station FR1 = new Station(47, "France");
    private static final Station FR2 = new Station(48, "France");
    private static final Station FR3 = new Station(49, "France");
    private static final Station FR4 = new Station(50, "France");

    private final List<Route> manyRoutes = List.of(
            new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null),
            new Route("AT2_VAD_1", AT2, VAD, 1, Route.Level.UNDERGROUND, Color.RED),
            new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED),
            new Route("BAD_OLT_1", BAD, OLT, 2, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE),
            new Route("BAL_DEL_1", BAL, DEL, 2, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("BAL_OLT_1", BAL, OLT, 2, Route.Level.UNDERGROUND, Color.ORANGE),
            new Route("BEL_LOC_1", BEL, LOC, 1, Route.Level.UNDERGROUND, Color.BLACK),
            new Route("BEL_LUG_1", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.RED),
            new Route("BEL_LUG_2", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null),
            new Route("BEL_WAS_2", BEL, WAS, 4, Route.Level.UNDERGROUND, null),
            new Route("BER_FRI_1", BER, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("BER_FRI_2", BER, FRI, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("BER_INT_1", BER, INT, 3, Route.Level.OVERGROUND, Color.BLUE),
            new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null),
            new Route("BER_LUC_2", BER, LUC, 4, Route.Level.OVERGROUND, null),
            new Route("BER_NEU_1", BER, NEU, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("BER_SOL_1", BER, SOL, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("BRI_INT_1", BRI, INT, 2, Route.Level.UNDERGROUND, Color.WHITE),
            new Route("BRI_IT5_1", BRI, IT5, 3, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null),
            new Route("BRI_SIO_1", BRI, SIO, 3, Route.Level.UNDERGROUND, Color.BLACK),
            new Route("BRI_WAS_1", BRI, WAS, 4, Route.Level.UNDERGROUND, Color.RED),
            new Route("BRU_COI_1", BRU, COI, 5, Route.Level.UNDERGROUND, null),
            new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE),
            new Route("BRU_IT2_1", BRU, IT2, 2, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("COI_DAV_1", COI, DAV, 2, Route.Level.UNDERGROUND, Color.VIOLET));

    private static List<Route> getRoutesListFromIds(List<String> ids){
        List<Route> routes = ChMap.routes();
        return routes.stream().filter(route -> {
            for (String id : ids) {
                if (route.id().equals(id)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }



    public static final Object reflect(Deck<Card> deck) throws IllegalAccessException{
        Class<?> classReference = deck.getClass();
        List<Field> fields = Arrays.asList(classReference.getFields());
        fields.stream().filter(f -> f.getName() == "deck");
        try{
            Object cards = fields.get(0).get(deck);
            return cards;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static final Random NON_RANDOM = new Random() {
        @Override
        public int nextInt(int i) {
            return i-1;
        }
    };

    private SortedBag<Ticket> tickets = SortedBag.of(List.of(new Ticket(BAD, BAL, 5), new Ticket(LAU, FR1, 7), new Ticket(YVE, WAS, 6)));

    GameState initialState = GameState.initial(tickets, NON_RANDOM);

    @Test // TODO not testing anything
    void initialWorks() {
        GameState initialState = GameState.initial(tickets, NON_RANDOM);
    }

    @Test
    void playerStateWorks() { // return complete playerState
        // and Map<PlayerId, PlayerState> playerStateTemp works
        assertEquals(SortedBag.of(), initialState.playerState(PlayerId.PLAYER_1).tickets());
        assertEquals(SortedBag.of(), initialState.playerState(PlayerId.PLAYER_2).tickets());

        assertEquals(SortedBag.of(Constants.ALL_CARDS.toList().subList(0,Constants.INITIAL_CARDS_COUNT)), initialState.playerState(PlayerId.PLAYER_1).cards());
        assertEquals(SortedBag.of(Constants.ALL_CARDS.toList().subList(Constants.INITIAL_CARDS_COUNT, Constants.INITIAL_CARDS_COUNT*2)), initialState.playerState(PlayerId.PLAYER_2).cards());

        assertEquals(Collections.emptyList(), initialState.playerState(PlayerId.PLAYER_1).routes());
        assertEquals(Collections.emptyList(), initialState.playerState(PlayerId.PLAYER_1).routes());
    }

    @Test
    void currentPlayerStateWorks() {
        assertEquals(SortedBag.of(), initialState.currentPlayerState().tickets());
        // temporary test -> choosing the current player in initial() works
        //assertEquals(SortedBag.of(Constants.ALL_CARDS.toList().subList(0,Constants.INITIAL_CARDS_COUNT)), initialState.currentPlayerState().cards());
    }

    @Test
    void topTicketsFailsWithWrongCount() {
        assertThrows(IllegalArgumentException.class, () -> initialState.topTickets(-1));
        assertThrows(IllegalArgumentException.class, () -> initialState.topTickets(4));
    }

    @Test
    void topTicketsWorks() {
        assertEquals(SortedBag.of(List.of(new Ticket(BAD, BAL, 5), new Ticket(LAU, FR1, 7))), initialState.topTickets(2));
    }

    @Test
    void topTicketsWorksWithZeroCount() {
        assertEquals(SortedBag.of(), initialState.topTickets(0));
    }

    @Test
    void withoutTopTicketsFailsWithWrongCount() {
        assertThrows(IllegalArgumentException.class, () -> initialState.withoutTopTickets(-1));
        assertThrows(IllegalArgumentException.class, () -> initialState.withoutTopTickets(4));
    }

    @Test
    void withoutTopTicketsWorks() {
        // taken 2 of the 3 tickets away
        assertEquals(SortedBag.of(List.of(new Ticket(YVE, WAS, 6))), initialState.withoutTopTickets(2).topTickets(1));
    }

    @Test
    void topCardWorks() {
        //Constants.ALL_CARDS.stream().forEach(e -> System.out.println(e.name()));

        // TODO the top 5 cards from the initial deck were taken and put face up by calling CardState.of()
        assertEquals(Constants.ALL_CARDS.get(Constants.INITIAL_CARDS_COUNT*2+5), initialState.topCard());
    }

    @Test
    void withoutTopDeckCard() {
        assertEquals(Constants.ALL_CARDS.toList().subList(14,Constants.ALL_CARDS.size()).size(), initialState.withoutTopCard().cardState().deckSize());
    }

    @Test
    void withMoreDiscardedCardsWorks() {
        assertEquals(3, initialState.withMoreDiscardedCards(SortedBag.of(3, Card.ORANGE)).cardState().discardsSize());
    }

    @Test
    void withCardsDeckRecreatedIfNeededWorksIfDeckNotEmpty() {
        assertEquals(initialState, initialState.withCardsDeckRecreatedIfNeeded(NON_RANDOM));
    }

    @Test
    void withCardsDeckRecreatedIfNeededWorksIfDeckEmpty() {
        GameState copy = initialState;
        while(copy.cardState().deckSize() != 0) {
            copy = copy.withMoreDiscardedCards(SortedBag.of(1, copy.topCard()));
            copy = copy.withoutTopCard();
        }
        assertTrue(copy.cardState().deckSize() == 0);
        assertEquals(initialState.cardState().deckSize(), copy.withCardsDeckRecreatedIfNeeded(NON_RANDOM).cardState().deckSize());
        assertTrue(copy.withCardsDeckRecreatedIfNeeded(NON_RANDOM).cardState().deckSize() == (110-8-5));
    }

    @Test
    void withChosenAdditionalTicketsWorks() {
        assertEquals(SortedBag.of(List.of(new Ticket(BAD, BAL, 5))), initialState.withChosenAdditionalTickets(tickets, SortedBag.of(List.of(new Ticket(BAD, BAL, 5)))).currentPlayerState().tickets());
    }

    @Test
    void withChosenAdditionalTicketsFailsIfChosenNotContainedInDrawn() {
        assertThrows(IllegalArgumentException.class, () -> initialState.withChosenAdditionalTickets(SortedBag.of(List.of(new Ticket(BAD, BAL, 5))), tickets));
    }

    @Test
    void withInitiallyChoseTicketsFailsIfAlreadyOwnsTickets() {
        GameState temp = initialState.withChosenAdditionalTickets(tickets, SortedBag.of(List.of(new Ticket(BAD, BAL, 5))));
        assertThrows(IllegalArgumentException.class, () -> temp.withInitiallyChosenTickets(temp.currentPlayerId(), SortedBag.of(List.of(new Ticket(BAD, BAL, 5)))));
    }

    @Test
    void withInitiallyChoseTicketsWorks() {
        assertEquals(SortedBag.of(List.of(new Ticket(BAD, BAL, 5))), initialState.withInitiallyChosenTickets(initialState.currentPlayerId(), SortedBag.of(List.of(new Ticket(BAD, BAL, 5)))).currentPlayerState().tickets());
    }

    @Test
    void withDrawnFaceUpCardFailsIfNotEnoughCards() {
        GameState copy = initialState;
        while(copy.cardState().deckSize() >= 5) {
            copy = copy.withoutTopCard();
        }
        assertTrue(copy.cardState().deckSize() == 4);
        GameState finalCopy = copy;
        assertThrows(IllegalArgumentException.class, () -> finalCopy.withDrawnFaceUpCard(2));
    }

    @Test
    void withDrawnFaceUpCardWorks() {
        //System.out.println(initialState.cardState().faceUpCard(1).name());
        assertEquals(initialState.currentPlayerState().cards().union(SortedBag.of(1, initialState.cardState().faceUpCard(1))), initialState.withDrawnFaceUpCard(1).currentPlayerState().cards());

        assertEquals(initialState.topCard(), initialState.withDrawnFaceUpCard(1).cardState().faceUpCard(1));
    }

    @Test
    void withBlindlyDrawnCardFailsIfNotEnoughCards() {
        GameState copy = initialState;
        while(copy.cardState().deckSize() >= 5) {
            copy = copy.withoutTopCard();
        }
        assertTrue(copy.cardState().deckSize() == 4);
        GameState finalCopy = copy;
        assertThrows(IllegalArgumentException.class, () -> finalCopy.withBlindlyDrawnCard());
    }

    @Test
    void withBlindlyDrawnCardWorks() {
        assertEquals(initialState.currentPlayerState().cards().union(SortedBag.of(1, initialState.topCard())), initialState.withBlindlyDrawnCard().currentPlayerState().cards());

        assertTrue(initialState.cardState().deckSize() == initialState.withBlindlyDrawnCard().cardState().deckSize()+1);
    }

    @Test
    void withClaimedRouteWorks() {
        GameState initialState1 = GameState.initial(tickets, NON_RANDOM);

        assertEquals(List.of(manyRoutes.get(8)), initialState.withClaimedRoute(manyRoutes.get(8), SortedBag.of(1, Card.BLACK)).currentPlayerState().routes());
        assertEquals(1, initialState.withClaimedRoute(manyRoutes.get(8), SortedBag.of(1, Card.BLACK)).cardState().discardsSize());
        System.out.println(initialState1.currentPlayerState().cards().size());

        // TODO issue modifies cards too many times...
        assertTrue(initialState1.withClaimedRoute(manyRoutes.get(8), SortedBag.of(1, Card.BLACK)).currentPlayerState().cards().size() == 3);
    }

    @Test
    void forNextTurnWorksNotLastTurn() {
        GameState temp = initialState.forNextTurn();
        assertEquals(initialState.currentPlayerId().next(), temp.currentPlayerId());

        assertTrue(temp.lastPlayer() == null);
    }

    PlayerState playerFewWagons = new PlayerState(tickets, SortedBag.of(3, Card.ORANGE), manyRoutes);

    @Test
    void forNextTurnWorksLastTurn() {
        GameState copy = initialState;
        while(copy.cardState().deckSize() >= 5) {
            copy = copy.withBlindlyDrawnCard();
        }
        for(Route r : manyRoutes) {
            if(r.color() != null) {
                copy = copy.withClaimedRoute(r, SortedBag.of(r.length(), Card.of(r.color())));
            }
        }
        System.out.println(copy.currentPlayerState().carCount());
        copy = copy.forNextTurn();
        assertTrue(copy.lastPlayer() == initialState.currentPlayerId());
    }
}
