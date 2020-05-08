package gov.nist.healthcare.iz.darq.controller.domain;

public class FailureWrapper {

    String message;

    public FailureWrapper(String message) {
        this.message = message;
    }
    public FailureWrapper(Exception ex) {
        this.message = ex.getMessage();
    }
    public FailureWrapper() {
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
