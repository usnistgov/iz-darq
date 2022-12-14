package gov.nist.healthcare.iz.darq.digest.app.exception;

public class InvalidConfigurationFileContentException extends TerminalException {
    public InvalidConfigurationFileContentException(Throwable cause) {
        super(
                cause,
                18,
                "[CONFIGURATION ERROR] Content is invalid ",
                "Configuration file content is invalid. Reason :" + cause.getMessage(),
                true
        );
    }
}
