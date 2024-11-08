package com.openclassrooms.mdd.validator;


import com.openclassrooms.mdd.validation.PasswordValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Wilhelm Zwertvaegher
 * Date:08/11/2024
 * Time:11:33
 */

public class PasswordValidatorTest {

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    private PasswordValidator passwordValidator = new PasswordValidator();

    /*Un mot de passe est valide si :
- son nombre de caractère est supérieur ou égal à 8 caractères ;
- il contient au moins un de chacun de ces types de caractères :
- chiffre,
- lettre minuscule,
- lettre majuscule,
- caractère spécial*/

    @Test
    public void shouldNotValidatePasswordWhenTooShort() {
        String tooShortPassword = "ab12.Ta";
        assertThat(passwordValidator.isValid(tooShortPassword, constraintValidatorContext)).isFalse();
    }

    @Test
    public void shouldNotValidatePasswordWhenNoDigit() {
        String noDigitPassword = "abZptr.Ta";
        assertThat(passwordValidator.isValid(noDigitPassword, constraintValidatorContext)).isFalse();
    }

    @Test
    public void shouldNotValidatePasswordWhenNoLowercase() {
        String noLowerCasePassword = "ABZPTR.12TA";
        assertThat(passwordValidator.isValid(noLowerCasePassword, constraintValidatorContext)).isFalse();
    }

    @Test
    public void shouldNotValidatePasswordWhenNoUpperCase() {
        String noUpperCasePassword = "abzptr.12ta";
        assertThat(passwordValidator.isValid(noUpperCasePassword, constraintValidatorContext)).isFalse();
    }

    @Test
    public void shouldNotValidatePasswordWhenNoSpecialCharacter() {
        String noSpecialCharacterPassword = "abzPTRx12ta";
        assertThat(passwordValidator.isValid(noSpecialCharacterPassword, constraintValidatorContext)).isFalse();
    }
    @Test
    public void shouldValidatePassword() {
        String validPassword = "*abzPTR_x12ta";
        assertThat(passwordValidator.isValid(validPassword, constraintValidatorContext)).isTrue();
    }


}
