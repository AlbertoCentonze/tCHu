package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Random;

public final class Deck <C extends Comparable<C>> {
    SortedBag<C> cards;

    private Deck(){

    }

    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng){
        return null;
    }

    public int size(){
        return cards.size();
    }

    public boolean isEmpty(){
        return false; //TODO
    }

    public C topCard(){
        return null; //TODO
    }

    public Deck<C> withoutTopCard(){
        return null; //TODO
    }

    public Deck<C> topCards(int count){
        return null; //TODO
    }

    public Deck<C> withoutTopCards(int count){
        return null; //TODO
    }
}
