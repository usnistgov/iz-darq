package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.domain.trait.AssignableToFacility;
import gov.nist.healthcare.domain.trait.Owned;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;

import java.util.*;

public class UserUploadedFile extends ADFMetaData implements Owned, AssignableToFacility {

    String facilityId;
    List<ADFileComponent> components;
    boolean composed;

    public UserUploadedFile() {
    }

    public UserUploadedFile(String name, String path, String owner, String ownerId, Date analysedOn, Date uploadedOn, ConfigurationPayload configuration, String keyHash, Summary summary, String size, String version, String build, String mqeVersion, Set<String> inactiveDetections, String facilityId, long totalAnalysisTime) {
        super(name, path, owner, ownerId, analysedOn, uploadedOn, configuration, keyHash, summary, size, version, build, mqeVersion, inactiveDetections, totalAnalysisTime);
        this.facilityId = facilityId;
        this.composed = false;
    }

    public UserUploadedFile(String name, String path, String owner, String ownerId, Date analysedOn, Date uploadedOn, ConfigurationPayload configuration, String keyHash, Summary summary, String size, String version, String build, String mqeVersion, Set<String> inactiveDetections, String facilityId, List<ADFileComponent> componentsRef, long totalAnalysisTime) {
        super(name, path, owner, ownerId, analysedOn, uploadedOn, configuration, keyHash, summary, size, version, build, mqeVersion, inactiveDetections, totalAnalysisTime);
        this.facilityId = facilityId;
        this.composed = componentsRef != null && componentsRef.size() > 1;
        if(componentsRef != null && componentsRef.size() <= 1) {
            throw new IllegalArgumentException("Invalid components, size : " + componentsRef.size());
        }
        this.components = componentsRef;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public List<ADFileComponent> getComponents() {
        return components;
    }

    public boolean isComposed() {
        return composed;
    }

    public void setComposed(boolean composed) {
        this.composed = composed;
    }
}
