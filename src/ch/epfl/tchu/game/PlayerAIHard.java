package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerAIHard extends PlayerAIMedium {

    /**
     * Constructor
     * @param seed can be null
     */
    public PlayerAIHard(Integer seed) {
        super(seed);
    }

    @Override
    public TurnKind nextTurn() {
        return super.nextTurn();
    }

    @Override
    public boolean nextTurnSpecific() { // TODO which nextTurnSpecific is called ?
        updateClaimable();
        if(!claimable.isEmpty()) {
            // TODO prioritize routes that are connected
            routeToClaim = choosingRouteToClaim();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        List<SortedBag<Card>> options = this.ownState.possibleClaimCards(this.routeToClaim);
        return choosingCards(options);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        return choosingCards(options);
    }

    private SortedBag<Card> choosingCards(List<SortedBag<Card>> options) {
        // prioritize card choice that do not contain locomotives
        List<SortedBag<Card>> noLocomotives = options.stream().filter(bag -> !bag.contains(Card.LOCOMOTIVE))
                .collect(Collectors.toList());
        if(!noLocomotives.isEmpty()) {
            return noLocomotives.get(rng.nextInt(noLocomotives.size()));
        }
        // otherwise order the player's cards by increasing number
        List<Card> countOfCard = new ArrayList<>();
        List<Integer> numberOfCard = ownState.cards().stream().mapToInt(c -> ownState.cards().countOf(c))
                .sorted().boxed().collect(Collectors.toList());
        for(Integer i : numberOfCard) {
            Set<Card> sameNumber = ownState.cards().stream().filter(c -> ownState.cards()
                    .countOf(c) == i).collect(Collectors.toSet());
            countOfCard.addAll(sameNumber);
        }
        // set of types of cards present among the card choices
        Set<Card> cardsInOptions = new HashSet<>();
        for (SortedBag<Card> bag : options) {
            cardsInOptions.addAll(bag.stream().collect(Collectors.toSet()));
        }
        // remove all cards owned by the player but not found among the card choices
        countOfCard.retainAll(cardsInOptions);
        // choose the SortedBag containing the type of card that the player owns in highest quantity
        return options.stream().filter(bag -> bag.contains(countOfCard.get(countOfCard.size() - 1))).findFirst().get();
    }

    // only called when claimable is not empty
    protected Route choosingRouteToClaim() {
        // prioritizing longest routes
        int i = claimable.stream().mapToInt(Route::length).max().getAsInt();
        return claimable.stream().filter(r -> r.length() == i).findFirst().get();
    }

}
