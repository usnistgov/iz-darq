package gov.nist.healthcare.iz.darq.analyzer.model.template;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Report;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class ReportTemplate extends Report {

    List<ReportSection> sections;

    public ReportTemplate() {
        this.sections = new ArrayList<>();
    }

    public List<ReportSection> getSections() {
        return sections;
    }

    public void setSections(List<ReportSection> sections) {
        this.sections = sections;
    }

}
