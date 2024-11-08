package com.openclassrooms.mdd.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:11:55
 *
 * Custom validator to validate Password complexity requirements
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(password)) {
            return false;
        }

        if(password.length() < 8) {
            return false;
        }

        return password.matches(".*[A-Z]+.*")
                && password.matches(".*[a-z]+.*")
                && password.matches(".*[0-9]+.*")
                && password.matches(".*\\W.*")
              ;
    }
}