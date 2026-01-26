package nl.pink.mocks.brp.validators;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BSNValidator.class})
public @interface BSNValidation {
    String message() default "Invalid BSN. Must be 9 digits and pass the 11-test";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
