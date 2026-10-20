package gov.nist.healthcare.iz.darq.analyzer.service.comparison;

import gov.nist.healthcare.iz.darq.analyzer.model.comparison.ReportComparisonResult;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.RowDifferential;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.SectionDifferential;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.TableDifferential;
import org.springframework.stereotype.Service;

@Service
public class ReportComparisonCsvExportService {

    // --- CSV structural constants ---
    public static final String CSV_SEPARATOR = ",";
    public static final String NEW_LINE = "\n";

    // --- Header constants ---
    public static final String HEADER_MEASURE_CODE = "Measure Code";
    public static final String HEADER_MEASURE_NAME = "Measure Name";
    public static final String HEADER_THRESHOLD = "Threshold";
    public static final String HEADER_OUTCOME_SUFFIX = " Outcome";
    public static final String HEADER_POINT_CHANGE = "% Point Change Improvement";
    public static final String HEADER_MEASURE_OUTCOME = "Measure Outcome";
    public static final String HEADER_MIN = "Min.";
    public static final String HEADER_MEAN = "Mean";
    public static final String HEADER_MAX = "Max.";
    public static final String HEADER_PERCENT_IIS_PASS = "% of IIS that Pass";

    // --- Value constants ---
    public static final String STATUS_MET = "Met";
    public static final String STATUS_NOT_MET = "Not Met";
    public static final String OUTCOME_BETTER = "Better";
    public static final String OUTCOME_WORSE = "Worse";
    public static final String OUTCOME_NO_CHANGE = "No Change";
    public static final String TEXT_NA = "N/A";

    /**
     * Generates a CSV formatted string matching the differential report structure.
     *
     * @param comparisonResult The calculated comparison payload
     * @param baseReportNickname e.g., "2023"
     * @param newReportNickname  e.g., "2024"
     * @return Raw CSV string
     */
    public String exportToCsv(ReportComparisonResult comparisonResult, String baseReportNickname, String newReportNickname) {
        StringBuilder csv = new StringBuilder();

        // 1. Build Header
        csv.append(HEADER_MEASURE_CODE).append(CSV_SEPARATOR)
                .append(HEADER_MEASURE_NAME).append(CSV_SEPARATOR)
                .append(HEADER_THRESHOLD).append(CSV_SEPARATOR)
                .append(escape(baseReportNickname)).append(CSV_SEPARATOR)
                .append(escape(baseReportNickname + HEADER_OUTCOME_SUFFIX)).append(CSV_SEPARATOR)
                .append(escape(newReportNickname)).append(CSV_SEPARATOR)
                .append(escape(newReportNickname + HEADER_OUTCOME_SUFFIX)).append(CSV_SEPARATOR)
                .append(HEADER_POINT_CHANGE).append(CSV_SEPARATOR)
                .append(HEADER_MEASURE_OUTCOME).append(CSV_SEPARATOR);
//                .append(HEADER_MIN).append(CSV_SEPARATOR)
//                .append(HEADER_MEAN).append(CSV_SEPARATOR)
//                .append(HEADER_MAX).append(CSV_SEPARATOR)
//                .append(HEADER_PERCENT_IIS_PASS).append(NEW_LINE);

        if (comparisonResult == null || comparisonResult.getSectionDifferentials() == null) {
            return csv.toString();
        }

        // 2. Iterate through Differentials
        for (SectionDifferential section : comparisonResult.getSectionDifferentials()) {
            if (section.getTableDifferentials() == null) continue;

            for (TableDifferential table : section.getTableDifferentials()) {
                if (table.getRowDifferentials() == null) continue;

                for (RowDifferential row : table.getRowDifferentials()) {

                    // Construct Measure Name
                    String measureName = table.getTableCaption() != null ? table.getTableCaption() : "";
                    if (row.getRowIdentifier() != null && !row.getRowIdentifier().isEmpty()) {
                        measureName += " " + row.getRowIdentifier().values().toString();
                    }

                    // Determine Measure Outcome logic based on pass transition
                    String measureOutcome = OUTCOME_NO_CHANGE;
                    if (!row.isBasePass() && row.isNewPass()) {
                        measureOutcome = OUTCOME_BETTER;
                    } else if (row.isBasePass() && !row.isNewPass()) {
                        measureOutcome = OUTCOME_WORSE;
                    }

                    // Calculate point change
                    double pointChange = (row.getNewPercent() != null && row.getBasePercent() != null)
                            ? (row.getNewPercent() - row.getBasePercent())
                            : 0.0;

                    // Append Row
                    csv.append("").append(CSV_SEPARATOR) // Measure Code (left blank in example)
                            .append(escape(measureName)).append(CSV_SEPARATOR)
                            .append(escape(row.getThresholdText())).append(CSV_SEPARATOR)
                            .append(formatPercent(row.getBasePercent())).append(CSV_SEPARATOR)
                            .append(row.isBasePass() ? STATUS_MET : STATUS_NOT_MET).append(CSV_SEPARATOR)
                            .append(formatPercent(row.getNewPercent())).append(CSV_SEPARATOR)
                            .append(row.isNewPass() ? STATUS_MET : STATUS_NOT_MET).append(CSV_SEPARATOR)
                            .append(formatPercent(pointChange)).append(CSV_SEPARATOR)
                            .append(measureOutcome).append(CSV_SEPARATOR)
//                            .append(formatPercent(row.getMinPercent())).append(CSV_SEPARATOR)
//                            .append(formatPercent(row.getMeanPercent())).append(CSV_SEPARATOR)
//                            .append(formatPercent(row.getMaxPercent())).append(CSV_SEPARATOR)
//                            .append(formatPercent(row.getPercentOfReportsPassing()))
                            .append(NEW_LINE);
                }
            }
        }

        return csv.toString();
    }

    private String escape(String value) {
        if (value == null) return "";
        // If value contains a comma, quote, or newline, wrap it in quotes and double internal quotes
        if (value.contains(CSV_SEPARATOR) || value.contains("\"") || value.contains(NEW_LINE)) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String formatPercent(Double value) {
        if (value == null) return TEXT_NA;
        // Example logic: formats 100.0 as "100.00%"
        return String.format("%.2f%%", value);
    }
}