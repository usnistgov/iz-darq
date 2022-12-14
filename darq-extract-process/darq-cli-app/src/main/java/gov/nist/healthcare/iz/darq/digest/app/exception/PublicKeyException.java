package gov.nist.healthcare.iz.darq.digest.app.exception;

public class PublicKeyException extends TerminalException {

    public PublicKeyException(String message) {
        super(null, 16, message, message, false);
    }
}
