package gov.nist.healthcare.auth.exception;

import org.springframework.security.authentication.AccountStatusException;

public class PendingVerificationException extends AccountStatusException {
    public PendingVerificationException(String msg) {
        super(msg);
    }
}
