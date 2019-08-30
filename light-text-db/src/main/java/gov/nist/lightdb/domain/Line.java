package gov.nist.lightdb.domain;

public class Line {

    String content;
    int nb;

    public Line(String content, int nb) {
        this.content = content;
        this.nb = nb;
    }

    public String getContent() {
        return content;
    }

    public int getNb() {
        return nb;
    }
}
