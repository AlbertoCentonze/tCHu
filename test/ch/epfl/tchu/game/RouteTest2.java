package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RouteTest2 {
    @Test
    void colorNull(){
        Route test =new Route("test", new Station(0,"1"), new Station(1,"2"), 2, Route.Level.UNDERGROUND,null);
        assertNull(test.color());
    }

    @Test
    void stationTest(){
        ArrayList<Station> right = new ArrayList<>();
        right.add(new Station(21,"LER"));
        right.add(new Station(5,"bfd"));
        Route test =new Route("test", new Station(21,"LER"), new Station(5,"bfd"), 5, Route.Level.OVERGROUND,null);
        assertEquals(right.get(0).id(), test.stations().get(0).id());
        assertEquals(right.get(0).name(), test.stations().get(0).name());
        assertEquals(right.get(1).id(), test.stations().get(1).id());
        assertEquals(right.get(1).name(), test.stations().get(1).name());
    }

    @Test
    void oppositeStationtest(){
        Route test =new Route("test", new Station(21,"LER"), new Station(5,"bfd"), 5, Route.Level.OVERGROUND,null);

        assertEquals(test.station1(),test.stationOpposite(test.station2()));
    }

    @Test
    void errorOnAdditionalCardsCount(){
        Route test1 =new Route("test", new Station(21,"LER"), new Station(5,"bfd"), 5, Route.Level.OVERGROUND,null);
        assertThrows(IllegalArgumentException.class, () -> {
            test1.additionalClaimCardsCount(null,null);
        });

        Route test2 =new Route("test", new Station(21,"LER"), new Station(5,"bfd"), 5, Route.Level.UNDERGROUND,null);
        SortedBag<Card> claim = SortedBag.of(3, Card.BLUE);
        SortedBag<Card> drawn1 = SortedBag.of(4, Card.BLUE);
        SortedBag<Card> drawn2 = SortedBag.of(1, Card.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            test2.additionalClaimCardsCount(claim,drawn1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            test2.additionalClaimCardsCount(claim,drawn2);
        });

    }

    @Test
    void possibleClaimCardsOnUsualNeutralTunnel(){
        Route test =new Route("test", new Station(0,"1"), new Station(1,"2"), 2, Route.Level.UNDERGROUND,null);
        System.out.println(test.possibleClaimCards());
    }

    @Test
    void possibleClaimCardsOnUsualColoredTunnel(){
        Route test =new Route("test", new Station(0,"1"), new Station(1,"2"), 6, Route.Level.UNDERGROUND,Color.RED);
        System.out.println(test.possibleClaimCards());
    }

    @Test
    void additionalClaimCardsCountOnUsual(){
        Route test =new Route("test", new Station(0,"1"), new Station(1,"2"), 2, Route.Level.UNDERGROUND,null);
        SortedBag<Card> claim = SortedBag.of(4, Card.BLUE);
        SortedBag.Builder<Card> card = new SortedBag.Builder<>();
        card.add(1,Card.LOCOMOTIVE);
        card.add(1,Card.RED);
        card.add(1,Card.BLUE);
        SortedBag<Card> drawn = card.build();
        assertEquals(2, test.additionalClaimCardsCount(claim, drawn));
    }

    @Test
    void additionalClaimCardsCountOnlyOnLoc(){
        Route test =new Route("test", new Station(0,"1"), new Station(1,"2"), 2, Route.Level.UNDERGROUND,null);
        SortedBag<Card> claim = SortedBag.of(6, Card.BLUE);
        SortedBag.Builder<Card> card = new SortedBag.Builder<>();
        card.add(3,Card.LOCOMOTIVE);
        SortedBag<Card> drawn = card.build();
        assertEquals(3, test.additionalClaimCardsCount(claim, drawn));
    }

    @Test
    void additionalClaimCardsCountOnSame(){
        Route test =new Route("test", new Station(0,"1"), new Station(1,"2"), 2, Route.Level.UNDERGROUND,null);
        SortedBag<Card> claim = SortedBag.of(5, Card.BLUE);
        SortedBag.Builder<Card> card = new SortedBag.Builder<>();
        card.add(3,Card.BLUE);
        SortedBag<Card> drawn = card.build();
        assertEquals(3, test.additionalClaimCardsCount(claim, drawn));
    }

    @Test
    void additionalClaimCardsCountOnZeroOcc(){
        Route test =new Route("test", new Station(0,"1"), new Station(1,"2"), 2, Route.Level.UNDERGROUND,null);
        SortedBag<Card> claim = SortedBag.of(3, Card.BLUE);
        SortedBag.Builder<Card> card = new SortedBag.Builder<>();
        card.add(3,Card.RED);
        SortedBag<Card> drawn = card.build();
        assertEquals(0, test.additionalClaimCardsCount(claim, drawn));
    }
    //Tester list.of sur station()

    @Test
    void testStation(){
        Route test =new Route("test", new Station(0,"1"), new Station(1,"2"), 2, Route.Level.UNDERGROUND,null);
        assertEquals(2,test.stations().size());
    }

}
