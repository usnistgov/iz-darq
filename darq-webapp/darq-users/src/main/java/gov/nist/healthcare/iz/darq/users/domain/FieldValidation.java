package gov.nist.healthcare.iz.darq.users.domain;

public class FieldValidation {
    boolean status;
    String expectation;

    public FieldValidation(boolean status, String expectation) {
        this.status = status;
        this.expectation = expectation;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getExpectation() {
        return expectation;
    }

    public void setExpectation(String expectation) {
        this.expectation = expectation;
    }
}
