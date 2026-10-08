
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.*;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MockReportComparisonTestFactory {

    public static AnalysisReport createStubReport(String reportId, String templateId) {
        AnalysisReport report = new AnalysisReport();
        report.setId(reportId);

        ReportTemplate template = new ReportTemplate();
        template.setId(templateId);
        report.setReportTemplate(template);

        report.setSections(new ArrayList<>());
        return report;
    }

    public static ReportSectionResult createStubSection(String sectionId, boolean thresholdViolation) {
        ReportSectionResult section = new ReportSectionResult();
        section.setId(sectionId);
        section.setThresholdViolation(thresholdViolation);
        section.setData(new ArrayList<>());
        section.setChildren(new ArrayList<>());
        return section;
    }

    public static DataTable createStubTable(String caption, boolean thresholdViolation) {
        DataTable table = new DataTable();
        table.setThresholdViolation(thresholdViolation);
        table.setQuery(createStubQueryPayload(caption));
        table.setValues(new ArrayList<>());
        return table;
    }

    public static DataTableRow createStubRow(Map<Field, String> values, boolean pass) {
        DataTableRow row = new DataTableRow();
        row.setValues(values);
        row.setPass(pass);
        row.setResult(null); // Passing null prevents constructor incompatibility with custom Fraction definitions
        return row;
    }

    /**
     * Creates an anonymous subclass of the abstract QueryPayload to bypass the need for Mockito.
     */
    public static QueryPayload createStubQueryPayload(String captionValue) {
        QueryPayload stub = new QueryPayload(null) {
            @Override
            public Set<Field> getNominatorFields() { return Collections.emptySet(); }

            @Override
            public Set<Field> getDenominatorFields() { return Collections.emptySet(); }

            @Override
            public Set<gov.nist.healthcare.iz.darq.analyzer.model.template.DataSelector> getFilterFields() {
                return Collections.emptySet();
            }

            @Override
            public gov.nist.healthcare.iz.darq.analyzer.model.template.QueryThreshold getQueryThreshold() {
                return null;
            }
        };
        stub.setCaption(captionValue);
        return stub;
    }
}