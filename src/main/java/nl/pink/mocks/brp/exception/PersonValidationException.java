package nl.pink.mocks.brp.exception;

import org.apache.logging.log4j.util.Strings;
import org.springframework.validation.Errors;

public class PersonValidationException extends IllegalArgumentException{

    public PersonValidationException() {
        super();
    }

    public PersonValidationException(String message) {
        super(message);
    }

    public PersonValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonValidationException(String message, Errors errors) {
        super(message+ Strings.join(errors.getAllErrors(), '\n'));
    }

    public PersonValidationException(Throwable cause) {
        super(cause);
    }
}
