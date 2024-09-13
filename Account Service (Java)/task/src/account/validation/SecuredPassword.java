package account.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = SecuredPasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
public @interface SecuredPassword {

    String message() default
            "The password is in the hacker's database!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
