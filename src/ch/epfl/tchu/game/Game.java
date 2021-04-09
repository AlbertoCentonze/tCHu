package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.PlayerId.*;

import java.util.*;
import java.util.stream.Collectors;


public final class Game {
    private Game(){} // TODO

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        // check there are two players and players' names in the Maps
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);
        // initialize the game
        GameState game = GameState.initial(tickets, rng);
        // Step 1 - assign id and name to each player
        players.forEach((id, player) -> player.initPlayers(id, playerNames));

        // Step 2
        // Creating info map
        Map<PlayerId, Info> info = new HashMap<>();
        // Associating to each player the corresponding info instance
        info.put(PLAYER_1, new Info(playerNames.get(PLAYER_1)));
        info.put(PLAYER_2, new Info(playerNames.get(PLAYER_2)));

        // communicate to the players who will play first
        String firstPlayerMessage = info.get(game.currentPlayerId()).willPlayFirst();
        updateInfo(players, firstPlayerMessage);

        Player initialPlayer = null; // TODO name
        for (PlayerId id : players.keySet()) {
            Player p = players.get(id);
            assert p != initialPlayer;
            initialPlayer = p;
            //Step 3
            // Distributing five tickets to each player
            p.setInitialTicketChoice(game.topTickets(INITIAL_TICKETS_COUNT));
            // Updating the players' states
            game = game.withoutTopTickets(INITIAL_TICKETS_COUNT);
        }
        updateState(players, game);

        // Step 4
        for (PlayerId id : players.keySet()) {
            Player p = players.get(id);
            // Each player chooses the tickets to keep
            game = game.withInitiallyChosenTickets(id, p.chooseInitialTickets());


        }
        updateState(players, game);

        for (PlayerId id : players.keySet()) {
            // Step 5
            // communicating to the players how many tickets each one has kept
            String keptTicketsMessage = info.get(id).keptTickets(game.playerState(id).ticketCount());
            updateInfo(players, keptTicketsMessage);
        }

        // --------------------------- STEP 2 -----------------------------
        // execution of the game
        // number of turns left once a player is left with 2 or fewer wagons
        int lastTurns = 2;
        boolean lastTurnHasBegun = false;
        while (!game.lastTurnBegins() || lastTurns >= 0) {
            // Info of the current player
            Info currentInfo = info.get(game.currentPlayerId());
            // communicating to the players that the current player can play
            String canPlayMessage = currentInfo.canPlay();
            updateInfo(players, canPlayMessage);
            // current player
            Player currentPlayer = players.get(game.currentPlayerId());

            // updating the players' states
            updateState(players, game);

            // establishing which action the current player wants to take
            Player.TurnKind typeOfTurn = currentPlayer.nextTurn();
            switch (typeOfTurn) {
                case DRAW_TICKETS: // draw 3 tickets and keep at least one
                    game = drawTicket(players, game, currentInfo, currentPlayer);
                    break;
                case DRAW_CARDS: // draw 2 cards
                    for (int i = 0; i < 2; ++i) {
                        // recreating deck if empty
                        game = game.withCardsDeckRecreatedIfNeeded(rng);
                        // establishing where the current player draws from
                        int source = currentPlayer.drawSlot();
                        // drawing from the deck of cards
                        if (source == Constants.DECK_SLOT) {
                            game = game.withBlindlyDrawnCard();
                            // communicating that the current player drew blindly from the deck
                            String drewBlindCardMessage = currentInfo.drewBlindCard();
                            updateInfo(players, drewBlindCardMessage);
                        } else { // taking one of the faceUpCards
                            Card chosenCard = game.cardState().faceUpCard(source);
                            game = game.withDrawnFaceUpCard(source);
                            // communicating that the current player drew a specific faceUpCard
                            String drewVisibleCardMessage = currentInfo.drewVisibleCard(chosenCard);
                            updateInfo(players, drewVisibleCardMessage);
                        }
                        // updating the players' states to inform them of changed cards
                        updateState(players, game);
                    }
                    break;
                case CLAIM_ROUTE: // attempt to claim a route
                    game = claimRoute(currentPlayer, players, currentInfo, game, rng);
                    break;
            }


            // counting down the turns left to play once the last turn has begun
            // System.out.println(game.currentPlayerState().carCount()); // TODO
            if (lastTurnHasBegun || game.lastTurnBegins()){
                --lastTurns;
                lastTurnHasBegun = true;
                if (lastTurns == 1){ // TODO take away
                    // communicating that the last turn begins
                    String lastTurnBeginsMessage = currentInfo.lastTurnBegins(game.currentPlayerState().carCount());
                    updateInfo(players, lastTurnBeginsMessage);
                }
            }
            // passing the turn to the opposite player
            game = game.forNextTurn();
        }

        // TODO updateState ?

        // counting the points of the two players once the game is over
        Map<PlayerId, Integer> points = new EnumMap<>(PlayerId.class);
        PlayerId playerWithLongest = null;
        Trail longestTrail = Trail.longest(Collections.emptyList());

        // final GameState tmpGame = game;
        //List<Integer> lengths = players.keySet().stream()
        //        .map(id -> Trail.longest(tmpGame.playerState(id).routes()).length())
        //        .collect();
        for (PlayerId id : players.keySet()){
            PlayerState p = game.playerState(id);
            points.put(id, p.finalPoints());

            // establishing which player owns the longest trail
            Trail currentLongest = Trail.longest(p.routes());
            if (currentLongest.length() > longestTrail.length()){
                playerWithLongest = id;
                longestTrail = currentLongest;
            } else if (currentLongest.length() == longestTrail.length()) {
                points.replace(id, points.get(id) + LONGEST_TRAIL_BONUS_POINTS); // TODO
            }
        }
        // adding 10 bonus points to the player with the longest trail
        points.replace(playerWithLongest, points.get(playerWithLongest) + LONGEST_TRAIL_BONUS_POINTS);
        // communicating which player got the bonus
        String longestTrailBonusMessage = info.get(playerWithLongest).getsLongestTrailBonus(longestTrail); // TODO both should get this ?
        updateInfo(players, longestTrailBonusMessage);

        // updating the players' states before announcing winner
        updateState(players, game);

        // communicating the winner or the tie
        if (points.get(PLAYER_1).equals(points.get(PLAYER_2))) {
            info.get(PLAYER_1);
            updateInfo(players, Info.draw(List.copyOf(playerNames.values()), points.get(PLAYER_1)));
        } else {
            // calculating the maximum points
            Optional<Integer> maxPoints = points.values().stream().max(Integer::compare);
            // selecting the winner
            PlayerId winner = points.get(PLAYER_1).equals(maxPoints.get()) ? PLAYER_1 : PLAYER_2;
            updateInfo(players, info.get(winner).won(maxPoints.get(), points.get(winner.next())));
        }
        assert game.cardState().totalSize() + game.playerState(PLAYER_1).cardCount() + game.playerState(PLAYER_2).cardCount() == 110;
    }


    private static void updateInfo(Map<PlayerId, Player> players, String message){
        players.values().forEach((p) -> p.receiveInfo(message));
        System.out.println(message);
    }

    private static void updateState(Map<PlayerId, Player> players, GameState game) {
        players.forEach((id, player) -> player
                .updateState(game, game.playerState(id)));
    }

    private static GameState drawTicket(Map<PlayerId, Player> players, GameState game, Info currentInfo, Player currentPlayer){
        SortedBag<Ticket> topThreeTickets = game.topTickets(IN_GAME_TICKETS_COUNT);
        // communicating that current player drew 3 tickets
        String drewTicketsMessage = currentInfo.drewTickets(IN_GAME_TICKETS_COUNT);
        updateInfo(players, drewTicketsMessage);

        SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(topThreeTickets);
        // communicating that the current player kept some tickets
        String keptTicketsMessage = currentInfo.keptTickets(chosenTickets.size());
        updateInfo(players, keptTicketsMessage);
        return game.withChosenAdditionalTickets(topThreeTickets, chosenTickets);
    }

    private static GameState claimRoute(Player currentPlayer, Map<PlayerId, Player> players, Info currentInfo, GameState game, Random rng){
        GameState newGame = game;
        Route selectedRoute = currentPlayer.claimedRoute();
        // cards that the current player intends to use to claim the route
        SortedBag<Card> cardsToClaim = currentPlayer.initialClaimCards();

        assert !cardsToClaim.isEmpty();

        // the current player wants to claim the selected route
        boolean wantsToClaim = true;

        // attempting to claim a tunnel
        if (selectedRoute.level() == Route.Level.UNDERGROUND) {
            // communicating that the current player is attempting to claim a tunnel
            String attemptsTunnelClaimMessage = currentInfo.attemptsTunnelClaim(selectedRoute, cardsToClaim);
            updateInfo(players, attemptsTunnelClaimMessage);

            // three additional cards drawn from the deck of cards
            SortedBag<Card> threeDrawnCards = SortedBag.of();
            for(int i = 0; i < ADDITIONAL_TUNNEL_CARDS; ++i) {
                // recreating deck if empty
                newGame = newGame.withCardsDeckRecreatedIfNeeded(rng);
                threeDrawnCards = threeDrawnCards.union(SortedBag.of(newGame.topCard()));
                newGame = newGame.withoutTopCard();
            }
            // adding drawn cards to the discards pile
            newGame = newGame.withMoreDiscardedCards(threeDrawnCards);
            updateState(players, newGame);
            // number of additional cards the current player needs
            int additionalCost = selectedRoute.additionalClaimCardsCount(cardsToClaim, threeDrawnCards);

            // communicating the additional cards that the current player has drawn
            // and whether they imply additional costs or not
            String drewAdditionalCardsMessage = currentInfo.drewAdditionalCards(threeDrawnCards, additionalCost);
            updateInfo(players, drewAdditionalCardsMessage);

            if (additionalCost != 0) {
                // establishing whether the current player can claim the route
                wantsToClaim = newGame.currentPlayerState().canClaimRoute(selectedRoute);
                if (wantsToClaim) {
                    // all possible cards that the current player can use to pay the additional cost
                    List<SortedBag<Card>> options = newGame.currentPlayerState().possibleAdditionalCards(additionalCost, cardsToClaim, threeDrawnCards);
                    // The player doesn't have additional cards
                    if (options.size() == 0){
                        wantsToClaim = false;
                    }else{
                        // additional cards chosen by the current player
                        SortedBag<Card> chosenOption = currentPlayer.chooseAdditionalCards(options);
                        // establishing whether the current player wants to claim the tunnel
                        wantsToClaim = !chosenOption.isEmpty();
                        if (wantsToClaim) {
                            // initial cards to build route and additional cards (for tunnel)
                            cardsToClaim = cardsToClaim.union(chosenOption);
                        }
                    }
                }
                if (!wantsToClaim){
                    // communicating that the current player could not or did not want to claim the tunnel
                    String didNotClaimRouteMessage = currentInfo.didNotClaimRoute(selectedRoute);
                    updateInfo(players, didNotClaimRouteMessage);
                }
            }
        }

        if (selectedRoute.level() == Route.Level.OVERGROUND || wantsToClaim) {
            // communicating that the current player claimed the route with cardsToClaim
            String claimedRouteMessage = currentInfo.claimedRoute(selectedRoute, cardsToClaim);
            updateInfo(players, claimedRouteMessage);
            // adding the claimed tunnel to the current players routes
            newGame = newGame.withClaimedRoute(selectedRoute, cardsToClaim);
        }
        return newGame;
    }

}
