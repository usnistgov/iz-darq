package gov.nist.healthcare.iz.darq.digest.app.exception;

public class InvalidDateFormatException extends TerminalException {
    public InvalidDateFormatException(Throwable cause, String message) {
        super(cause, 19, message, message, false);
    }
}
