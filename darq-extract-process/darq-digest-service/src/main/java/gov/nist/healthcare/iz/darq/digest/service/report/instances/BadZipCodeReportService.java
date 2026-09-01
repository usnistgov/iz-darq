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

public class BadZipCodeReportService extends AggregateLocalReportService {

    public final static String FILENAME = "zip_codes.csv";

    public static final Set<Detection> DETECTIONS = new HashSet<>(Arrays.asList(
            Detection.PatientAddressZipIsInvalid
//            Detection.PatientAddressZipIsMissing,
//            Detection.PatientAddressZipIsPresent
    ));

    public BadZipCodeReportService() {
        super(FILENAME, DETECTIONS.stream().map(Detection::getMqeMqeCode).toArray(String[]::new));
    }

    @Override
    public List<AggregateRow> getRows(PreProcessRecord context, RecordDetectionEngineResult detections) {
        List<AggregateRow> rows = new ArrayList<>();
        boolean hasBadZipCode = DETECTIONS.stream()
                .map(Detection::getMqeMqeCode)
                .anyMatch((detection) -> detections.getPatientDetections().containsKey(detection) && detections.getPatientDetections().get(detection).exists());
        if (hasBadZipCode) {
            DqString zip = context.getRecord().patient.address.zip;
            if(zip.hasValue()) {
                List<Detection> ZipCodeDetections = getZipDetections(
                        detections.getPatientDetections()
                );
                rows.add(
                        new AggregateRow(
                                Collections.singletonList(
                                        zip.getValue()
                                ),
                                Collections.singletonList(
                                        ZipCodeDetections
                                                .stream()
                                                .map(this::getDetectionText)
                                                .collect(Collectors.joining(" "))
                                )
                        )
                );
            }
        }

        return rows;

    }

    @Override
    public List<String> getHeader() {
        return Arrays.asList(
                "Zip",
                "Detections",
                "Count"
        );
    }

    List<Detection> getZipDetections(Map<String, DetectionSum> patientDetections) {
        List<Detection> zipDetections = new ArrayList<>();
        if(patientDetections != null) {
            patientDetections.forEach((code, v) -> {
                if(v.exists()) {
                    DETECTIONS.forEach((detection) -> {
                        if(detectionIs(detection, code)) {
                            zipDetections.add(detection);
                        }
                    });
                }
            });
        }
        return zipDetections;
    }

    String getDetectionText(Detection detection) {
        return detection.getMqeMqeCode() + " - " + detection.getDisplayText();
    }

    boolean detectionIs(Detection detection, String code) {
        return detection.mqeCode.name().equals(code);
    }
}
