package gov.nist.healthcare.iz.darq.digest.app.exception;

public class FileNotFoundException extends TerminalException {
    public FileNotFoundException(FileErrorCode location) {
        super(
                null,
                location.getLocationErrorCode(),
                location.writeLocation() + " not found.",
                location.writeLocation() + " not found.",
                false
        );
    }
}
