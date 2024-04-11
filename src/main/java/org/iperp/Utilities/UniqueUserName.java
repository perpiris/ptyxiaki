package org.iperp.Utilities;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.iperp.Services.Implementation.RegistrationService;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = UniqueUserName.RegistrationRequestUsernameUniqueValidator.class
)
public @interface UniqueUserName {

    String message() default "A user is already registered.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class RegistrationRequestUsernameUniqueValidator implements ConstraintValidator<UniqueUserName, String> {

        private final RegistrationService registrationService;

        public RegistrationRequestUsernameUniqueValidator(
                final RegistrationService registrationService) {
            this.registrationService = registrationService;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                return true;
            }
            return !registrationService.usernameExists(value);
        }

    }

}
