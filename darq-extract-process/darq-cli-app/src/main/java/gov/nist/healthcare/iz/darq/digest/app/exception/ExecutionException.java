package gov.nist.healthcare.iz.darq.digest.app.exception;

public class ExecutionException extends TerminalException {
    public ExecutionException(Throwable cause, String message) {
        super(cause, 20, message, message, true);
    }
}
