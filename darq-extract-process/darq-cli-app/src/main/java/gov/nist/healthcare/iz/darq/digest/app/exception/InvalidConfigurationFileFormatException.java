package gov.nist.healthcare.iz.darq.digest.app.exception;

public class InvalidConfigurationFileFormatException extends TerminalException {
    public InvalidConfigurationFileFormatException(Throwable cause) {
        super(
                cause,
                17,
                "[CONFIGURATION ERROR] Format is invalid ",
                "Configuration file format is invalid. Reason :" + cause.getMessage(),
                true
        );
    }
}
