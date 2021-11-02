package com.opabs.common.validator;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class DistinguishedNameValidator implements ConstraintValidator<DistinguishedName, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = false;
        if (value != null) {
            try {
                new X500Name(value);
                isValid = true;
            } catch (Exception ex) {
                log.error("Encountered illegal distinguished name.", ex);
            }
        }
        return isValid;
    }
}
