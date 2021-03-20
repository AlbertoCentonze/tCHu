package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

public interface Player {
    abstract void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);
    abstract void receiveInfo(String info);
    abstract void updateState(PublicGameState newState, PlayerState ownState);
    abstract void setInitialTicketChoice(SortedBag<Ticket> tickets);
    abstract SortedBag<Ticket> chooseInitialTickets();
    abstract TurnKind nextTurn();
    abstract SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);
    abstract int drawSlot();
    abstract Route claimedRoute();
    abstract SortedBag<Card> initialClaimCards();
    abstract SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    public enum TurnKind {
        DRAW_TICKETS, DRAW_CARDS, CLAIM_ROUTE;
        final public static List<TurnKind> ALL = List.of(TurnKind.values());
    }
}
