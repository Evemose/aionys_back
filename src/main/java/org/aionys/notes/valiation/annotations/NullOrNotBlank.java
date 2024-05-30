package org.aionys.notes.valiation.annotations;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be not blank. Null elements are considered valid.
 */
@Constraint(validatedBy = {NullOrNotBlankValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {
    String message() default "must not be blank";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
