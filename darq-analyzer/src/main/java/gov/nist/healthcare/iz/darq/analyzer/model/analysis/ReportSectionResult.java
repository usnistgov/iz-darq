package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportSection;

import java.util.ArrayList;
import java.util.List;

public class ReportSectionResult extends ReportSection {
    List<DataTable> data;
    String comment;

    public void fromSection(ReportSection sectionTemplate) {
        this.setId(sectionTemplate.getId());
        this.setPath(sectionTemplate.getPath());
        this.setPosition(sectionTemplate.getPosition());
        this.setHeader(sectionTemplate.getHeader());
        this.setText(sectionTemplate.getText());
        data = new ArrayList<>();
    }

    @Override
    public List<DataTable> getData() {
        return data;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
