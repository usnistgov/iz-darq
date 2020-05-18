package gov.nist.healthcare.iz.darq.analyzer.model.template;

import java.util.ArrayList;
import java.util.List;

public class ReportSection {

    String id;
    String path;
    int position;
    String header;
    String text;
    List<? extends DataViewQuery> data;
    List<? extends ReportSection> children;

    public ReportSection() {
        this.data = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<? extends DataViewQuery> getData() {
        return data;
    }

    public void setData(List<? extends DataViewQuery> data) {
        this.data = data;
    }

    public List<? extends ReportSection> getChildren() {
        return children;
    }

    public void setChildren(List<? extends ReportSection> children) {
        this.children = children;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
