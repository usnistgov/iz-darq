package gov.nist.healthcare.iz.darq.digest.app.exception;

import java.nio.file.Path;

public class TemporaryFolderCleanupException extends TerminalException {
	public TemporaryFolderCleanupException(Throwable cause, Path temporaryDirectory) {
		super(cause,
		      23,
		      "[CLEANUP_FOLDERS_ISSUE]",
		      "This DAR command line tool was unable to fully clean up temporary files. Please be sure to delete the following folders and their contents:\n"+ temporaryDirectory.toAbsolutePath(),
		      false
		);
	}
}
