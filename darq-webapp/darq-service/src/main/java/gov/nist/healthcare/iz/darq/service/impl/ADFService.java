package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.service.MergeService;
import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import gov.nist.healthcare.iz.darq.model.ADFileComponent;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ADFService {

    @Value("#{environment.QDAR_STORE}")
    private String PATH;
    @Autowired
    private ADFStore<UserUploadedFile> storage;
    @Autowired
    private CryptoUtils crypto;
    @Autowired
    private MergeService mergeService;
    @Autowired
    private ConfigurationService configService;

    private final HashMap<String, HashSet<String>> compatibilityVersions = new HashMap<String, HashSet<String>>() {{
        put("COMPATIBILITY_1", new HashSet<>(Arrays.asList(
                "2.0.0-SNAPSHOT", "2.0.0"
        )));
    }};

    UserUploadedFile create(String name, String facility, String ownerId, byte[] content, ADFile file, List<ADFileComponent> components) throws InvalidFileFormat {
        try {
            this.configService.validateConfigurationPayload(file.getConfiguration());
            String uid = UUID.randomUUID().toString();
            Path dir = Files.createDirectory(Paths.get(PATH+"/"+uid));
            if (dir.toFile().exists()) {
                UserUploadedFile metadata = new UserUploadedFile(
                        name,
                        uid,
                        null,
                        ownerId,
                        file.getAnalysisDate(),
                        new Date(),
                        file.getConfiguration(),
                        "",
                        file.getSummary(),
                        humanReadableByteCount(content.length, true),
                        file.getVersion(),
                        file.getBuild(),
                        file.getMqeVersion(),
                        file.getInactiveDetections(),
                        facility,
                        components
                );
                storage.store(metadata);
                Files.write(Paths.get(dir.toString(), "/" + ADFStorage.ADF_FILENAME), content);
                return metadata;
            } else {
                throw new Exception("could not create ADF directory");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidFileFormat("Could not upload ADF due to : " + e.getMessage());
        }
    }

    public boolean mergeable(List<ADFile> files) throws OperationFailureException {
        // Check CLI/MQE version
        String version = this.compatibilityVersion(files.get(0));
        if(files.stream().anyMatch(file -> {
            String cc = this.compatibilityVersion(file);
            return !cc.equals(version);
        })) {
            throw new OperationFailureException("Files don't have the same compatibility version");
        }

        // Check Configuration
        ConfigurationPayload configuration = files.get(0).getConfiguration();
        if(files.stream().anyMatch(file -> !file.getConfiguration().equals(configuration))) {
            throw new OperationFailureException("Files don't have the same configuration");
        }

        // Check Inactive
        Set<String> inactive = files.get(0).getInactiveDetections();
        if(files.stream().anyMatch(file -> !file.getInactiveDetections().equals(inactive))) {
            throw new OperationFailureException("Files don't have the same active/inactive detections");
        }
        return true;
    }

    public String compatibilityVersion(ADFile file) {
        String version = Optional.ofNullable(file.getVersion()).orElse(RandomStringUtils.random(5));
        String compatibilityVersion = this.compatibilityVersions.entrySet().stream().filter(
                (entry) -> entry.getValue().contains(version)
        ).findFirst().map(Map.Entry::getKey).orElse(version);

        String mqe = Optional.ofNullable(file.getMqeVersion()).orElse(RandomStringUtils.random(5));
        return compatibilityVersion + " - " + mqe;
    }

    public UserUploadedFile merge(String name, String facility, String ownerId, Set<UserUploadedFile> metadataList) throws Exception {
        if(metadataList == null) {
            throw new OperationFailureException("Invalid number of ADF to merge : none");
        }
        if(metadataList.size() <= 1) {
            throw new OperationFailureException("Invalid number of ADF to merge : " + metadataList.size());
        }

        List<ADFile> files = new ArrayList<>();
        for(UserUploadedFile metadata: metadataList) {
            files.add(storage.getFile(metadata.getId()));
        }

        this.mergeable(files);

        ADFile first = files.get(0);
        Map<String, PatientPayload> generalPatientSection = first.getGeneralPatientPayload();
        Map<String, Map<String, ADPayload>> reportingGroupPayload = first.getReportingGroupPayload();
        Summary summary = first.getSummary();

        for(int i = 1; i < files.size(); i++) {
            generalPatientSection = this.mergeService.mergePatientAgeGroup(generalPatientSection, files.get(i).getGeneralPatientPayload());
            reportingGroupPayload = this.mergeService.mergeADPayloadProvider(reportingGroupPayload, files.get(i).getReportingGroupPayload());
            summary = Summary.merge(summary, files.get(i).getSummary());
        }

        ADFile file = new ADFile(
                generalPatientSection,
                reportingGroupPayload,
                first.getConfiguration(),
                summary,
                null,
                first.getVersion(),
                first.getBuild(),
                first.getMqeVersion(),
                first.getInactiveDetections()
        );

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.crypto.encryptContentToFile(file, byteArrayOutputStream);
        return this.create(name, facility, ownerId, byteArrayOutputStream.toByteArray(), file, this.getComponentsListFrom(metadataList));
    }

    List<ADFileComponent> getComponentsListFrom(Set<UserUploadedFile> metadataList) {
        List<ADFileComponent> adFileComponents = new ArrayList<>();
        for(UserUploadedFile metadata: metadataList) {
            if(metadata.isComposed()) {
                adFileComponents.addAll(metadata.getComponents());
            } else {
                adFileComponents.add(this.fileComponentFromMetadata(metadata));
            }
        }

        return adFileComponents;
    }

    ADFileComponent fileComponentFromMetadata(UserUploadedFile metadata) {
        return new ADFileComponent(
                metadata.getId(),
                metadata.getName(),
                metadata.getOwnerId(),
                metadata.getAnalysedOn(),
                metadata.getUploadedOn(),
                metadata.getSize(),
                metadata.getSummary(),
                metadata.getFacilityId()
        );
    }

    public String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
