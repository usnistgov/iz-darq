package gov.nist.healthcare.iz.darq.users.exception;

import gov.nist.healthcare.iz.darq.users.domain.FieldValidation;

public class FieldValidationException extends Exception {
    FieldValidation fieldValidation;

    public FieldValidationException(FieldValidation fieldValidation) {
        super(fieldValidation.getExpectation());
        this.fieldValidation = fieldValidation;
    }

    public FieldValidation getFieldValidation() {
        return fieldValidation;
    }

    public void setFieldValidation(FieldValidation fieldValidation) {
        this.fieldValidation = fieldValidation;
    }
}
