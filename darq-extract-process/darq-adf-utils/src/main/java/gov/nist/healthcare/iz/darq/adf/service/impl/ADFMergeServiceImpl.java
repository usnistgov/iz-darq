package gov.nist.healthcare.iz.darq.adf.service.impl;

import gov.nist.healthcare.iz.darq.adf.service.ADFMergeService;
import gov.nist.healthcare.iz.darq.adf.service.MergeService;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ADFMergeServiceImpl implements ADFMergeService {
    @Autowired
    private MergeService mergeService;

    private final HashMap<String, HashSet<String>> compatibilityVersions = new HashMap<String, HashSet<String>>() {{
        put("COMPATIBILITY_1", new HashSet<>(Arrays.asList(
                "2.0.0-SNAPSHOT", "2.0.0", "2.0.1", "2.0.2", "2.0.3"
        )));
    }};

    @Override
    public ADFile mergeADFiles(List<ADFile> files) throws Exception {
        this.areMergeable(files);

        ADFile first = files.get(0);
        Map<String, PatientPayload> generalPatientSection = first.getGeneralPatientPayload();
        Map<String, Map<String, ADPayload>> reportingGroupPayload = first.getReportingGroupPayload();
        Summary summary = first.getSummary();

        for(int i = 1; i < files.size(); i++) {
            generalPatientSection = this.mergeService.mergePatientAgeGroup(generalPatientSection, files.get(i).getGeneralPatientPayload());
            reportingGroupPayload = this.mergeService.mergeADPayloadProvider(reportingGroupPayload, files.get(i).getReportingGroupPayload());
            summary = Summary.merge(summary, files.get(i).getSummary());
        }

        return new ADFile(
                generalPatientSection,
                reportingGroupPayload,
                first.getConfiguration(),
                summary,
                null,
                first.getVersion(),
                first.getBuild(),
                first.getMqeVersion(),
                first.getInactiveDetections(),
                0
        );
    }

    @Override
    public String compatibilityVersion(ADFile file) {
        String version = Optional.ofNullable(file.getVersion()).orElse(this.generateRandomString(5));
        String compatibilityVersion = this.compatibilityVersions.entrySet().stream().filter(
                (entry) -> entry.getValue().contains(version)
        ).findFirst().map(Map.Entry::getKey).orElse(version);

        String mqe = Optional.ofNullable(file.getMqeVersion()).orElse(this.generateRandomString(5));
        return compatibilityVersion + " - " + mqe;
    }

    public String generateRandomString(int size) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public boolean areMergeable(List<ADFile> files) throws Exception {
        // Check CLI/MQE version
        String version = this.compatibilityVersion(files.get(0));
        if(files.stream().anyMatch(file -> {
            String cc = this.compatibilityVersion(file);
            return !cc.equals(version);
        })) {
            throw new Exception("Files don't have the same compatibility version");
        }

        // Check Configuration
        ConfigurationPayload configuration = files.get(0).getConfiguration();
        if(files.stream().anyMatch(file -> !file.getConfiguration().equals(configuration))) {
            throw new Exception("Files don't have the same configuration");
        }

        // Check Inactive
        Set<String> inactive = files.get(0).getInactiveDetections();
        if(files.stream().anyMatch(file -> !file.getInactiveDetections().equals(inactive))) {
            throw new Exception("Files don't have the same active/inactive detections");
        }
        return true;
    }
}
