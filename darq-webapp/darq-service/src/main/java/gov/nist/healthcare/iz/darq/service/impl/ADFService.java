package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.service.ADFMergeService;
import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.service.exception.InvalidFileFormat;
import gov.nist.healthcare.iz.darq.model.ADFileComponent;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.service.utils.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private ConfigurationService configService;

    UserUploadedFile create(String name, String facility, String ownerId, ADFReader file, List<ADFileComponent> components) throws InvalidFileFormat {
        try {
            this.configService.validateConfigurationPayload(file.getConfigurationPayload());
            String uid = UUID.randomUUID().toString();
            Path dir = Files.createDirectory(Paths.get(PATH+"/"+uid));
            if (dir.toFile().exists()) {
                UserUploadedFile metadata = new UserUploadedFile(
                        name,
                        uid,
                        null,
                        ownerId,
                        file.getMetadata().getAnalysisDate(),
                        new Date(),
                        file.getConfigurationPayload(),
                        "",
                        file.getSummary(),
                        humanReadableByteCount(file.getFileSize(), true),
                        file.getMetadata().getVersion(),
                        file.getMetadata().getBuild(),
                        file.getMetadata().getMqeVersion(),
                        file.getMetadata().getInactiveDetections(),
                        facility,
                        components,
                        file.getMetadata().getTotalAnalysisTime()
                );
                storage.store(metadata);
                // Move File
                Files.move(file.getADFLocation(), Paths.get(dir.toString(), "/" + ADFStorage.ADF_FILENAME));
                return metadata;
            } else {
                throw new Exception("could not create ADF directory");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidFileFormat("Could not upload ADF due to : " + e.getMessage());
        }
    }

    public String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
