package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class TestServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (
            ServerSocket serverSocket = new ServerSocket(5108);
            Socket socket = serverSocket.accept()) {
                Player playerProxy = new RemotePlayerProxy(socket);
                // testing initPlayers
                var playerNames = Map.of(PLAYER_1, "Alberto",
                    PLAYER_2, "Emma");
            playerProxy.initPlayers(PLAYER_1, playerNames);
            // testing receiveInfo
            playerProxy.receiveInfo("Hello");

            playerProxy.chooseTickets(SortedBag.of(ChMap.tickets().subList(2,7)));

            SortedBag<Card> goodOption = SortedBag.of(1, Card.BLUE, 1, Card.LOCOMOTIVE);
            playerProxy.chooseAdditionalCards(List.of(goodOption));

            //playerProxy.nextTurn();
            //playerProxy.claimedRoute();

            // testing updateState
            var faceUpCards = SortedBag.of(5, Card.LOCOMOTIVE).toList();
            var cardState = new PublicCardState(faceUpCards, 0, 0);
            var initialPlayerState = (PublicPlayerState) PlayerState.initial(SortedBag.of(4, Card.RED));
            var playerState = Map.of(
                    PLAYER_1, initialPlayerState,
                    PLAYER_2, initialPlayerState);
            playerProxy.updateState(new PublicGameState(3, cardState, PLAYER_1, playerState, PLAYER_1), (PlayerState) initialPlayerState);

            // testing setInitialTicketChoice
            playerProxy.setInitialTicketChoice(SortedBag.of(ChMap.tickets().subList(0,5)));

            // testing chooseInitialTickets
            playerProxy.chooseInitialTickets();

            // testing nextTurn
            playerProxy.nextTurn();

            // testing chooseTickets
            playerProxy.chooseTickets(SortedBag.of(ChMap.tickets().subList(2,7)));

            // testing drawSlot
            playerProxy.drawSlot();

            // testing claimedRoute
            playerProxy.claimedRoute();

            // testing initialClaimCards
            playerProxy.initialClaimCards();

            // testing additional
            //SortedBag<Card> goodOption = SortedBag.of(1, Card.BLUE, 1, Card.LOCOMOTIVE);
            playerProxy.chooseAdditionalCards(List.of(goodOption));
        }
        System.out.println("Server done!");
    }
}
