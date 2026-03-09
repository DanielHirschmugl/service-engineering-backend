package jku.se.exceptions;

public class NoValidPasswordException extends Exception {
    public NoValidPasswordException(String message) {
        super(message);
    }
}
