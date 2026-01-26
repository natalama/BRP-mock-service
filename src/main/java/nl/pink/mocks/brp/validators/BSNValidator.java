package nl.pink.mocks.brp.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nl.pink.mocks.brp.utils.BsnUtils;

public class BSNValidator implements ConstraintValidator<BSNValidation, String> {
    @Override
    public boolean isValid(String bsn, ConstraintValidatorContext constraintValidatorContext) {
        return BsnUtils.isValidBsn(bsn);
    }
}
