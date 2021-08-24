package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Section;

import java.util.ArrayList;
import java.util.List;

public class ReportSection extends Section {

    List<QueryPayload> data;
    List<ReportSection> children;

    public ReportSection() {
        this.data = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public List<QueryPayload> getData() {
        return data;
    }

    public void setData(List<QueryPayload> data) {
        this.data = data;
    }

    public List<ReportSection> getChildren() {
        return children;
    }

    public void setChildren(List<ReportSection> children) {
        this.children = children;
    }
}
