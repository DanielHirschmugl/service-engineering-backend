package jku.ce.exceptions;

public class NoValidPasswordException extends Exception {
    public NoValidPasswordException(String message) {
        super(message);
    }
}
