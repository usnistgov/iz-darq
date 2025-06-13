package gov.nist.healthcare.iz.darq.digest.app.exception;

public class FileEncodingException extends TerminalException{
	public FileEncodingException(Throwable cause) {
		super(cause,
		      25,
		      "[FILE_ENCODING_ISSUE]",
		      "This DAR command line tool encountered an error while processing your extract files. The issue stems from the file's encoding. Please ensure that your extract files is encoded in UTF-8.",
		      false
		);
	}
}
