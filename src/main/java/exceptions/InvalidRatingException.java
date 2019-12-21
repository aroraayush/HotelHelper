package exceptions;

/**
 * This class represents a custom exception that throws exception when rating is out of range 1-5
 */
public class InvalidRatingException extends Exception {
    /**
     * This is constructor for InvalidRatingException
     * @param message error message to be shown
     */
    public InvalidRatingException(String message) {
        super(message);
    }
}
