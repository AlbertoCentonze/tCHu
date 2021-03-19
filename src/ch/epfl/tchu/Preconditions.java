package ch.epfl.tchu;

/**
 * @author Emma Poggiolini (330757)
 */

public final class Preconditions {
    /**
	 * Constructor by default
	 */
    private Preconditions() {}

    /**
	 * Check whether the argument is valid
	 * @param (boolean) shouldBeTrue
	 */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

}