package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerAIEasy;
import ch.epfl.tchu.game.PlayerAIMedium;

public enum PlayerType {
    HOST("host", false),
    REMOTE("remote", false),
    AI_EASY("easy", true),
    AI_MEDIUM("medium", true),
    AI_HARD("hard", true);

    String typeString;
    boolean ai;
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
            case AI_EASY:
                return new PlayerAIEasy();
            case AI_MEDIUM:
                return new PlayerAIMedium();
            case AI_HARD:
                return null; //TODO
            default:
                throw new Error();
        }
    }

    public boolean isAi(){
        return ai;
    }

    @Override
    public String toString() {
        return typeString;
    }
}
