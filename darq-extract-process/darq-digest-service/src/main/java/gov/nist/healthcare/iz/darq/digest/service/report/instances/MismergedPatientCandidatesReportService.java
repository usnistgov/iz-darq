package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.localreport.AggregateLocalReportService;
import gov.nist.healthcare.iz.darq.localreport.AggregateRow;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.immregistries.mqe.validator.detection.Detection;

import java.util.*;
import java.util.stream.Collectors;

public class MismergedPatientCandidatesReportService extends AggregateLocalReportService {

    public static final String FILENAME = "mismerged_candidates.csv";

    // One entry per report column, in column order. A column is marked when the patient has any
    // of its detections. DETECTIONS is derived from this, so a detection cannot be treated as a
    // mismerge signal without also belonging to a column.
    private static final List<List<Detection>> COLUMN_DETECTIONS = Arrays.asList(
            Collections.singletonList(
                    Detection.VaccineEvaluationHasInvalidDoses5orMore
            ),
            Collections.singletonList(
                    Detection.PatientFluSeasonDoseCountIs2OrMore
            ),
            Arrays.asList(
                    Detection.PatientCovid2021DoseCountIs4OrMore,
                    Detection.PatientCovid2021DoseCountIs5OrMore,
                    Detection.PatientCovid2021DoseCountIs6OrMore
            )
    );

    public static final Set<Detection> DETECTIONS = Collections.unmodifiableSet(
            new HashSet<>(
                    COLUMN_DETECTIONS.stream()
                            .flatMap(List::stream)
                            .collect(Collectors.toList())
            )
    );

    public MismergedPatientCandidatesReportService() {
        super(FILENAME, DETECTIONS.stream().map(Detection::getMqeMqeCode).toArray(String[]::new));
    }

    @Override
    public List<String> getHeader() {
        return Arrays.asList(
                "Patient ID",
                "More than 5 invalid doses",
                "Too many flu doses",
                "Too many COVID doses",
                // AggregateLocalReportService appends the aggregate count as a trailing column,
                // so the header needs an entry for it or it sits one short of every data row.
                "Count"
        );
    }

    @Override
    public List<AggregateRow> getRows(PreProcessRecord context, RecordDetectionEngineResult detectionEngineResult) {
        List<AggregateRow> rows = new ArrayList<>();
        List<Detection> mismergedDetections = getMismergeDetections(
                detectionEngineResult.getPatientDetections()
        );
        if (!mismergedDetections.isEmpty()) {
            DqString patientID = context.getRecord().patient.patID;
            if (patientID.hasValue()) {
                List<String> columns = COLUMN_DETECTIONS.stream()
                        .map((columnDetections) -> mark(mismergedDetections, columnDetections))
                        .collect(Collectors.toList());
                rows.add(
                        new AggregateRow(
                                Collections.singletonList(
                                        patientID.getValue()
                                ),
                                columns
                        )
                );
            }
        }

        return rows;
    }

    String mark(List<Detection> mismergedDetections, List<Detection> columnDetections) {
        return mismergedDetections.stream().anyMatch(columnDetections::contains) ? "X" : "";
    }

    List<Detection> getMismergeDetections(Map<String, DetectionSum> patientDetections) {
        if (patientDetections == null) {
            return Collections.emptyList();
        }
        return DETECTIONS.stream()
                .filter((detection) -> {
                    DetectionSum sum = patientDetections.get(detection.getMqeMqeCode());
                    return sum != null && sum.exists();
                })
                .collect(Collectors.toList());
    }

}