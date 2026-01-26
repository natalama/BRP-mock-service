package nl.pink.mocks.brp.exception;

import java.io.IOException;

/**
 * Exception for errors related to person file (reading/writing)
 * */
public class PersonFileException extends IOException {
    public PersonFileException() {
        super();
    }

    public PersonFileException(String message) {
        super(message);
    }

    public PersonFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonFileException(Throwable cause) {
        super(cause);
    }
}
