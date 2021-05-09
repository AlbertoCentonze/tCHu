package ch.epfl.tchu.net;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import javax.swing.event.CellEditorListener;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SerdesTest {
    private static <T> void testSerde(List<T> testList, Serde<T> serde){
        Set<String> serializedSet = new HashSet<>();
        Set<T> deserializedSet = new HashSet<>();
        for (T t : testList){
            String serialized = serde.serialize(t);
            serializedSet.add(serialized);
            T deserialized = serde.deserialize(serialized);
            deserializedSet.add(deserialized);
            assertEquals(t, deserialized);
        }
        assertEquals(new HashSet<>(testList).size(), serializedSet.size());
        assertEquals(new HashSet<>(testList), deserializedSet);
    }

    private static List<String> randomStringGenerator(){
        List<String> stringList = new ArrayList<>();
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        for (int i = 0; i < 100; ++i){
            StringBuilder sb = new StringBuilder(rnd.nextInt(100));
            for (int n = 0; n < 100; n++)
                sb.append(chars.charAt(rnd.nextInt(chars.length())));
            stringList.add(sb.toString());
        }
        return stringList;
    }

    private static <T extends Comparable<T>> List<SortedBag<T>> sortedBagFromSource(List<T> source){
        List<SortedBag<T>> listOfSortedBags = new ArrayList<>();
        for (int i = 0; i < source.size(); ++i){
            for (int n = source.size(); n > 0; --n){
                if (i == n)
                    break;
                SortedBag<T> element = SortedBag.of(source.subList(i, n));
                if (!listOfSortedBags.contains(element))
                    listOfSortedBags.add(element);
            }
        }
        return listOfSortedBags;
    }

    private static <T> List<List<T>> sublistFromSource(List<T> source){
        List<List<T>> listOfSublist = new ArrayList<>();
        for (int i = 0; i < source.size(); ++i){
            for (int n = source.size(); n > 0; --n){
                if (i == n)
                    break;
                List<T> element = source.subList(i, n);
                if (!listOfSublist.contains(element))
                    listOfSublist.add(element);
            }
        }
        return listOfSublist;
    }

    private static <T> List<T> listFromRandomElements(List<T> source){
        Random rnd = new Random();
        List<T> randomList = new ArrayList<>();
        for (int i = 0; i < source.size(); ++i){
            T newElement = source.get(rnd.nextInt(source.size()));
            if (!randomList.contains(newElement))
                randomList.add(newElement);
        }
        return randomList;
    }

    @Test
    void integerSerdeWorks(){
        List<Integer> integerList = IntStream.range(0, 1000).boxed().collect(Collectors.toList());
        testSerde(integerList, Serdes.INTEGER_SERDE);
    }

    @Test
    void stringSerdeWorks(){
        testSerde(randomStringGenerator(), Serdes.STRING_SERDE);
    }

    @Test
    void playerIdSerdeWorks(){
        testSerde(PlayerId.ALL, Serdes.PLAYER_ID_SERDE);
    }

    @Test
    void TurnKindSerdeWorks(){
        testSerde(Player.TurnKind.ALL, Serdes.TURN_KIND_SERDE);
    }

    @Test
    void cardSerdeWorks(){
        testSerde(Card.ALL, Serdes.CARD_SERDE);
    }

    @Test
    void routeSerdeWorks(){
        testSerde(ChMap.routes(), Serdes.ROUTE_SERDE);
    }

    @Test
    void ticketSerdeWorks(){
        testSerde(ChMap.tickets(), Serdes.TICKET_SERDE);
    }

    @Test
    void listOfStringSerdeWorks(){
        List<List<String>> listOfString = new ArrayList<>();
        for (int i = 0; i < 100; ++i)
            listOfString.add(randomStringGenerator());
        testSerde(listOfString, Serdes.LIST_OF_STRING_SERDE);
    }

    @Test
    void listOfStringSerdeWorksWithEmptyList(){
        testSerde(List.of(List.of()), Serdes.LIST_OF_STRING_SERDE);
    }

    @Test
    void listOfCardSerdeWorks(){
        List<List<Card>> allSubLists = sublistFromSource(Card.ALL);
        List<List<Card>> listOfCards = listFromRandomElements(allSubLists);
        testSerde(listOfCards, Serdes.LIST_OF_CARD_SERDE);
    }

    @Test
    void listOfCardSerdeWorksWithEmptyList(){
        testSerde(List.of(List.of()), Serdes.LIST_OF_CARD_SERDE);
    }

    @Test
    void listOfRouteSerdeWorks(){
        List<List<Route>> allSubLists = sublistFromSource(ChMap.routes());
        List<List<Route>> listOfRoutes = listFromRandomElements(allSubLists);
        testSerde(listOfRoutes, Serdes.LIST_OF_ROUTE_SERDE);
    }

    @Test
    void listOfRouteSerdeWorksWithEmptyList(){
        testSerde(List.of(List.of()), Serdes.LIST_OF_ROUTE_SERDE);
    }

    @Test
    void sortedBagOfCardSerdeWorks(){
        List<SortedBag<Card>> allSubLists = sortedBagFromSource(Card.ALL);
        List<SortedBag<Card>> listOfSortedbagOfCards = listFromRandomElements(allSubLists);
        testSerde(listOfSortedbagOfCards, Serdes.SORTEDBAG_OF_CARD_SERDE);
    }

    @Test
    void sortedBagOfCardSerdeWorksWithEmptyList(){
        testSerde(List.of(SortedBag.of()), Serdes.SORTEDBAG_OF_CARD_SERDE);
    }

    @Test
    void sortedBagOfTicketSerdeWorks(){
        List<SortedBag<Ticket>> allSubLists = sortedBagFromSource(ChMap.tickets());
        List<SortedBag<Ticket>> listOfSortedBagOfTickets = listFromRandomElements(allSubLists);
        testSerde(listOfSortedBagOfTickets, Serdes.SORTEDBAG_OF_TICKET_SERDE);
    }

    @Test
    void sortedBagOfTicketSerdeWorksWithEmptyList(){
        testSerde(List.of(SortedBag.of()), Serdes.SORTEDBAG_OF_TICKET_SERDE);
    }

    @Test
    void listOfSortedBagOfCardSerdeWorks(){
        List<List<SortedBag<Card>>> samples = new ArrayList<>();
        for (int i = 0; i < 10; ++i)
            samples.add(sortedBagFromSource(Card.ALL));
        testSerde(samples, Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE);

    }

    @Test
    void listOfSortedBagOfCardSerdeWorksWithEmptyList(){ //TODO
        testSerde(List.of(List.of(SortedBag.of())), Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE);
    }

    // --------------------------------------------------------------------------------------------------------------------

    @Test
    void publicGameSerdeWorksExampleFromClass() {
        List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PLAYER_2, ps, null);
        // serialized
        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", Serdes.PUBLIC_GAME_STATE_SERDE.serialize(gs));

        // deserialized
        // gameState ticket count is the same
        assertEquals(gs.ticketsCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").ticketsCount());
        // gameState cardState is the same
        assertEquals(gs.cardState().deckSize(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().deckSize());
        assertEquals(gs.cardState().discardsSize(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().discardsSize());
        assertEquals(gs.cardState().faceUpCard(3), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().faceUpCard(3));
        assertEquals(gs.cardState().faceUpCards(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").cardState().faceUpCards());
        // gameState has the same claimed routes
        assertEquals(gs.claimedRoutes(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").claimedRoutes());
        // gameState has the same current player id
        assertEquals(gs.currentPlayerId(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").currentPlayerId());
        // gameState has the same last player id
        assertEquals(gs.lastPlayer(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").lastPlayer());
        // gameState has the same playerState for PLAYER_1
        assertEquals(gs.playerState(PLAYER_1).carCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).carCount());
        assertEquals(gs.playerState(PLAYER_1).routes(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).routes());
        assertEquals(gs.playerState(PLAYER_1).cardCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).cardCount());
        assertEquals(gs.playerState(PLAYER_1).ticketCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).ticketCount());
        assertEquals(gs.playerState(PLAYER_1).claimPoints(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_1).claimPoints());
        // gameState has the same playerState for PLAYER_2
        assertEquals(gs.playerState(PLAYER_2).carCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).carCount());
        assertEquals(gs.playerState(PLAYER_2).routes(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).routes());
        assertEquals(gs.playerState(PLAYER_2).cardCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).cardCount());
        assertEquals(gs.playerState(PLAYER_2).ticketCount(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).ticketCount());
        assertEquals(gs.playerState(PLAYER_2).claimPoints(), Serdes.PUBLIC_GAME_STATE_SERDE.deserialize("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:").playerState(PLAYER_2).claimPoints());
    }

    // -------------------------------------------------------------------------------------------------------------


    //Integer
    @Test
    void integerSerdeWorksOnSerialization(){
        assertEquals("2021", Serdes.INTEGER_SERDE.serialize(2021));
        assertEquals("-2021", Serdes.INTEGER_SERDE.serialize(-2021));
    }

    @Test
    void integerSerdeWorksOnDeserialization(){
        assertEquals(2021, Serdes.INTEGER_SERDE.deserialize("2021"));
        assertEquals(-2021, Serdes.INTEGER_SERDE.deserialize("-2021"));
    }

    //String
    @Test
    void stringSerdeWorksOnSerialization(){
        assertEquals( "Q2hhcmxlcw==", Serdes.STRING_SERDE.serialize("Charles"));
    }

    @Test
    void stringSerdeWorksOnDeserialization(){
        assertEquals( "Charles", Serdes.STRING_SERDE.deserialize("Q2hhcmxlcw=="));
    }

    //PlayerId
    @Test
    void playerIdSerdeWorksOnSerialization(){
        assertEquals( "0", Serdes.PLAYER_ID_SERDE.serialize(PlayerId.PLAYER_1));
        assertEquals( "1", Serdes.PLAYER_ID_SERDE.serialize(PlayerId.PLAYER_2));
    }

    @Test
    void playerIdSerdeWorksOnDeserialization(){
        assertEquals( PlayerId.PLAYER_1, Serdes.PLAYER_ID_SERDE.deserialize("0"));
        assertEquals(PlayerId.PLAYER_2, Serdes.PLAYER_ID_SERDE.deserialize("1"));
    }

    //TurnKind
    @Test
    void turnKindSerdeWorksOnSerialization(){
        assertEquals("0", Serdes.TURN_KIND_SERDE.serialize(Player.TurnKind.DRAW_TICKETS));
        assertEquals("1", Serdes.TURN_KIND_SERDE.serialize(Player.TurnKind.DRAW_CARDS));
        assertEquals("2", Serdes.TURN_KIND_SERDE.serialize(Player.TurnKind.CLAIM_ROUTE));
    }

    @Test
    void turnKindSerdeWorksOnDeserialization(){
        assertEquals(Player.TurnKind.DRAW_TICKETS, Serdes.TURN_KIND_SERDE.deserialize("0"));
        assertEquals(Player.TurnKind.DRAW_CARDS, Serdes.TURN_KIND_SERDE.deserialize("1"));
        assertEquals(Player.TurnKind.CLAIM_ROUTE, Serdes.TURN_KIND_SERDE.deserialize("2"));
    }

    //Card
    @Test
    void cardSerdeWorksOnSerialization(){
        assertEquals("0", Serdes.CARD_SERDE.serialize(Card.BLACK));
        assertEquals("1", Serdes.CARD_SERDE.serialize(Card.VIOLET));
        assertEquals("2", Serdes.CARD_SERDE.serialize(Card.BLUE));
        assertEquals("3", Serdes.CARD_SERDE.serialize(Card.GREEN));
        assertEquals("4", Serdes.CARD_SERDE.serialize(Card.YELLOW));
        assertEquals("5", Serdes.CARD_SERDE.serialize(Card.ORANGE));
        assertEquals("6", Serdes.CARD_SERDE.serialize(Card.RED));
        assertEquals("7", Serdes.CARD_SERDE.serialize(Card.WHITE));
        assertEquals("8", Serdes.CARD_SERDE.serialize(Card.LOCOMOTIVE));
    }

    @Test
    void cardSerdeWorksOnDeserialization(){
        assertEquals(Card.BLACK, Serdes.CARD_SERDE.deserialize("0"));
        assertEquals(Card.VIOLET, Serdes.CARD_SERDE.deserialize("1"));
        assertEquals(Card.BLUE, Serdes.CARD_SERDE.deserialize("2"));
        assertEquals(Card.GREEN, Serdes.CARD_SERDE.deserialize("3"));
        assertEquals(Card.YELLOW, Serdes.CARD_SERDE.deserialize("4"));
        assertEquals(Card.ORANGE, Serdes.CARD_SERDE.deserialize("5"));
        assertEquals(Card.RED, Serdes.CARD_SERDE.deserialize("6"));
        assertEquals(Card.WHITE, Serdes.CARD_SERDE.deserialize("7"));
        assertEquals(Card.LOCOMOTIVE, Serdes.CARD_SERDE.deserialize("8"));
    }

    //Route
    @Test
    void routeSerdeWorksOnSerialization(){
        for (int i = 0; i < ChMap.routes().size(); ++i){

            String expected = String.valueOf(i);
            assertEquals(expected, Serdes.ROUTE_SERDE.serialize(ChMap.routes().get(i)));
        }
    }

    @Test
    void routeSerdeWorksOnDeserialization(){
        for (int i = 0; i < ChMap.routes().size(); ++i){
            Route expected = ChMap.routes().get(i);
            assertEquals(expected, Serdes.ROUTE_SERDE.deserialize(String.valueOf(i)));
        }
    }

    //Ticket
    @Test
    void ticketSerdeWorksOnSerialization(){
        for (int i = 0; i < ChMap.tickets().size(); ++i){
            String expected = String.valueOf(i);
            assertEquals(expected, Serdes.TICKET_SERDE.serialize(ChMap.tickets().get(i)));
            if (i >= 38)
                ++i;
        }
    }

    @Test
    void ticketSerdeWorksOnDeserialization(){
        for (int i = 0; i < ChMap.tickets().size(); ++i){
            Ticket expected = ChMap.tickets().get(i);
            assertEquals(expected, Serdes.TICKET_SERDE.deserialize(String.valueOf(i)));
        }
    }

    //List<String>
    @Test
    void listStringSerdeWorksOnSerialization(){
        //Empty list
        assertEquals("", Serdes.LIST_OF_STRING_SERDE.serialize(List.of()));

        //Non empty list
        List<String> l = List.of("Theo", "Shin", "e s p a c e", "nombres 65");
        String expected = "VGhlbw==,U2hpbg==,ZSBzIHAgYSBjIGU=,bm9tYnJlcyA2NQ==";
        assertEquals(expected, Serdes.LIST_OF_STRING_SERDE.serialize(l));
    }

    @Test
    void listStringSerdeWorksOnDeserialization(){
        //Empty list
        assertEquals(List.of(), Serdes.LIST_OF_STRING_SERDE.deserialize(""));

        //Non empty list
        String data = "VGhlbw==,U2hpbg==,ZSBzIHAgYSBjIGU=,bm9tYnJlcyA2NQ==";
        List<String> expected = List.of("Theo", "Shin", "e s p a c e", "nombres 65");
        assertEquals(expected, Serdes.LIST_OF_STRING_SERDE.deserialize(data));
    }

    //List<Card>
    @Test
    void listCardSerdeWorksOnSerialization(){
        //Empty list
        assertEquals("", Serdes.LIST_OF_CARD_SERDE.serialize(List.of()));

        //Non empty list
        String expected = "0,1,2,3,4,5,6,7,8";
        assertEquals(expected, Serdes.LIST_OF_CARD_SERDE.serialize(Card.ALL));
    }

    @Test
    void listCardSerdeWorksOnDeserialization(){
        //Empty list
        assertEquals(List.of(), Serdes.LIST_OF_CARD_SERDE.deserialize(""));

        //Non empty list
        String data = "0,1,2,3,4,5,6,7,8";
        List<Card> expected = Card.ALL;
        assertEquals(expected, Serdes.LIST_OF_CARD_SERDE.deserialize(data));
    }

    //List<Route>
    @Test
    void listRouteCardSerdeWorksOnSerialization(){
        //Empty list
        assertEquals("", Serdes.LIST_OF_ROUTE_SERDE.serialize(List.of()));

        //Non empty list
        List<String> indexList = new ArrayList<>();
        for (Route r: ChMap.routes())
            indexList.add(String.valueOf(ChMap.routes().indexOf(r)));
        String expected = String.join(",", indexList);
        assertEquals(expected, Serdes.LIST_OF_ROUTE_SERDE.serialize(ChMap.routes()));
    }

    @Test
    void listRouteSerdeWorksOnDeserialization(){
        //Empty list
        assertEquals(List.of(),Serdes.LIST_OF_ROUTE_SERDE.deserialize(""));

        //Non empty list
        List<String> indexList = new ArrayList<>();
        for (Route r: ChMap.routes())
            indexList.add(String.valueOf(ChMap.routes().indexOf(r)));
        String data = String.join(",", indexList);
        List<Route> expected = ChMap.routes();
        assertEquals(expected, Serdes.LIST_OF_ROUTE_SERDE.deserialize(data));
    }


    //SortedBag<Card>
    @Test
    void sortedBagCardSerdeWorksOnSerialization(){
        //Empty bag
        SortedBag<Card> emptyBag = SortedBag.of();
        assertEquals("", Serdes.SORTEDBAG_OF_CARD_SERDE.serialize(emptyBag));

        //Non empty bag
        SortedBag<Card> data = SortedBag.of(Card.ALL);
        List<String> indexList = new ArrayList<>();
        for (Card c: data)
            indexList.add(String.valueOf(Card.ALL.indexOf(c)));
        String expected = String.join(",",indexList);
        assertEquals(expected, Serdes.SORTEDBAG_OF_CARD_SERDE.serialize(data));
    }

    @Test
    void sortedBagCardSerdeWorksOnDeserialization(){
        //Empty bag
        assertEquals(SortedBag.of(), Serdes.SORTEDBAG_OF_CARD_SERDE.deserialize(""));

        //Non empty bag
        List<Card> expectedList = SortedBag.of(Card.ALL).toList();
        List<String> indexList = new ArrayList<>();
        for (Card c: expectedList)
            indexList.add(String.valueOf(Card.ALL.indexOf(c)));
        String data = String.join(",",indexList);
        SortedBag<Card> expected = SortedBag.of(expectedList);
        assertEquals(expected, Serdes.SORTEDBAG_OF_CARD_SERDE.deserialize(data));
    }

    //SortedBag<Ticket>
    @Test
    void sortedBagTicketSerdeWorksOnSerialization(){
        //Empty bag
        SortedBag<Ticket> emptyBag = SortedBag.of();
        assertEquals("", Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(emptyBag));

        //Non empty bagserialize
        SortedBag<Ticket> data = SortedBag.of(ChMap.tickets());
        List<String> indexList = new ArrayList<>();
        for (Ticket c: data)
            indexList.add(String.valueOf(ChMap.tickets().indexOf(c)));
        String expected = String.join(",",indexList);
        assertEquals(expected, Serdes.SORTEDBAG_OF_TICKET_SERDE.serialize(data));
    }

    @Test
    void sortedBagTicketSerdeWorksOnDeserialization(){
        //Empty bag
        assertEquals(SortedBag.of(), Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(""));

        //Non empty bag
        List<Ticket> expectedList = SortedBag.of(ChMap.tickets()).toList();
        List<String> indexList = new ArrayList<>();
        for (Ticket c: expectedList)
            indexList.add(String.valueOf(ChMap.tickets().indexOf(c)));
        String data = String.join(",",indexList);
        SortedBag<Ticket> expected = SortedBag.of(expectedList);
        assertEquals(expected, Serdes.SORTEDBAG_OF_TICKET_SERDE.deserialize(data));

    }

    //List<SortedBag<Card>>
    @Test
    void listSortedBagCardSerdeWorksOnSerialization(){
        //Empty list
        assertEquals("", Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE.serialize(List.of()));

        //Non empty list
        List<SortedBag<Card>> data = List.of(
                SortedBag.of(2, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.WHITE),
                SortedBag.of(1, Card.YELLOW, 1, Card.ORANGE)
        );
        List<String> indexList = new ArrayList<>();
        for (SortedBag<Card> s: data){
            List<String> indexListForBag = new ArrayList<>();
            for (Card c: s.toList())
                indexListForBag.add(String.valueOf(Card.ALL.indexOf(c)));
            indexList.add(String.join(",", indexListForBag));
        }
        String expected = String.join(";", indexList);

        assertEquals(expected, Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE.serialize(data));
    }

    @Test
    void listSortedBagCardSerdeWorksOnDeserialization(){
        //Empty list
        assertEquals(List.of(), Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE.deserialize(""));

        //Non empty list
        List<SortedBag<Card>> expected = List.of(
                SortedBag.of(2, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.WHITE),
                SortedBag.of(1, Card.YELLOW, 1, Card.ORANGE)
        );
        List<String> indexList = new ArrayList<>();
        for (SortedBag<Card> s: expected){
            List<String> indexListForBag = new ArrayList<>();
            for (Card c: s.toList())
                indexListForBag.add(String.valueOf(Card.ALL.indexOf(c)));
            indexList.add(String.join(",", indexListForBag));
        }
        String data = String.join(";", indexList);

        assertEquals(expected, Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE.deserialize(data));
    }

    //PublicCardState
    @Test
    void publicCardStateSerdeWorksOnSerializationEmptyCase(){
        //Null attributes
        List<Card> facedUpCard = List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.GREEN, Card.VIOLET);
        int deckSize = 0;
        int discardSize = 0;
        PublicCardState data = new PublicCardState(facedUpCard,deckSize,discardSize);
        String expected = "2,6,8,3,1;0;0";
        assertEquals(expected, Serdes.PUBLIC_CARD_STATE_SERDE.serialize(data));
    }

    @Test
    void publicCardStateSerdeWorksOnSerializationSimpleCase(){
        //Non null example
        List<Card> facedUpCard = List.of(Card.BLACK, Card.WHITE, Card.BLACK, Card.GREEN, Card.ORANGE);
        int deckSize = 26;
        int discardSize = 6;
        PublicCardState data = new PublicCardState(facedUpCard, deckSize, discardSize);
        String expected = "0,7,0,3,5;26;6";
        assertEquals(expected, Serdes.PUBLIC_CARD_STATE_SERDE.serialize(data));
    }

    @Test
    void publicCardStateSerdeWorksOnDeserializationEmptyCase(){
        //Null attributes
        List<Card> facedUpCard = List.of(Card.ORANGE, Card.RED, Card.LOCOMOTIVE, Card.YELLOW, Card.VIOLET);
        String data = "5,6,8,4,1;0;0";
        PublicCardState expected = new PublicCardState(facedUpCard, 0, 0);
        PublicCardState actual = Serdes.PUBLIC_CARD_STATE_SERDE.deserialize(data);
        assertEquals(expected.deckSize(), actual.deckSize());
        assertEquals(expected.discardsSize(), actual.discardsSize());

        List<Card> actualFacedUpCards = List.of(
                actual.faceUpCard(0),
                actual.faceUpCard(1),
                actual.faceUpCard(2),
                actual.faceUpCard(3),
                actual.faceUpCard(4)
        );
        assertEquals(facedUpCard, actualFacedUpCards);
    }

    @Test
    void publicCardStateSerdeWorksOnDeserializationSimpleCase(){
        //Non null example
        List<Card> facedUpCard = List.of(Card.RED, Card.BLUE, Card.BLACK, Card.LOCOMOTIVE, Card.ORANGE);
        int deckSize = 26;
        int discardSize = 6;
        String data = "6,2,0,8,5;26;6";
        PublicCardState expected = new PublicCardState(facedUpCard, deckSize, discardSize);
        PublicCardState actual = Serdes.PUBLIC_CARD_STATE_SERDE.deserialize(data);
        assertEquals(facedUpCard, List.of(
                actual.faceUpCard(0),
                actual.faceUpCard(1),
                actual.faceUpCard(2),
                actual.faceUpCard(3),
                actual.faceUpCard(4)
        ));
        assertEquals(expected.deckSize(), actual.deckSize());
        assertEquals(expected.discardsSize(), actual.discardsSize());
    }

    //PublicPlayerState
    @Test
    void publicPlayerStateSerdeWorksOnSerializationEmptyCase(){
        //Null attributes
        int ticketCount = 0;
        int cardCount = 0;
        List<Route> routes = List.of();
        PublicPlayerState data = new PublicPlayerState(ticketCount, cardCount, routes);
        String expected = "0;0;";
        assertEquals(expected, Serdes.PUBLIC_PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void publicPlayerStateSerdeWorksOnSerializationSimpleCase(){
        //Some routes
        int ticketCount = 3;
        int cardCount = 12;
        List<Route> routes = List.of(ChMap.routes().get(5), ChMap.routes().get(61), ChMap.routes().get(12), ChMap.routes().get(72));
        PublicPlayerState data = new PublicPlayerState(ticketCount, cardCount, routes);
        String expected = "3;12;5,61,12,72";
        assertEquals(expected, Serdes.PUBLIC_PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void publicPlayerStateSerdeWorksOnSerializationExtremeCase(){
        //All routes
        int ticketCount = 11;
        int cardCount = 5;
        List<Route> routes = ChMap.routes();
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: ChMap.routes())
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String indexRoutes = String.join(",", indexListRoutes);
        PublicPlayerState data = new PublicPlayerState(ticketCount, cardCount, routes);
        String expected = new StringBuilder("11;5;")
                .append(indexRoutes)
                .toString();
        assertEquals(expected, Serdes.PUBLIC_PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void publicPlayerStateSerdeWorksOnDeserializationEmptyCase(){
        //Null attributes
        int ticketCount = 0;
        int cardCount = 0;
        List<Route> routes = List.of();
        String data = "0;0;";
        PublicPlayerState expected = new PublicPlayerState(ticketCount, cardCount, routes);
        PublicPlayerState actual = Serdes.PUBLIC_PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.ticketCount(), actual.ticketCount());
        assertEquals(expected.cardCount(),  actual.cardCount());
        assertEquals(expected.routes(), actual.routes());
    }

    @Test
    void publicPlayerStateSerdeWorksOnDeserializationSimpleCase(){
        //Some routes
        int ticketCount = 3;
        int cardCount = 12;
        List<Route> routes = List.of(ChMap.routes().get(5), ChMap.routes().get(61), ChMap.routes().get(12), ChMap.routes().get(72));
        String data = "3;12;5,61,12,72";
        PublicPlayerState expected = new PublicPlayerState(ticketCount, cardCount, routes);
        PublicPlayerState actual = Serdes.PUBLIC_PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.ticketCount(), actual.ticketCount());
        assertEquals(expected.cardCount(), actual.cardCount());
    }

    @Test
    void publicPlayerStateSerdeWorksOnDeserializationExtremeCase(){
        //All routes
        int ticketCount = 11;
        int cardCount = 12;
        List<Route> routes = ChMap.routes();
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: ChMap.routes())
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String indexRoutes = String.join(",", indexListRoutes);
        String data = new StringBuilder("11;12;")
                .append(indexRoutes)
                .toString();
        PublicPlayerState expected = new PublicPlayerState(ticketCount, cardCount, routes);
        PublicPlayerState actual = Serdes.PUBLIC_PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.ticketCount(), actual.ticketCount());
        assertEquals(expected.cardCount(), actual.cardCount());
        assertEquals(expected.routes(), actual.routes());
    }

    //PlayerState
    @Test
    void playerStateSerdeWorksOnSerializationEmptyCase() {
        //with empty attributes
        SortedBag<Ticket> tickets = SortedBag.of();
        SortedBag<Card> cards = SortedBag.of();
        List<Route> routes = List.of();
        PlayerState data = new PlayerState(tickets, cards, routes);
        String expected = ";;";
        assertEquals(expected, Serdes.PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void playerStateSerdeWorksOnSerializationSimpleCase(){
        //With non empty attributes
        List<Ticket> ticketsBuffer = List.of(
                ChMap.tickets().get(1),
                ChMap.tickets().get(14),
                ChMap.tickets().get(9),
                ChMap.tickets().get(22),
                ChMap.tickets().get(19),
                ChMap.tickets().get(2)
        );
        SortedBag<Ticket> tickets = SortedBag.of(ticketsBuffer);
        SortedBag<Card> cards = SortedBag.of(1, Card.WHITE, 1, Card.RED);
        List<Route> routes = List.of(
                ChMap.routes().get(5),
                ChMap.routes().get(14),
                ChMap.routes().get(9),
                ChMap.routes().get(25),
                ChMap.routes().get(39),
                ChMap.routes().get(2),
                ChMap.routes().get(66)
        );
        List<String> indexListTickets = new ArrayList<>();
        for (Ticket t: tickets.toList())
            indexListTickets.add(String.valueOf(ChMap.tickets().indexOf(t)));
        List<String> indexListCards = new ArrayList<>();
        for (Card c: cards.toList())
            indexListCards.add(String.valueOf(Card.ALL.indexOf(c)));
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: routes)
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String strIndexTickets = String.join(",", indexListTickets);
        String strIndexCards = String.join(",", indexListCards);
        String strIndexRoutes = String.join(",", indexListRoutes);

        PlayerState data = new PlayerState(tickets, cards, routes);
        String expected = String.join(";", List.of(strIndexTickets, strIndexCards, strIndexRoutes));
        assertEquals(expected, Serdes.PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void playerStateSerdeWorksOnSerializationExtremeCase(){
        //With full list attributes
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        SortedBag<Card> cards = SortedBag.of(Card.ALL);
        List<Route> routes = ChMap.routes();
        List<String> indexListTickets = new ArrayList<>();
        for (Ticket t: tickets.toList())
            indexListTickets.add(String.valueOf(ChMap.tickets().indexOf(t)));
        List<String> indexListCards = new ArrayList<>();
        for (Card c: cards.toList())
            indexListCards.add(String.valueOf(Card.ALL.indexOf(c)));
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: routes)
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String strIndexTickets = String.join(",", indexListTickets);
        String strIndexCards = String.join(",", indexListCards);
        String strIndexRoutes = String.join(",", indexListRoutes);

        PlayerState data = new PlayerState(tickets, cards, routes);
        String expected = String.join(";", List.of(strIndexTickets, strIndexCards, strIndexRoutes));
        assertEquals(expected, Serdes.PLAYER_STATE_SERDE.serialize(data));
    }

    @Test
    void playerStateSerdeWorksOnDeserializationEmptyCase(){
        //with empty attributes
        SortedBag<Ticket> tickets = SortedBag.of();
        SortedBag<Card> cards = SortedBag.of();
        List<Route> routes = List.of();
        String data = ";;";
        PlayerState expected = new PlayerState(tickets, cards, routes);
        PlayerState actual = Serdes.PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.tickets(), actual.tickets());
        assertEquals(expected.cards(), actual.cards());
        assertEquals(expected.routes(), actual.routes());
    }

    @Test
    void playerStateSerdeWorksOnDeserializationSimpleCase(){
        //With non empty attributes
        List<Ticket> ticketsBuffer = List.of(
                ChMap.tickets().get(0),
                ChMap.tickets().get(15),
                ChMap.tickets().get(10),
                ChMap.tickets().get(24),
                ChMap.tickets().get(3),
                ChMap.tickets().get(9)
        );
        SortedBag<Ticket> tickets = SortedBag.of(ticketsBuffer);
        SortedBag<Card> cards = SortedBag.of(1, Card.BLACK, 1, Card.LOCOMOTIVE);
        List<Route> routes = List.of(
                ChMap.routes().get(40),
                ChMap.routes().get(1),
                ChMap.routes().get(59),
                ChMap.routes().get(3),
                ChMap.routes().get(33),
                ChMap.routes().get(23),
                ChMap.routes().get(71)
        );
        List<String> indexListTickets = new ArrayList<>();
        for (Ticket t: tickets.toList())
            indexListTickets.add(String.valueOf(ChMap.tickets().indexOf(t)));
        List<String> indexListCards = new ArrayList<>();
        for (Card c: cards.toList())
            indexListCards.add(String.valueOf(Card.ALL.indexOf(c)));
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: routes)
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String strIndexTickets = String.join(",", indexListTickets);
        String strIndexCards = String.join(",", indexListCards);
        String strIndexRoutes = String.join(",", indexListRoutes);

        String data = String.join(";", List.of(strIndexTickets, strIndexCards, strIndexRoutes));
        PlayerState expected = new PlayerState(tickets, cards, routes);
        PlayerState actual = Serdes.PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.tickets(), actual.tickets());
        assertEquals(expected.cards(), actual.cards());
        assertEquals(expected.routes(), actual.routes());
    }

    @Test
    void playerStateSerdeWorksOnDeserializationExtremeCase(){
        //With full list attributes
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());
        SortedBag<Card> cards = SortedBag.of(Card.ALL);
        List<Route> routes = ChMap.routes();
        List<String> indexListTickets = new ArrayList<>();
        for (Ticket t: tickets.toList())
            indexListTickets.add(String.valueOf(ChMap.tickets().indexOf(t)));
        List<String> indexListCards = new ArrayList<>();
        for (Card c: cards.toList())
            indexListCards.add(String.valueOf(Card.ALL.indexOf(c)));
        List<String> indexListRoutes = new ArrayList<>();
        for (Route r: routes)
            indexListRoutes.add(String.valueOf(ChMap.routes().indexOf(r)));
        String strIndexTickets = String.join(",", indexListTickets);
        String strIndexCards = String.join(",", indexListCards);
        String strIndexRoutes = String.join(",", indexListRoutes);
        String data = String.join(";", strIndexTickets, strIndexCards, strIndexRoutes);
        PlayerState expected = new PlayerState(tickets, cards, routes);
        PlayerState actual = Serdes.PLAYER_STATE_SERDE.deserialize(data);
        assertEquals(expected.tickets(), actual.tickets());
        assertEquals(expected.cards(), actual.cards());
        assertEquals(expected.routes(), actual.routes());
    }

    //PublicGameState
    @Test
    void publicGameStateSerdeWorksOnSerializationEmptyCase(){
        ///Null attributes and empty lists

        //ticketCount
        int ticketsCount = 0;
        //cardState
        List<Card> facedUpCard = List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.GREEN, Card.VIOLET);
        PublicCardState cardState = new PublicCardState(facedUpCard,0,0);
        String cardStateString = "2,6,8,3,1;0;0";
        //currentPlayerId
        PlayerId currentPlayerId = PlayerId.PLAYER_1;
        //playerState(PLAYER_1) && playerState(PLAYER_2)
        List<Route> routes = List.of();
        PublicPlayerState playerState1 = new PublicPlayerState(0, 0, routes);
        PublicPlayerState playerState2 = new PublicPlayerState(0, 0, routes);
        Map<PlayerId, PublicPlayerState> playerState = Map.of(
                PlayerId.PLAYER_1, playerState1,
                PlayerId.PLAYER_2, playerState2
        );
        String playerStateString = "0;0;";
        //lastPlayer--> null
        PlayerId lastPlayer = null;

        PublicGameState data = new PublicGameState(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
        List<String> expectedList = List.of("0", cardStateString, "0", playerStateString, playerStateString, "");
        String expected = String.join(":", expectedList);
        assertEquals(expected, Serdes.PUBLIC_GAME_STATE_SERDE.serialize(data));
    }

    @Test
    void publicGameStateSerdeWorksOnSerializationSimpleCase(){
        ///Example given in doc !

        //ticketsCount
        int ticketsCount = 40;
        List<Card> facedUpCards = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cardState = new PublicCardState(facedUpCards, 30, 31);
        List<Route> routes = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, routes),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of())
        );

        PublicGameState data = new PublicGameState(ticketsCount, cardState, PlayerId.PLAYER_2, ps, null);
        String expected = "40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:";
        assertEquals(expected, Serdes.PUBLIC_GAME_STATE_SERDE.serialize(data));
    }

    @Test
    void publicGameStateSerdeWorksOnDeserializationEmptyCase(){
        ///Null attributes and empty lists

        //ticketCount
        int ticketsCount = 0;
        //cardState
        List<Card> facedUpCard = List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.GREEN, Card.VIOLET);
        PublicCardState cardState = new PublicCardState(facedUpCard,0,0);
        String cardStateString = "2,6,8,3,1;0;0";
        //currentPlayerId
        PlayerId currentPlayerId = PlayerId.PLAYER_1;
        //playerState(PLAYER_1) && playerState(PLAYER_2)
        List<Route> routes = List.of();
        PublicPlayerState playerState1 = new PublicPlayerState(0, 0, routes);
        PublicPlayerState playerState2 = new PublicPlayerState(0, 0, routes);
        Map<PlayerId, PublicPlayerState> playerState = Map.of(
                PlayerId.PLAYER_1, playerState1,
                PlayerId.PLAYER_2, playerState2
        );
        String playerStateString = "0;0;";
        //lastPlayer--> null
        PlayerId lastPlayer = null;

        List<String> dataList = List.of("0", cardStateString, "0", playerStateString, playerStateString, "");
        String data = String.join(":", dataList);
        PublicGameState expected = new PublicGameState(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
        PublicGameState actual = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(data);

        //ticketsCount test
        assertEquals(expected.ticketsCount(), actual.ticketsCount());
        //cardState test
        assertEquals(facedUpCard, List.of(
                actual.cardState().faceUpCard(0),
                actual.cardState().faceUpCard(1),
                actual.cardState().faceUpCard(2),
                actual.cardState().faceUpCard(3),
                actual.cardState().faceUpCard(4)
        ));
        assertEquals(expected.cardState().deckSize(), actual.cardState().deckSize());
        assertEquals(expected.cardState().discardsSize(), actual.cardState().discardsSize());
        //currentPlayer test
        assertEquals(expected.currentPlayerId(), actual.currentPlayerId());
        //playerState
        assertEquals(expected.playerState(PlayerId.PLAYER_1).ticketCount(), actual.playerState(PlayerId.PLAYER_1).ticketCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).ticketCount(), actual.playerState(PlayerId.PLAYER_2).ticketCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_1).cardCount(), actual.playerState(PlayerId.PLAYER_1).cardCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).cardCount(), actual.playerState(PlayerId.PLAYER_2).cardCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_1).routes(), actual.playerState(PlayerId.PLAYER_1).routes());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).routes(), actual.playerState(PlayerId.PLAYER_2).routes());
        //lastPlayer
        assertEquals(expected.lastPlayer(), actual.lastPlayer());
    }

    @Test
    void publicGameStateSerdeWorksOnDeserializationSimpleCase(){
        ///Example given in doc !

        //ticketsCount
        int ticketsCount = 40;
        List<Card> facedUpCards = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
        PublicCardState cardState = new PublicCardState(facedUpCards, 30, 31);
        List<Route> routes = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, routes),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of())
        );

        String data = "40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:";
        PublicGameState expected = new PublicGameState(ticketsCount, cardState, PlayerId.PLAYER_2, ps, null);
        PublicGameState actual = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(data);
        //ticketsCount test
        assertEquals(expected.ticketsCount(), actual.ticketsCount());
        //cardState test
        assertEquals(facedUpCards, List.of(
                actual.cardState().faceUpCard(0),
                actual.cardState().faceUpCard(1),
                actual.cardState().faceUpCard(2),
                actual.cardState().faceUpCard(3),
                actual.cardState().faceUpCard(4)
        ));
        assertEquals(expected.cardState().deckSize(), actual.cardState().deckSize());
        assertEquals(expected.cardState().discardsSize(), actual.cardState().discardsSize());
        //currentPlayer test
        assertEquals(expected.currentPlayerId(), actual.currentPlayerId());
        //playerState
        assertEquals(expected.playerState(PlayerId.PLAYER_1).ticketCount(), actual.playerState(PlayerId.PLAYER_1).ticketCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).ticketCount(), actual.playerState(PlayerId.PLAYER_2).ticketCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_1).cardCount(), actual.playerState(PlayerId.PLAYER_1).cardCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).cardCount(), actual.playerState(PlayerId.PLAYER_2).cardCount());
        assertEquals(expected.playerState(PlayerId.PLAYER_1).routes(), actual.playerState(PlayerId.PLAYER_1).routes());
        assertEquals(expected.playerState(PlayerId.PLAYER_2).routes(), actual.playerState(PlayerId.PLAYER_2).routes());
        //lastPlayer
        assertEquals(expected.lastPlayer(), actual.lastPlayer());
    }

    // ----------------------------------------------------------------------------------------------------------------

    private final static int TRY_COUNT = 100;

    private static int randomSize(int bound)
    {
        return new Random().nextInt(bound - 1) + 1;
    }

    private static void comparePublicPlayerState(PublicPlayerState expected, PublicPlayerState actual)
    {
        assertEquals(expected.carCount(), actual.carCount());
        assertEquals(expected.cardCount(), actual.cardCount());
        assertEquals(expected.claimPoints(), actual.claimPoints());
        assertEquals(expected.routes(), actual.routes());
        assertEquals(expected.ticketCount(), actual.ticketCount());
    }
    private static void comparePlayerState(PlayerState expected, PlayerState actual)
    {
        comparePublicPlayerState(expected, actual);
        assertEquals(expected.cards(), actual.cards());
        assertEquals(expected.tickets(), actual.tickets());
        assertEquals(expected.routes(), actual.routes());
        assertEquals(expected.finalPoints(), actual.finalPoints());
        assertEquals(expected.ticketPoints(), actual.ticketPoints());
    }

    private static void comparePublicCardState(PublicCardState expected, PublicCardState actual)
    {
        assertEquals(expected.deckSize(), actual.deckSize());
        assertEquals(expected.discardsSize(), actual.discardsSize());
        assertEquals(expected.faceUpCards(), actual.faceUpCards());
        assertEquals(expected.totalSize(), actual.totalSize());
        assertEquals(expected.isDeckEmpty(), actual.isDeckEmpty());
    }
    private static Card randomCard()
    {
        return Card.ALL.get(new Random().nextInt(Card.COUNT));
    }
    private static Ticket randomTicket()
    {
        return ChMap.tickets().get(new Random().nextInt(ChMap.tickets().size()));
    }
    private static Route randomRoute()
    {
        return ChMap.routes().get(new Random().nextInt(ChMap.routes().size()));
    }

    private static PublicPlayerState randomPublicPlayerState()
    {
        Random rng = new Random();
        return new PublicPlayerState(rng.nextInt(30), rng.nextInt(30), randomRouteList(15));
    }
    private static PublicCardState randomPublicCardState()
    {
        Random rng = new Random();
        return new PublicCardState(randomFaceUpCards(), rng.nextInt(110), rng.nextInt(110));
    }

    private static List<Card> randomCardList(int maxSize)
    {
        List<Card> list = new ArrayList<>();

        int listSize = randomSize(100);
        for (int j = 0; j < listSize; ++j) list.add(randomCard());

        return list;
    }
    private static List<Card> randomFaceUpCards()
    {
        List<Card> list = new ArrayList<>();

        for (int j = 0; j < 5; ++j) list.add(randomCard());

        return list;
    }
    private static SortedBag<Card> randomSortedBagCard(int maxSize)
    {
        return SortedBag.of(randomCardList(maxSize));
    }
    private static List<SortedBag<Card>> randomListSortedBagCard(int maxListSize, int maxSortedBagSize)
    {
        List<SortedBag<Card>> list = new ArrayList<>();

        int listSize = randomSize(maxListSize);
        for (int j = 0; j < listSize; ++j) list.add(randomSortedBagCard(maxSortedBagSize));

        return list;
    }

    private static SortedBag<Ticket> randomSortedBagTicket(int maxSize)
    {
        SortedBag.Builder<Ticket> sbb = new SortedBag.Builder<>();

        int bagSize = randomSize(maxSize);
        for (int j = 0; j < bagSize; ++j) sbb.add(randomTicket());

        return sbb.build();
    }

    private static List<Route> randomRouteList(int maxSize)
    {
        List<Route> list = new ArrayList<>();

        int listSize = randomSize(100);
        for (int j = 0; j < listSize; ++j) list.add(randomRoute());

        return list;
    }
    private static PlayerId randomLastPlayer()
    {
        int pINumber = new Random().nextInt(PlayerId.COUNT + 1);
        return pINumber == PlayerId.COUNT ? null : PlayerId.ALL.get(pINumber);
    }


    @Test
    void integer()
    {
        Random rng = new Random();
        Serde<Integer> serde = Serdes.INTEGER_SERDE;

        for (int i = 0; i < TRY_COUNT; ++i)
        {
            Integer integer = rng.nextInt();
            assertEquals(integer, serde.deserialize(serde.serialize(integer)));
        }
    }

    @Test
    void stringSerde()
    {
        Random rng = new Random();
        Serde<String> serde = Serdes.STRING_SERDE;

        for (int i = 0; i < TRY_COUNT; ++i)
        {
            int size = rng.nextInt(100);

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < size; ++j)
                sb.append((char) (byte) rng.nextInt());

            String s = sb.toString();

            assertEquals(s, serde.deserialize(serde.serialize(s)));
        }
    }

    @Test
    void playerId()
    {
        Serde<PlayerId> serde = Serdes.PLAYER_ID_SERDE;

        for (var id : PlayerId.ALL) assertEquals(id, serde.deserialize(serde.serialize(id)));
    }

    @Test
    void turnKind()
    {
        Serde<Player.TurnKind> serde = Serdes.TURN_KIND_SERDE;

        for (var id : Player.TurnKind.ALL) assertEquals(id, serde.deserialize(serde.serialize(id)));
    }

    @Test
    void card()
    {
        Serde<Card> serde = Serdes.CARD_SERDE;

        for (var id : Card.ALL) assertEquals(id, serde.deserialize(serde.serialize(id)));
    }

    @Test
    void route()
    {
        Serde<Route> serde = Serdes.ROUTE_SERDE;

        for (var r : ChMap.routes()) assertEquals(r, serde.deserialize(serde.serialize(r)));
    }

    @Test
    void ticket()
    {
        var serde = Serdes.TICKET_SERDE;

        for (var t : ChMap.tickets()) assertEquals(t, serde.deserialize(serde.serialize(t)));
    }

    @Test
    void listString()
    {
        Random rng = new Random();
        List<String> list = new ArrayList<>();
        var serde = Serdes.LIST_OF_STRING_SERDE;

        for (int i = 0; i < TRY_COUNT; ++i)
        {
            list.clear();

            int listSize = randomSize(100);
            for (int j = 0; j < listSize; ++j)
            {
                int stringSize = randomSize(100);

                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < stringSize; ++k)
                    sb.append((char) (byte) rng.nextInt());

                list.add(sb.toString());
            }

            assertEquals(list, serde.deserialize(serde.serialize(list)));
        }
    }

    @Test
    void listCard()
    {
        Random rng = new Random();
        var serde = Serdes.LIST_OF_CARD_SERDE;

        for (int i = 0; i < TRY_COUNT; ++i)
        {
            var list = randomCardList(100);
            assertEquals(list, serde.deserialize(serde.serialize(list)));
        }
    }

    @Test
    void listRoute()
    {
        Random rng = new Random();
        var serde = Serdes.LIST_OF_ROUTE_SERDE;

        for (int i = 0; i < TRY_COUNT; ++i)
        {
            var list = randomRouteList(100);
            assertEquals(list, serde.deserialize(serde.serialize(list)));
        }
    }

    @Test
    void sortedBagCard()
    {
        Random rng = new Random();
        var serde = Serdes.SORTEDBAG_OF_CARD_SERDE;

        for (int i = 0; i < TRY_COUNT; ++i)
        {

            var sb = randomSortedBagCard(100);
            assertEquals(sb, serde.deserialize(serde.serialize(sb)));
        }
    }

    @Test
    void sortedBagTicket()
    {
        Random rng = new Random();
        var serde = Serdes.SORTEDBAG_OF_TICKET_SERDE;

        for (int i = 0; i < TRY_COUNT; ++i)
        {
            var sb = randomSortedBagTicket(100);
            assertEquals(sb, serde.deserialize(serde.serialize(sb)));
        }
    }

    @Test
    void listSortedBagCard()
    {
        Random rng = new Random();
        var serde = Serdes.LIST_OF_SORTEDBAG_OF_CARD_SERDE;

        for (int i = 0; i < TRY_COUNT; ++i)
        {
            var list = randomListSortedBagCard(100, 100);
            assertEquals(list, serde.deserialize(serde.serialize(list)));
        }
    }

    @Test
    void publicCardState()
    {
        var s = Serdes.PUBLIC_CARD_STATE_SERDE;
        Random rng = new Random();
        for (int i = 0; i < TRY_COUNT; ++i)
        {
            PublicCardState pcs = randomPublicCardState();
            comparePublicCardState(pcs, s.deserialize(s.serialize(pcs)));
        }


    }
    @Test
    void publicPlayerState()
    {
        var s = Serdes.PUBLIC_PLAYER_STATE_SERDE;
        Random rng = new Random();
        for (int i = 0; i < TRY_COUNT; ++i)
        {
            PublicPlayerState pps = randomPublicPlayerState();
            comparePublicPlayerState(pps, s.deserialize(s.serialize(pps)));
        }
    }
    @Test
    void playerState()
    {
        var s = Serdes.PLAYER_STATE_SERDE;
        for (int i = 0; i < TRY_COUNT; ++i)
        {
            PlayerState ps = new PlayerState(randomSortedBagTicket(15), randomSortedBagCard(30), randomRouteList(15));

            comparePlayerState(ps, s.deserialize(s.serialize(ps)));
        }
    }
    @Test
    void publicGameState()
    {
        var s = Serdes.PUBLIC_GAME_STATE_SERDE;
        Random rng = new Random();
        for (int i = 0; i < TRY_COUNT; ++i)
        {
            Map<PlayerId, PublicPlayerState> playerStateMap = new EnumMap<>(PlayerId.class);
            playerStateMap.put(PlayerId.PLAYER_1, randomPublicPlayerState());
            playerStateMap.put(PlayerId.PLAYER_2, randomPublicPlayerState());
            PublicGameState pgs = new PublicGameState(rng.nextInt(30), randomPublicCardState(), PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT - 1)), playerStateMap, randomLastPlayer());

            comparePublicCardState(pgs.cardState(), s.deserialize(s.serialize(pgs)).cardState());

            comparePublicPlayerState(pgs.playerState(PlayerId.PLAYER_1), s.deserialize(s.serialize(pgs)).playerState(PlayerId.PLAYER_1));
            comparePublicPlayerState(pgs.playerState(PlayerId.PLAYER_2), s.deserialize(s.serialize(pgs)).playerState(PlayerId.PLAYER_2));
            comparePublicPlayerState(pgs.currentPlayerState(), s.deserialize(s.serialize(pgs)).currentPlayerState());

            assertEquals(pgs.lastPlayer(), s.deserialize(s.serialize(pgs)).lastPlayer());
            if (pgs.lastPlayer() != null)
                comparePublicPlayerState(pgs.playerState(pgs.lastPlayer()), s.deserialize(s.serialize(pgs)).playerState(pgs.lastPlayer()));

            assertEquals(pgs.currentPlayerId(), s.deserialize(s.serialize(pgs)).currentPlayerId());
            assertEquals(pgs.claimedRoutes(), s.deserialize(s.serialize(pgs)).claimedRoutes());
            assertEquals(pgs.ticketsCount(), s.deserialize(s.serialize(pgs)).ticketsCount());
            assertEquals(pgs.lastPlayer(), s.deserialize(s.serialize(pgs)).lastPlayer());
        }
    }
}
