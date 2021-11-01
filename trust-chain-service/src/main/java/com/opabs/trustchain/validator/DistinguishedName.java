package com.opabs.trustchain.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DistinguishedNameValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DistinguishedName {
    String message() default "Invalid distinguished name.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
