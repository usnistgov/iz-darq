package gov.nist.healthcare.iz.darq.digest.app.exception;

public class RequiredParameterMissingException extends TerminalException {
    public RequiredParameterMissingException(FileErrorCode location) {
        super(
                null,
                location.getLocationErrorCode() + 7,
                location.writeLocation() + " parameter is missing.",
                location.writeLocation() + " parameter is missing.",
                false
        );
    }
}
