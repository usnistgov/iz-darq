package gov.nist.healthcare.iz.darq.analyzer.service.comparison;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTableRow;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.ReportSectionResult;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.ReportComparisonResult;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.RowDifferential;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.SectionDifferential;
import gov.nist.healthcare.iz.darq.analyzer.model.comparison.TableDifferential;
import gov.nist.healthcare.iz.darq.analyzer.model.template.Threshold;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ReportComparisonService {

    public ReportComparisonResult compare(AnalysisReport baseReport, AnalysisReport newReport) {
        if (baseReport.getReportTemplate() == null || newReport.getReportTemplate() == null) {
            throw new IllegalArgumentException("Both reports must have a ReportTemplate assigned.");
        }

        if (!Objects.equals(baseReport.getReportTemplate().getId(), newReport.getReportTemplate().getId())) {
            throw new IllegalArgumentException("Reports do not share the same ReportTemplate ID.");
        }

        ReportComparisonResult result = new ReportComparisonResult();
        result.setBaseReportId(baseReport.getId());
        result.setNewReportId(newReport.getId());
        result.setTemplateId(baseReport.getReportTemplate().getId());

        result.setSectionDifferentials(compareSections(baseReport.getSections(), newReport.getSections()));

        return result;
    }

    private List<SectionDifferential> compareSections(List<ReportSectionResult> baseSections, List<ReportSectionResult> newSections) {
        if (baseSections == null) return Collections.emptyList();

        Map<String, ReportSectionResult> newSectionsMap = newSections != null ?
                newSections.stream()
                        .filter(s -> s.getId() != null)
                        .collect(Collectors.toMap(ReportSectionResult::getId, s -> s))
                : Collections.emptyMap();

        return baseSections.stream()
                .filter(base -> base.getId() != null)
                .map(base -> {
                    ReportSectionResult newSec = newSectionsMap.get(base.getId());
                    SectionDifferential diff = new SectionDifferential();
                    diff.setSectionId(base.getId());

                    if (newSec == null) {
                        diff.setMissingInNewReport(true);
                        return diff;
                    }

                    diff.setThresholdViolationChanged(base.isThresholdViolation() != newSec.isThresholdViolation());
                    diff.setChildren(compareSections(base.getChildren(), newSec.getChildren()));
                    diff.setTableDifferentials(compareDataTables(base.getData(), newSec.getData()));

                    return diff;
                })
                .collect(Collectors.toList());
    }

    private List<TableDifferential> compareDataTables(List<DataTable> baseTables, List<DataTable> newTables) {
        if (baseTables == null) return Collections.emptyList();

        List<DataTable> safeNewTables = newTables != null ? newTables : Collections.emptyList();

        return IntStream.range(0, baseTables.size())
                .mapToObj(i -> {
                    DataTable baseTable = baseTables.get(i);
                    TableDifferential diff = new TableDifferential();

                    String caption = (baseTable.getQuery() != null) ? baseTable.getQuery().getCaption() : "Unknown";
                    diff.setTableCaption(caption);
                    diff.setTableIndex(i);

                    if (i >= safeNewTables.size()) {
                        diff.setMissingInNewReport(true);
                        return diff;
                    }

                    DataTable newTable = safeNewTables.get(i);
                    diff.setThresholdViolationChanged(baseTable.isThresholdViolation() != newTable.isThresholdViolation());
                    diff.setRowDifferentials(compareRows(baseTable.getValues(), newTable.getValues()));

                    return diff;
                })
                .collect(Collectors.toList());
    }

    private List<RowDifferential> compareRows(List<DataTableRow> baseRows, List<DataTableRow> newRows) {
        if (baseRows == null) return Collections.emptyList();

        Map<Map<Field, String>, DataTableRow> newRowsMap = newRows != null ?
                newRows.stream()
                        .filter(r -> r.getValues() != null)
                        .collect(Collectors.toMap(DataTableRow::getValues, r -> r, (r1, r2) -> r1))
                : Collections.emptyMap();

        return baseRows.stream()
                .filter(baseRow -> baseRow.getValues() != null)
                .map(baseRow -> {
                    DataTableRow newRow = newRowsMap.get(baseRow.getValues());
                    RowDifferential diff = new RowDifferential();
                    diff.setRowIdentifier(baseRow.getValues());

                    if (newRow == null) {
                        diff.setMissingInNewReport(true);
                        return diff;
                    }

                    diff.setPassStatusChanged(baseRow.isPass() != newRow.isPass());

                    // --- NEW FIELDS FOR CSV EXPORT ---
                    diff.setBasePass(baseRow.isPass());
                    diff.setNewPass(newRow.isPass());

                    diff.setThresholdText(formatThreshold(baseRow.getThreshold()));

                    // Assuming Fraction has a way to extract the double/percentage value.
                    // Replace 'extractPercent' with your actual Fraction math (e.g. fraction.getPercent())
                    diff.setBasePercent(extractPercent(baseRow.getEffectiveResult()));
                    diff.setNewPercent(extractPercent(newRow.getEffectiveResult()));

                    diff.setBaseResult(baseRow.getEffectiveResult());
                    diff.setNewResult(newRow.getEffectiveResult());

                    return diff;
                })
                .collect(Collectors.toList());
    }

    /**
     * Renders a threshold the way the report UI does, e.g. ">99%" or "<5.5%".
     */
    private String formatThreshold(Threshold threshold) {
        if (threshold == null || threshold.getComparator() == null) {
            return "";
        }
        String symbol;
        switch (threshold.getComparator()) {
            case GT:
                symbol = ">";
                break;
            case LT:
                symbol = "<";
                break;
            default:
                return "";
        }
        double value = threshold.getValue();
        String valueText = value == Math.floor(value) && !Double.isInfinite(value)
                ? String.valueOf((long) value)
                : String.valueOf(value);
        return symbol + valueText + "%";
    }

    /**
     * Helper method to convert your Fraction object into a usable percentage double.
     * Implement this based on the exact properties of gov.nist.healthcare.iz.darq.digest.domain.Fraction
     */
    private Double extractPercent(Fraction fraction) {
        if (fraction == null) return 0.0;
        // Example: return (double) fraction.getCount() / fraction.getTotal() * 100;
        return 0.0; // TODO: Map to actual Fraction logic
    }
}