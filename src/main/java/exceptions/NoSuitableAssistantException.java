package exceptions;

public class NoSuitableAssistantException extends Exception {
    public NoSuitableAssistantException(String errorMessage) {
        super(errorMessage);
    }
}
