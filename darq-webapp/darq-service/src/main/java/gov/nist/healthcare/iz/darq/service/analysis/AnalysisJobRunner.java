package gov.nist.healthcare.iz.darq.service.analysis;

import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;

import java.util.List;


public interface AnalysisJobRunner {
    AnalysisJob addJob(String name, String templateId, String adfId, String user) throws JobRunningException, NotFoundException;
    AnalysisJob startJob(AnalysisJob job) throws Exception;
    AnalysisJob failJob(AnalysisJob job, String reason) throws JobRunningException;
    List<AnalysisJob> getAllJobsForUserAndFacility(String user, String facilityId);
    boolean deleteJobForUser(String id, String user) throws JobRunningException;

}
