package gov.nist.healthcare.iz.darq.service.analysis;

import gov.nist.healthcare.iz.darq.model.ActiveJob;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import javassist.NotFoundException;

import java.util.List;
import java.util.Map;

public interface AnalysisJobRunner {
    AnalysisJob addJob(String name, String templateId, String adfId, String user) throws JobRunningException;
    AnalysisJob startJob(AnalysisJob job) throws JobRunningException;
    AnalysisJob stopJob(String jobId, String user) throws JobRunningException;;
    AnalysisJob failJob(AnalysisJob job, String reason) throws JobRunningException;;
    List<ActiveJob> getAllJobsForUser(String user);
    ActiveJob getJobForUser(String id, String user);
    Map<String, Double> progressMap(String user);
}
