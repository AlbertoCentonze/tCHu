package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import static javafx.application.Platform.runLater;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Alberto Centonze (327267)
 * Adapter to make GraphicalPlayer implement the interface Player
 */
public final class GraphicalPlayerAdapter implements Player {
    private GraphicalPlayer graphicalPlayer;
    // All the blocking queues used to exchange variables between javafx and the game thread
    private final ArrayBlockingQueue<SortedBag<Ticket>> ticketsQueue = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<SortedBag<Card>> claimCardsQueue = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<Integer> drawSlotQueue = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<Route> routeQueue = new ArrayBlockingQueue<>(1);
    private final ArrayBlockingQueue<TurnKind> turnQueue = new ArrayBlockingQueue<>(1);

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets,
                ticketsQueue::add));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return takeFromQueue(ticketsQueue);
    }

    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(
                //ticket handler
                () -> {
                    turnQueue.add(TurnKind.DRAW_TICKETS);
                },
                // card handler
                slot -> {
                    drawSlotQueue.add(slot);
                    turnQueue.add(TurnKind.DRAW_CARDS);
                },
                // route handler
                (route, initialClaimCards) -> {
                    routeQueue.add(route);
                    claimCardsQueue.add(initialClaimCards);
                    turnQueue.add(TurnKind.CLAIM_ROUTE);
                }));
        return takeFromQueue(turnQueue);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(() -> graphicalPlayer.chooseTickets(options,
                ticketsQueue::add));
        return takeFromQueue(ticketsQueue);
    }

    @Override
    public int drawSlot() {
        if (drawSlotQueue.size() == 0){
            runLater(() -> graphicalPlayer.drawCard(
                    drawSlotQueue::add));
        }
        return takeFromQueue(drawSlotQueue);
    }

    @Override
    public Route claimedRoute() {
        return takeFromQueue(routeQueue);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return takeFromQueue(claimCardsQueue);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options,
                claimCardsQueue::add));
        return takeFromQueue(claimCardsQueue);
    }

    private <T> T takeFromQueue(ArrayBlockingQueue<T> queue){
        try{
            return queue.take();
        }catch (InterruptedException e){
            throw new Error(e);
        }
    }
}
