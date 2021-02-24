package ch.epfl.tchu;

public final class Preconditions {

    public static void main(String[] args){
        System.out.println("Ji");
    }

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