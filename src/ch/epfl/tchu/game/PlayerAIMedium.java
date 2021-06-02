package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerAIMedium extends PlayerAI {
    private static final int PROBABILITY_DRAW_TICKET = 5;
    private static final int MEDIUM_NUMBER_OF_POINTS = 10;

    private List<Ticket> ticketsToBuild;

    /**
     * @param seed can be null blabla
     */
    public PlayerAIMedium(Integer seed) {
        super(seed);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        // keep Ticket with the least points, the most points and the middle number of points
        SortedBag<Ticket> options = initialTickets;
        List<Integer> points = options.stream().mapToInt(Ticket::points).sorted()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        points.removeAll(List.of(points.get(1), points.get(3)));
        SortedBag<Ticket> chosen = SortedBag.of(options.stream().filter(t -> points.contains(t.points()))
                .collect(Collectors.toList()));
        for(Ticket t : chosen) {
            ticketsToBuild.add(t);
        }
        return chosen;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        List<Integer> points = options.stream().mapToInt(Ticket::points).sorted()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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
        } else if(getAvailableRoutes().stream().anyMatch(r -> ownState.canClaimRoute(r))) {
            // TODO prioritize routes that are connected
            return TurnKind.CLAIM_ROUTE;
        } else {
            return TurnKind.DRAW_CARDS; // TODO canDrawCard() to be checked?
        }
    }

}

/* hard:
takes cards that could be helpful to opponent -> opponent needs a card to claim a route
to connect two other routes of his -> if that card is in the faceUpCards the AI takes it
claims routes that link two routes of the opponent
prioritizes double routes (helpful?)
when choosing tickets takes tickets for which he has already built many routes
and for which the opponent hasn't built many routes

prioritize tunnels?
prioritize longest? & play against opponent for longest?

what is a smart way to choose the initial claim cards and the additional cards?
    additional cards: if they're necessary to claim another more important route, then NOT claim the tunnel
 */
