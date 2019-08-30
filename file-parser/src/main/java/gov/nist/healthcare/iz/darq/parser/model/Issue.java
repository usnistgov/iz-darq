package gov.nist.healthcare.iz.darq.parser.model;

public class Issue {

    private String field;
    private String record;
    private String message;
    private boolean critical;

    public Issue(String field, String record, String message, boolean critical) {
        this.field = field;
        this.record = record;
        this.message = message;
        this.critical = critical;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public String toString() {
        String str = "";
        str += this.getRecord() != null && !this.getRecord().isEmpty() ? "[ RECORD TYPE : "+ this.getRecord() +" ] " : "";
        str += this.getField() != null && !this.getField().isEmpty() ? "[ FIELD : "+ this.getField() +" ] " : "";
        str += getMessage();

        return  str;
    }
}
