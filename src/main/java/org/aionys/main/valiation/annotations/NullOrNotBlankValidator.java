package org.aionys.main.valiation.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String>{

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !value.isBlank();
    }
}
