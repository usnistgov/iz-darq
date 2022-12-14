package gov.nist.healthcare.iz.darq.digest.app.exception;

public class InvalidCommandException extends TerminalException {
    public InvalidCommandException(Throwable cause) {
        super(
                cause,
                15,
                "Command Parsing failed.  Reason: ",
                "Command Parsing failed.  Reason: " + cause.getMessage(),
                true
        );
    }
}
