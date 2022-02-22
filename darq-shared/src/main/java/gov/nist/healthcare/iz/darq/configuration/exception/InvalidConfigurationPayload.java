package gov.nist.healthcare.iz.darq.configuration.exception;

import java.util.List;

public class InvalidConfigurationPayload extends Exception {
    public InvalidConfigurationPayload(List<String> errors) {
        super("Invalid Configuration : " + String.join(", ", errors));
    }
}
