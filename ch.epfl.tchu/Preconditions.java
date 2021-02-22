public final class Preconditions {

    /**
	 * Constructor by default
	 * @param none
	 */
    private Preconditions() {}

    /**
	 * Check whether the argument is valid
	 * @param (boolean) argument
	 */
    public static void checkArgument(boolean shouldBeTrue){
        if(!shouldBeTrue){
            throw new IllegalArgumentException();
        }
    }

}