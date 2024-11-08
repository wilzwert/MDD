package com.openclassrooms.mdd.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:11:53
 * Custom annotation to validate password
 * Used for user registration requests
 */
@Documented
@Constraint(validatedBy = {PasswordValidator.class})
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Password does not meet requirements";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}

