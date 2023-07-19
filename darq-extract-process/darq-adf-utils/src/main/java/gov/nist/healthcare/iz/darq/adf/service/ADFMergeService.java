package gov.nist.healthcare.iz.darq.adf.service;

import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;

import java.util.List;

public interface ADFMergeService {
    ADFile mergeADFiles(List<ADFile> files) throws Exception;
    String compatibilityVersion(ADFile file);
    boolean areMergeable(List<ADFile> files) throws Exception;
}
