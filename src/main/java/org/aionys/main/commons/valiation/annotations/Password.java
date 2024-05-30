package org.aionys.main.commons.valiation.annotations;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Password validation annotation.
 * Password must contain at least eight characters,
 * one uppercase letter, one lowercase letter, one digit and one special character.
 * Null elements are considered valid.
 */
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Password must contain at least 8 characters, " +
            "one uppercase letter, one lowercase letter, one digit and one special character";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
