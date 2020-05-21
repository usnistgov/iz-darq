package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Section;

import java.util.ArrayList;
import java.util.List;

public class ReportSection extends Section {

    List<DataViewQuery> data;
    List<ReportSection> children;

    public ReportSection() {
        this.data = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public List<DataViewQuery> getData() {
        return data;
    }

    public void setData(List<DataViewQuery> data) {
        this.data = data;
    }

    public List<ReportSection> getChildren() {
        return children;
    }

    public void setChildren(List<ReportSection> children) {
        this.children = children;
    }
}
