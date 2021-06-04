package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerAIMedium extends PlayerAI {
    private static final int PROBABILITY_DRAW_TICKET = 5;
    private static final int MEDIUM_NUMBER_OF_POINTS = 10;

    protected final List<Ticket> ticketsToBuild = new ArrayList<>();

    /**
     * Constructor
     * @param seed can be null
     */
    public PlayerAIMedium(Integer seed) {
        super(seed);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        // keep Ticket with the least points, the most points and the middle number of points
        SortedBag<Ticket> options = initialTickets;
        List<Ticket> orderedList = options.toList();
        orderedList.sort(Comparator.comparingInt(Ticket::points));
        for (int i = 1; i <= 3; i += 2)
            orderedList.remove(i); // removes at slot 1 and 3
        SortedBag<Ticket> chosen = SortedBag.of(orderedList);
        ticketsToBuild.addAll(chosen.toList());
        return chosen;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        List<Integer> points = options.stream().mapToInt(Ticket::points).sorted()
                .boxed().collect(Collectors.toList());
        SortedBag<Ticket> chosen;

        if(ticketsToBuild.size() == 0) { // choose tickets with the most and least points
            points.remove(1);
            chosen = SortedBag.of(options.stream().filter(t -> points.contains(t.points()))
                    .collect(Collectors.toList()));
        }
        else if(ticketsToBuild.get(0).points() > MEDIUM_NUMBER_OF_POINTS) {
            // choose ticket with the least points
            chosen = SortedBag.of(options.stream().filter(t -> points.get(0) == t.points())
                    .collect(Collectors.toList()));
        } else {
            // choose ticket with the most points
            chosen = SortedBag.of(options.stream().filter(t -> points.get(Constants.IN_GAME_TICKETS_COUNT - 1) == t.points())
                    .collect(Collectors.toList()));
        }
        for(Ticket t : chosen) {
            if(t.points(ownState.connections()) < 0) {
                ticketsToBuild.add(t);
            }
        }
        return chosen;
    }

    @Override
    public int drawSlot() {
        // prioritize locomotives
        if(gameState.cardState().faceUpCards().contains(Card.LOCOMOTIVE)) {
            return gameState.cardState().faceUpCards().indexOf(Card.LOCOMOTIVE);
        } // otherwise choose randomly
        return super.drawSlot();
    }

    @Override
    public TurnKind nextTurn() {
        // remove a ticket from the tickets to build if it has been fully built
        ownState.tickets().forEach(t -> {
            if(ticketsToBuild.contains(t) && t.points(ownState.connections()) > 0) {
                ticketsToBuild.remove(t);
            }
        });
        // drawing tickets if all tickets have been built
        // or if 1 ticket is left to build with a probability of 0.2
        if(gameState.canDrawTickets() && (ticketsToBuild.size() == 0 ||
                (ticketsToBuild.size() == 1 && rng.nextInt(PROBABILITY_DRAW_TICKET) == 0))) {
            return TurnKind.DRAW_TICKETS;
        }
        if(nextTurnSpecific()) {
            return TurnKind.CLAIM_ROUTE;
        }
        else if(gameState.canDrawCards()) { // otherwise draws card
            return TurnKind.DRAW_CARDS;
        } else { // default value
            return TurnKind.DRAW_TICKETS;
        }
    }

    public boolean nextTurnSpecific() {
        updateClaimable();
        if(!claimable.isEmpty()) {
            routeToClaim = choosingRouteToClaim();
            return true;
        } else {
            return false;
        }
    }

    // only called when claimable is not empty
    protected Route choosingRouteToClaim() {
        // prioritizing longest routes
        int i = claimable.stream().mapToInt(Route::length).max().getAsInt();
        return claimable.stream().filter(r -> r.length() == i).findFirst().get();
    }

}
