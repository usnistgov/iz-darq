package gov.nist.healthcare.iz.darq.digest.app.exception;

public class SummaryIssuesException extends TerminalException {
	public SummaryIssuesException() {
		super(
				null,
				24,
				"Issues were encountered while processing the extract files, see darq-analysis/summary/index.html",
				"Issues were encountered while processing the extract files, see darq-analysis/summary/index.html",
				false
		);
	}
}
