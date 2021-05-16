package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PlayerType { //TODO fix it if more (real) players are not implemented
    HOST("Host", false),
    REMOTE("Remote", false),
    AI_EASY("AI-Easy", true),
    AI_MEDIUM("AI-Medium", true),
    AI_HARD("AI-Hard", true);

    String typeString;
    boolean ai;

    private static final List<PlayerType> ALL = List.of(PlayerType.values());
    public static final List<PlayerType> AIS = ALL.stream().filter(t -> t.ai).collect(Collectors.toList());
    public static final List<PlayerType> HUMANS = ALL.stream().filter(t -> !t.ai).collect(Collectors.toList());
    PlayerType(String type, boolean isAi){
        typeString = type;
        ai = isAi;
    }

    public Player getPlayer(){
        switch (this){
            case HOST:
                return new GraphicalPlayerAdapter();
            case REMOTE:
                return null; // TODO
            default:
                throw new UnsupportedOperationException(); //TODO what type of error
        }
    }

    private PlayerAI getAi(){
        switch (this){
            case AI_EASY:
                return new PlayerAIEasy(null);
            case AI_MEDIUM:
                return new PlayerAIMedium(null);
            case AI_HARD:
                return new PlayerAIHard(null);
            default:
                throw new IllegalArgumentException(); //TODO what type of error
        }
    }

    public PlayerAI getAi(Integer seed){
        if (seed == null)
            return getAi();
        switch (this){
            case AI_EASY:
                return new PlayerAIEasy(seed);
            case AI_MEDIUM:
                return new PlayerAIMedium(seed);
            case AI_HARD:
                return new PlayerAIHard(seed);
            default:
                throw new IllegalArgumentException(); //TODO what type of error
        }
    }

    @Override
    public String toString() {
        return typeString;
    }
}
