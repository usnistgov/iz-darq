package gov.nist.healthcare.iz.darq.digest.app.exception;

public class DetectionTestException extends TerminalException {
	public DetectionTestException(String message) {
		super(null, 23, message, message, false);
	}
}
