package ch.epfl.tchu.game;

public class PlayerAIMedium extends PlayerAI {
    @Override
    public TurnKind nextTurn() {
        return null;
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
