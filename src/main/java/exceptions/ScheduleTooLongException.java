package exceptions;

public class ScheduleTooLongException extends Exception {
    public ScheduleTooLongException(String message) {
        super(message);
    }
}
