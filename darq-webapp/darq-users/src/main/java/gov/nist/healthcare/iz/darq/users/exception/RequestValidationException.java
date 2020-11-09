package gov.nist.healthcare.iz.darq.users.exception;

import gov.nist.healthcare.iz.darq.users.domain.FieldValidation;

import java.util.List;
import java.util.stream.Collectors;

public class RequestValidationException extends Exception {
    List<FieldValidation> fieldValidationList;

    public RequestValidationException(List<FieldValidation> fieldValidationList) {
        super(String.join(", ",  fieldValidationList.stream().map(FieldValidation::getExpectation).collect(Collectors.toSet())));
        this.fieldValidationList = fieldValidationList;
    }

    public List<FieldValidation> getFieldValidationList() {
        return fieldValidationList;
    }

    public void setFieldValidationList(List<FieldValidation> fieldValidationList) {
        this.fieldValidationList = fieldValidationList;
    }
}
