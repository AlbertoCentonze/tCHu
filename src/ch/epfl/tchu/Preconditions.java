package ch.epfl.tchu;

/**
 * @author Emma Poggiolini
 */
public final class Preconditions {
    /**
	 * Constructor by default
	 * @param none
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