import gov.nist.healthcare.iz.darq.analyzer.model.comparison.ReportComparisonResult;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.RowDifferential;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.SectionDifferential;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.TableDifferential;
import gov.nist.healthcare.iz.darq.analyzer.service.comparison.ReportComparisonCsvExportService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReportComparisonCsvExportServiceTest {

    private ReportComparisonCsvExportService exportService;

    @Before
    public void setUp() {
        exportService = new ReportComparisonCsvExportService();
    }

    @Test
    public void exportToCsv_WithNullResult_GeneratesHeaderOnly() {
        String csv = exportService.exportToCsv(new ReportComparisonResult(), "2023", "2024");

        String expectedHeader = "Measure Code,Measure Name,Threshold,2023,2023 Outcome,2024,2024 Outcome,% Point Change Improvement,Measure Outcome\n";//,Min.,Mean,Max.,% of IIS that Pass\n";
        assertEquals("Should generate only the header line", expectedHeader, csv);
    }

    @Test
    public void exportToCsv_WithStatusTransitions_CalculatesOutcomesCorrectly() {
        ReportComparisonResult result = new ReportComparisonResult();
        SectionDifferential section = new SectionDifferential();
        TableDifferential table = new TableDifferential();
        table.setTableCaption("Test Measure");
        table.setRowDifferentials(new ArrayList<>());

        // Row 1: Met -> Met (No Change)
        table.getRowDifferentials().add(createTestRow(true, 100.0, true, 100.0, ">99%"));
        // Row 2: Not Met -> Met (Better)
        table.getRowDifferentials().add(createTestRow(false, 99.0, true, 99.1, ">99%"));
        // Row 3: Met -> Not Met (Worse)
        table.getRowDifferentials().add(createTestRow(true, 95.6, false, 84.0, ">85%"));

        section.getTableDifferentials().add(table);
        result.setSectionDifferentials(Collections.singletonList(section));

        String csv = exportService.exportToCsv(result, "2023", "2024");
        String[] lines = csv.split(ReportComparisonCsvExportService.NEW_LINE);

        assertEquals("Should have 1 header and 3 data rows", 4, lines.length);

        // Assert Row 1 - No Change
        assertTrue("Row 1 should map to 'Met' for both and 'No Change'",
                lines[1].contains("Met,100.00%,Met,0.00%,No Change"));

        // Assert Row 2 - Better (+0.10% improvement)
        assertTrue("Row 2 should transition to Better with 0.10% improvement",
                lines[2].contains("Not Met,99.10%,Met,0.10%,Better"));

        // Assert Row 3 - Worse (-11.60% regression)
        assertTrue("Row 3 should transition to Worse with -11.60% improvement",
                lines[3].contains("Met,84.00%,Not Met,-11.60%,Worse"));
    }

    @Test
    public void exportToCsv_WithSpecialCharacters_EscapesStringsCorrectly() {
        ReportComparisonResult result = new ReportComparisonResult();
        SectionDifferential section = new SectionDifferential();
        TableDifferential table = new TableDifferential();

        // Include comma in table caption
        table.setTableCaption("Measure 1.1, Patient Name");

        RowDifferential row = createTestRow(true, 100.0, true, 100.0, ">99%");
        row.setThresholdText(">99%, required by \"CDC\""); // Includes comma and quotes

        table.setRowDifferentials(Collections.singletonList(row));
        section.getTableDifferentials().add(table);
        result.setSectionDifferentials(Collections.singletonList(section));

        String csv = exportService.exportToCsv(result, "Base Report", "New Report");
        String[] lines = csv.split(ReportComparisonCsvExportService.NEW_LINE);

        // Verify escaping behavior
        assertTrue("Measure name containing comma should be wrapped in quotes",
                lines[1].contains("\"Measure 1.1, Patient Name\""));
        assertTrue("Threshold containing quotes and comma should be escaped properly",
                lines[1].contains("\">99%, required by \"\"CDC\"\"\""));
    }

    @Test
    public void exportToCsv_WithNullValues_FormatsAsNA() {
        ReportComparisonResult result = new ReportComparisonResult();
        SectionDifferential section = new SectionDifferential();
        TableDifferential table = new TableDifferential();
        table.setTableCaption("Null Measure");

        RowDifferential row = new RowDifferential();
        row.setBasePass(false);
        row.setNewPass(false);
        row.setBasePercent(null);
        row.setNewPercent(null);

        table.setRowDifferentials(Collections.singletonList(row));
        section.getTableDifferentials().add(table);
        result.setSectionDifferentials(Collections.singletonList(section));

        String csv = exportService.exportToCsv(result, "2023", "2024");
        String[] lines = csv.split(ReportComparisonCsvExportService.NEW_LINE);

        assertTrue("Null percents should output N/A", lines[1].contains("N/A,Not Met,N/A,Not Met,0.00%"));
    }

    // --- Helper Method to instantiate RowDifferentials ---

    private RowDifferential createTestRow(boolean basePass, Double basePercent, boolean newPass, Double newPercent, String thresholdText) {
        RowDifferential row = new RowDifferential();
        row.setBasePass(basePass);
        row.setBasePercent(basePercent);
        row.setNewPass(newPass);
        row.setNewPercent(newPercent);
        row.setThresholdText(thresholdText);
        return row;
    }
}