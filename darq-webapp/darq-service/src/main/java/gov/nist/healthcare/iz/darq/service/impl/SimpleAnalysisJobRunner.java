package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.model.ActiveJob;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.model.JobStatus;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.AnalysisJobRepository;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;
import gov.nist.healthcare.iz.darq.service.analysis.AnalysisJobRunner;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SimpleAnalysisJobRunner implements AnalysisJobRunner {

    Map<String, ActiveJob> running = new ConcurrentHashMap<>();

    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    ADFMetaDataRepository adfMetaDataRepository;
    @Autowired
    AnalysisJobRepository analysisJobRepository;

    @PostConstruct
    public void clean() {
        this.running = new ConcurrentHashMap<>();
        List<AnalysisJob> jobs = this.analysisJobRepository.findByStatus(JobStatus.RUNNING);
        for(AnalysisJob job: jobs) {
            this.failJob(job, "Job incorrectly stopped, perhaps due to application shutdown");
        }
    }

    @PreDestroy
    public void shutdown() {
        List<AnalysisJob> jobs = this.running.values().stream().map((j) -> j.getJob()).collect(Collectors.toList());
        for(AnalysisJob job: jobs) {
            this.failJob(job, "Job stopped due to application shutdown");
        }
    }

    @Override
    public AnalysisJob addJob(String name, String templateId, String adfId, String user) throws JobRunningException {
        ReportTemplate template = this.templateRepository.findMineOrReadOnly(templateId, user);
        ADFMetaData adf = this.adfMetaDataRepository.findByIdAndOwner(adfId, user);
        if(template == null) {
            throw new JobRunningException("Report Template" + templateId + " Not Found");
        }

        if(adf == null) {
            throw new JobRunningException("ADF " + adfId + " Not Found");
        }

        AnalysisJob job = new AnalysisJob(name, adfId, adf.getName(), template, user);
        this.analysisJobRepository.save(job);
        this.startJob(job);
        return job;
    }

    @Override
    public AnalysisJob startJob(AnalysisJob job) throws JobRunningException {
        if(job.getStatus() == JobStatus.RUNNING || running.containsKey(job.getId())) {
            throw new JobRunningException("Job " + job.getName() + " Already Running ");
        }
        job.setStatus(JobStatus.RUNNING);
        job.setStartTime(new Date());
        /// TODO  START JOB
        running.put(job.getId(), new ActiveJob(job, new Fraction()));
        this.analysisJobRepository.save(job);
        return job;
    }

    @Override
    public AnalysisJob stopJob(String jobId, String user) {
        return null;
    }

    @Override
    public AnalysisJob failJob(AnalysisJob job, String reason) {
        job.setStatus(JobStatus.FAILED);
        job.setEndTime(new Date());
        job.setFailure(reason);
        this.running.remove(job.getId());
        this.analysisJobRepository.save(job);
        return job;
    }

    @Override
    public List<ActiveJob> getAllJobsForUser(String user) {
        return this.running.values().stream().filter((job) -> job.getJob().getOwner().equals(user)).collect(Collectors.toList());
    }

    @Override
    public ActiveJob getJobForUser(String id, String user) {
        ActiveJob job = this.running.get(id);
        if(job != null && job.getJob().getOwner().equals(user)) {
            return job;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Double> progressMap(String user) {
        List<ActiveJob> jobs = this.getAllJobsForUser(user);
        return jobs.stream().collect(
                Collectors.groupingBy(
                        (job) -> job.getJob().getId(),
                        Collectors.collectingAndThen(
                            Collectors.toList(),
                            (List<ActiveJob> list) -> {
                                return list.get(0).getProgress().percent();
                             })
                        )
                );
    }
}
