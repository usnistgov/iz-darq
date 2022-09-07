package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.iz.darq.adf.service.ADFMergeService;
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
    private ConfigurationService configService;
    @Autowired
    private ADFMergeService adfMergeService;

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
                        components,
                        file.getTotalAnalysisTime()
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

        ADFile file = this.adfMergeService.mergeADFiles(files);

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
