package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.analyzer.service.ReportService;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.model.JobStatus;
import gov.nist.healthcare.iz.darq.model.UserUploadedFile;
import gov.nist.healthcare.iz.darq.repository.ADFMetaDataRepository;
import gov.nist.healthcare.iz.darq.repository.AnalysisJobRepository;
import gov.nist.healthcare.iz.darq.repository.AnalysisReportRepository;
import gov.nist.healthcare.iz.darq.repository.TemplateRepository;
import gov.nist.healthcare.iz.darq.service.analysis.AnalysisJobRunner;
import gov.nist.healthcare.iz.darq.service.analysis.RunnableJob;
import gov.nist.healthcare.iz.darq.service.exception.JobRunningException;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;

@Service
public class SimpleAnalysisJobRunner implements AnalysisJobRunner {

    ThreadPoolExecutor executor;
    Map<String, Map<String, Future<?>>> futures;

    @Value("${job.executor.threads}")
    private int threads;
    private final int DEFAULT_THREAD_COUNT = 5;
    @Autowired
    TemplateRepository templateRepository;
//    @Autowired
//    ADFMetaDataRepository adfMetaDataRepository;
    @Autowired
    AnalysisJobRepository analysisJobRepository;
    @Autowired
    AnalysisReportRepository reportRepository;
    @Autowired
    ReportService analysisService;
    @Autowired
    ADFStorage store;

    @PostConstruct
    public void clean() {
        this.futures = new HashMap<>();
        int nThreads = threads > 0 ? threads : DEFAULT_THREAD_COUNT;
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        List<AnalysisJob> jobs = this.analysisJobRepository.findByStatusIn(Arrays.asList(JobStatus.RUNNING, JobStatus.QUEUED));
        for(AnalysisJob job: jobs) {
            this.failJob(job, "Job incorrectly stopped, perhaps due to application shutdown");
        }
    }

    @PreDestroy
    public void shutdown() {
        this.executor.shutdownNow();
    }



    @Override
    public AnalysisJob addJob(String name, String templateId, String adfId, String user) throws JobRunningException, NotFoundException {
        ReportTemplate template = this.templateRepository.findMineOrReadOnly(templateId, user);
        UserUploadedFile adf = this.store.getAccessible(adfId, user);
        if(template == null) {
            throw new JobRunningException("Report Template" + templateId + " Not Found");
        }

        if(adf == null) {
            throw new JobRunningException("ADF " + adfId + " Not Found");
        }

        AnalysisJob job = new AnalysisJob(name, adfId, adf.getName(), template, user, adf.getFacilityId());
        job.setSubmitTime(new Date());
        this.analysisJobRepository.save(job);
        Future<?> future = executor.submit(new RunnableJob(job, this));
        this.register(job.getId(), job.getOwner(), future);
        return job;
    }

    @Override
    public AnalysisJob startJob(AnalysisJob job) throws Exception {
        ADFile file = this.store.getFile(job.getAdfId(), job.getOwner());
        if(job.getStatus() == JobStatus.RUNNING) {
            throw new JobRunningException("Job " + job.getName() + " Already Running ");
        }
        job.setStatus(JobStatus.RUNNING);
        job.setStartTime(new Date());
        this.analysisJobRepository.save(job);
        AnalysisReport report = this.analysisService.analyse(file, job.getTemplate());
        report.fromTemplate(job.getTemplate());
        report.setName(job.getName());
        report.setOwner(job.getOwner());
        report.setFacilityId(job.getFacilityId());
        report.setAdfName(job.getAdfName());
        report.setReportTemplate(job.getTemplate());
        this.reportRepository.save(report);
        job.setReportId(report.getId());
        job.setEndTime(new Date());
        job.setStatus(JobStatus.FINISHED);
        this.analysisJobRepository.save(job);
        this.clear(job.getId(), job.getOwner());
        return job;
    }

    public void register(String id, String user, Future<?> future) {
        if(!this.futures.containsKey(user)) {
            this.futures.put(user, new HashMap<>());
        }
        this.futures.get(user).put(id, future);
    }

    public void clear(String id, String user) {
        if(this.futures.containsKey(user)) {
            this.futures.get(user).remove(id);
            if(this.futures.get(user).size() == 0) {
                this.futures.remove(user);
            }
        }
    }

    @Override
    public AnalysisJob failJob(AnalysisJob job, String reason) {
        job.setStatus(JobStatus.FAILED);
        job.setEndTime(new Date());
        job.setFailure(reason);
        this.analysisJobRepository.save(job);
        this.clear(job.getId(), job.getOwner());
        return job;
    }

    @Override
    public List<AnalysisJob> getAllJobsForUserAndFacility(String user, String facilityId) {
        return this.analysisJobRepository.findByOwnerAndFacilityId(user, facilityId);
    }

    @Override
    public boolean deleteJobForUser(String id, String user) throws JobRunningException {
        if(this.futures.containsKey(user) && this.futures.get(user).containsKey(id)) {
            Future<?> future = this.futures.get(user).get(id);
            if(future.isDone() || future.isCancelled()) {
                AnalysisJob job = this.analysisJobRepository.findByIdAndOwner(id, user);
                if(job != null) {
                    if(!Strings.isNullOrEmpty(job.getReportId())) {
                        this.reportRepository.delete(job.getReportId());
                    }
                    this.analysisJobRepository.delete(id);
                } else {
                    this.clear(id, user);
                    throw new JobRunningException("Job does not exist");
                }
                this.clear(id, user);
                return true;
            } else {
                throw new JobRunningException("Can't delete running task");
            }
        } else {
            AnalysisJob job = this.analysisJobRepository.findByIdAndOwner(id, user);
            if(job != null) {
                if(!Strings.isNullOrEmpty(job.getReportId())) {
                    this.reportRepository.delete(job.getReportId());
                }
                this.analysisJobRepository.delete(id);
                return true;
            } else {
                throw new JobRunningException("Job does not exist");
            }
        }
    }
}
