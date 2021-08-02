package gov.nist.extract.lucene.model;

public class FormatIssue {
    private int line;
    private String message;

    public FormatIssue(int line, String message) {
        this.line = line;
        this.message = message;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
