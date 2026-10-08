import gov.nist.healthcare.iz.darq.analyzer.model.analysis.*;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.*;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.analyzer.service.ReportComparisonService;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ReportComparisonServiceTest {

    private ReportComparisonService comparisonService;

    @Before
    public void setUp() {
        comparisonService = new ReportComparisonService();
    }

    @Test
    public void compare_WhenTemplatesDoNotMatch_ThrowsIllegalArgumentException() {
        AnalysisReport baseReport = createStubReport("report-base", "template-1");
        AnalysisReport newReport = createStubReport("report-new", "template-2");

        try {
            comparisonService.compare(baseReport, newReport);
            fail("Expected an IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException exception) {
            assertEquals("Reports do not share the same ReportTemplate ID.", exception.getMessage());
        }
    }

    @Test
    public void compare_WithDifferingData_CalculatesDifferentialsCorrectly() {
        // 1. Setup Base Report
        AnalysisReport baseReport = createStubReport("report-1", "template-1");

        ReportSectionResult baseSection = createStubSection("section-1", false);
        DataTable baseTable = createStubTable("Query 1", false);

        Map<Field, String> rowIdentifier = new HashMap<>();
        // Using a null key avoids needing to instantiate the unknown Field class/enum.
        // HashMap fully supports a single null key, making it safe for equals/hashCode lookups.
        rowIdentifier.put(null, "ValueA");

        DataTableRow baseRow = createStubRow(rowIdentifier, true);

        baseTable.getValues().add(baseRow);
        baseSection.getData().add(baseTable);
        baseReport.setSections(Collections.singletonList(baseSection));

        // 2. Setup New Report (with modified states)
        AnalysisReport newReport = createStubReport("report-2", "template-1");

        // Change threshold violation to TRUE
        ReportSectionResult newSection = createStubSection("section-1", true);
        DataTable newTable = createStubTable("Query 1", false);

        // Change pass status to FALSE
        DataTableRow newRow = createStubRow(rowIdentifier, false);

        newTable.getValues().add(newRow);
        newSection.getData().add(newTable);
        newReport.setSections(Collections.singletonList(newSection));

        // 3. Execute comparison
        ReportComparisonResult result = comparisonService.compare(baseReport, newReport);

        // 4. Assert Root Result
        assertEquals("report-1", result.getBaseReportId());
        assertEquals("report-2", result.getNewReportId());
        assertEquals("template-1", result.getTemplateId());
        assertEquals(1, result.getSectionDifferentials().size());

        // 5. Assert Section Differential
        SectionDifferential sectionDiff = result.getSectionDifferentials().get(0);
        assertEquals("section-1", sectionDiff.getSectionId());
        assertFalse(sectionDiff.isMissingInNewReport());
        assertTrue("Threshold violation should have changed from false to true", sectionDiff.isThresholdViolationChanged());
        assertEquals(1, sectionDiff.getTableDifferentials().size());

        // 6. Assert Table Differential
        TableDifferential tableDiff = sectionDiff.getTableDifferentials().get(0);
        assertEquals(0, tableDiff.getTableIndex());
        assertEquals("Query 1", tableDiff.getTableCaption());
        assertFalse(tableDiff.isMissingInNewReport());
        assertEquals(1, tableDiff.getRowDifferentials().size());

        // 7. Assert Row Differential
        RowDifferential rowDiff = tableDiff.getRowDifferentials().get(0);
        assertEquals(rowIdentifier, rowDiff.getRowIdentifier());
        assertTrue("Pass status should have changed from true to false", rowDiff.isPassStatusChanged());

        // We expect fractions to be null because we didn't pass real Fraction objects to avoid constructor conflicts
        assertNull(rowDiff.getBaseResult());
        assertNull(rowDiff.getNewResult());
    }

    @Test
    public void compare_WithMissingDataInNewReport_FlagsMissingCorrectly() {
        AnalysisReport baseReport = createStubReport("report-1", "template-1");
        ReportSectionResult baseSection = createStubSection("section-missing", false);
        baseReport.setSections(Collections.singletonList(baseSection));

        // New report has NO sections
        AnalysisReport newReport = createStubReport("report-2", "template-1");
        newReport.setSections(new ArrayList<>());

        ReportComparisonResult result = comparisonService.compare(baseReport, newReport);

        assertEquals(1, result.getSectionDifferentials().size());
        assertTrue(result.getSectionDifferentials().get(0).isMissingInNewReport());
    }

    // --- Helper Methods for Manual Stub Generation ---

    private AnalysisReport createStubReport(String reportId, String templateId) {
        AnalysisReport report = new AnalysisReport();
        report.setId(reportId);

        ReportTemplate template = new ReportTemplate();
        template.setId(templateId);
        report.setReportTemplate(template);

        report.setSections(new ArrayList<>());
        return report;
    }

    private ReportSectionResult createStubSection(String sectionId, boolean thresholdViolation) {
        ReportSectionResult section = new ReportSectionResult();
        section.setId(sectionId);
        section.setThresholdViolation(thresholdViolation);
        section.setData(new ArrayList<>());
        section.setChildren(new ArrayList<>());
        return section;
    }

    private DataTable createStubTable(String caption, boolean thresholdViolation) {
        DataTable table = new DataTable();
        table.setThresholdViolation(thresholdViolation);
        table.setQuery(createStubQueryPayload(caption));
        table.setValues(new ArrayList<>());
        return table;
    }

    private DataTableRow createStubRow(Map<Field, String> values, boolean pass) {
        DataTableRow row = new DataTableRow();
        row.setValues(values);
        row.setPass(pass);
        row.setResult(null); // Passing null prevents constructor incompatibility with custom Fraction definitions
        return row;
    }

    /**
     * Creates an anonymous subclass of the abstract QueryPayload to bypass the need for Mockito.
     */
    private QueryPayload createStubQueryPayload(String captionValue) {
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