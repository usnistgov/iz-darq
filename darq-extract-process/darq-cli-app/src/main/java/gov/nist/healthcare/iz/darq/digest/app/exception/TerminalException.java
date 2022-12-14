package gov.nist.healthcare.iz.darq.digest.app.exception;

public class TerminalException extends Exception {
    private final int exitCode;
    private final String logs;
    private final String print;
    private final boolean printStackTrace;

    public TerminalException(Throwable cause, int exitCode, String logs, String print, boolean printStackTrace) {
        super(cause);
        this.exitCode = exitCode;
        this.logs = logs;
        this.print = print;
        this.printStackTrace = printStackTrace;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getLogs() {
        return logs;
    }

    public String getPrint() {
        return print;
    }

    public boolean isPrintStackTrace() {
        return printStackTrace;
    }
}
