package gov.nist.extract.lucene.index;

public class IndexedDocument {

    private String ID;
    private String content;
    private int line;

    public IndexedDocument(String ID, String content, int line) {
        this.ID = ID;
        this.content = content;
        this.line = line;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
