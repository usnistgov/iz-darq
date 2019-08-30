package gov.nist.healthcare.iz.darq.parser.service.model;

import gov.nist.healthcare.iz.darq.parser.model.Issue;

public class ParseError extends Issue {
    private int line;
    private String recordID;

    public ParseError(String ID, String field, String record, String message, boolean critical, int line) {
        super(field, record, message, critical);
        this.line = line;
        this.recordID = ID;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String toString() {
        String str = "";
        str += this.recordID != null && !this.recordID.isEmpty() ? "[ RECORD ID : "+ this.recordID +" ] " : "";
        str += this.getRecord() != null && !this.getRecord().isEmpty() ? "[ RECORD TYPE : "+ this.getRecord() +" ] " : "";
        str += this.getField() != null && !this.getField().isEmpty() ? "[ FIELD : "+ this.getField() +" ] " : "";
        str += this.line != 0 ? "[ LINE : "+ this.line+" ] " : "";
        str += getMessage();

        return  str;
    }
}
