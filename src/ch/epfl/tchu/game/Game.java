package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.PlayerId.*;

import java.util.*;


public final class Game {
    private Game(){}

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        GameState game = GameState.initial(tickets, rng);
        //Step 1
        players.forEach((id, player) -> player.initPlayers(id, playerNames));
        //Step 2

        //Creating info map
        Map<PlayerId, Info> info = new HashMap<>();

        // Associating to each player the corresponding info instance
        info.put(PLAYER_1, new Info(playerNames.get(PLAYER_1)));
        info.put(PLAYER_2, new Info(playerNames.get(PLAYER_2)));

        String firstPlayerMessage = info.get(game.currentPlayerId()).willPlayFirst();
        updateInfo(players, firstPlayerMessage);
        for (PlayerId id : players.keySet()) {
            Player p = players.get(id);
            //Step 3
            // Distributing five tickets to each player
            p.setInitialTicketChoice(game.topTickets(INITIAL_TICKETS_COUNT));
            game = game.withoutTopTickets(INITIAL_TICKETS_COUNT);
            // Step 4
            // TODO can we leave step 3 and 4 in the same cycle ?
            // Each player chooses tickets to keep
            game = game.withInitiallyChosenTickets(id, p.chooseInitialTickets());
        }
        for (PlayerId id : players.keySet()) {
            // Step 5
            // TODO check rules
            PlayerState ps = game.playerState(id);
            String keptTicketsMessage = info.get(id).keptTickets(ps.ticketCount());
            updateInfo(players, keptTicketsMessage);
        }

        // --------------------------- STEP 2 -----------------------------
        int lastTurns = 2;
        boolean lastTurnHasBegun = false;
        while (!game.lastTurnBegins() || lastTurns >= 0) {
            Info currentInfo = info.get(game.currentPlayerId());
            String canPlayMessage = currentInfo.canPlay();
            updateInfo(players, canPlayMessage);
            Player currentPlayer = players.get(game.currentPlayerId());
            Player.TurnKind typeOfTurn = currentPlayer.nextTurn();
            switch (typeOfTurn) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> topThreeTickets = game.topTickets(IN_GAME_TICKETS_COUNT);
                    String drewTicketsMessage = currentInfo.drewTickets(IN_GAME_TICKETS_COUNT);
                    updateInfo(players, drewTicketsMessage);
                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(topThreeTickets);
                    game = game.withChosenAdditionalTickets(topThreeTickets, chosenTickets);
                    String keptTicketsMessage = currentInfo.keptTickets(chosenTickets.size());
                    updateInfo(players, keptTicketsMessage);
                    break;
                case DRAW_CARDS:
                    for (int i = 0; i < 2; ++i) {
                        int source = currentPlayer.drawSlot();
                        if (source == Constants.DECK_SLOT) {
                            game.withBlindlyDrawnCard();
                            String drewBlindCardMessage = currentInfo.drewBlindCard();
                            updateInfo(players, drewBlindCardMessage);
                        } else {
                            game.withDrawnFaceUpCard(source);
                            Card chosenCard = game.cardState().faceUpCard(source);
                            String drewVisibleCardMessage = currentInfo.drewVisibleCard(chosenCard);
                            updateInfo(players, drewVisibleCardMessage);
                        }
                    }
                    break;
                case CLAIM_ROUTE:
                    Route selectedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> cardsToClaim = currentPlayer.initialClaimCards();
                    //TODO
                    if (selectedRoute.level() == Route.Level.UNDERGROUND) {
                        String attemptsTunnelClaimMessage = currentInfo.attemptsTunnelClaim(selectedRoute, cardsToClaim);
                        updateInfo(players, attemptsTunnelClaimMessage);
                        int additionalCost = selectedRoute.additionalClaimCardsCount(null);
                        if (additionalCost != 0) {
                            String drewAdditionalCardsMessage = currentInfo.drewAdditionalCards(null, additionalCost);
                            updateInfo(players, drewAdditionalCardsMessage);
                            boolean canClaim = game.currentPlayerState().canClaimRoute(selectedRoute);
                            boolean wantsToClaim = canClaim;
                            if (canClaim) {
                                List<SortedBag<Card>> options = game.currentPlayerState().possibleAdditionalCards(null, cardsToClaim, )
                                SortedBag<Card> chosenOption = currentPlayer.chooseAdditionalCards(options);
                                wantsToClaim = !chosenOption.isEmpty();
                                if (wantsToClaim) {
                                    SortedBag<Card> allCardsUsed = chosenOption.union(cardsToClaim);
                                    String claimedRouteMessage = currentInfo.claimedRoute(selectedRoute, allCardsUsed);
                                    updateInfo(players, claimedRouteMessage);
                                    game = game.withClaimedRoute(selectedRoute, allCardsUsed);
                                }
                            }
                            if (!wantsToClaim){
                                String didNotClaimRouteMessage = currentInfo.didNotClaimRoute(selectedRoute);
                                updateInfo(players, didNotClaimRouteMessage);
                            }
                        }
                    }else{
                        //TODO optimize
                        String claimedRouteMessage = currentInfo.claimedRoute(selectedRoute, cardsToClaim);
                        updateInfo(players, claimedRouteMessage);
                    }
                    break;
            }

            if (lastTurnHasBegun || game.lastTurnBegins()){
                --lastTurns;
                lastTurnHasBegun = true;
                if (lastTurns == 1){
                    String lastTurnBeginsMessage = currentInfo.lastTurnBegins(game.currentPlayerState().carCount());
                    updateInfo(players, lastTurnBeginsMessage);
                }
            }
            game = game.forNextTurn();
        }

        Map<PlayerId, Integer> points = new EnumMap<>(PlayerId.class);
        PlayerId playerWithLongest = PLAYER_1;
        int longestLength = 0;
        for (PlayerId id : players.keySet()){
            PlayerState p = game.playerState(id);
            int currentLongest = Trail.longest(p.routes()).length();
            if (currentLongest > longestLength){
                playerWithLongest = id;
            }
            points.put(id, p.finalPoints());
        }
        points.replace(playerWithLongest, points.get(playerWithLongest) + 10);

        for (PlayerId id : players.keySet()){
            PlayerState p = game.playerState(id);
            points.put(id, )
        }
    }


    private static void updateInfo(Map<PlayerId, Player> players, String message){
        players.values().forEach((p) -> p.receiveInfo(message));
        System.out.println(message);
    }

}
