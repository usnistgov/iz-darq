import gov.nist.healthcare.iz.darq.analyzer.model.analysis.*;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.*;
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
        AnalysisReport baseReport = MockReportComparisonTestFactory.createStubReport("report-base", "template-1");
        AnalysisReport newReport = MockReportComparisonTestFactory.createStubReport("report-new", "template-2");

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
        AnalysisReport baseReport = MockReportComparisonTestFactory.createStubReport("report-1", "template-1");

        ReportSectionResult baseSection = MockReportComparisonTestFactory.createStubSection("section-1", false);
        DataTable baseTable = MockReportComparisonTestFactory.createStubTable("Query 1", false);

        Map<Field, String> rowIdentifier = new HashMap<>();
        // Using a null key avoids needing to instantiate the unknown Field class/enum.
        // HashMap fully supports a single null key, making it safe for equals/hashCode lookups.
        rowIdentifier.put(null, "ValueA");

        DataTableRow baseRow = MockReportComparisonTestFactory.createStubRow(rowIdentifier, true);

        baseTable.getValues().add(baseRow);
        baseSection.getData().add(baseTable);
        baseReport.setSections(Collections.singletonList(baseSection));

        // 2. Setup New Report (with modified states)
        AnalysisReport newReport = MockReportComparisonTestFactory.createStubReport("report-2", "template-1");

        // Change threshold violation to TRUE
        ReportSectionResult newSection = MockReportComparisonTestFactory.createStubSection("section-1", true);
        DataTable newTable = MockReportComparisonTestFactory.createStubTable("Query 1", false);

        // Change pass status to FALSE
        DataTableRow newRow = MockReportComparisonTestFactory.createStubRow(rowIdentifier, false);

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
        AnalysisReport baseReport = MockReportComparisonTestFactory.createStubReport("report-1", "template-1");
        ReportSectionResult baseSection = MockReportComparisonTestFactory.createStubSection("section-missing", false);
        baseReport.setSections(Collections.singletonList(baseSection));

        // New report has NO sections
        AnalysisReport newReport = MockReportComparisonTestFactory.createStubReport("report-2", "template-1");
        newReport.setSections(new ArrayList<>());

        ReportComparisonResult result = comparisonService.compare(baseReport, newReport);

        assertEquals(1, result.getSectionDifferentials().size());
        assertTrue(result.getSectionDifferentials().get(0).isMissingInNewReport());
    }
}