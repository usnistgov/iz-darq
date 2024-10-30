package gov.nist.healthcare.iz.darq.service.job;

import gov.nist.healthcare.iz.darq.model.Job;

import java.util.function.Function;

public class RunnableJob implements Runnable {

    Job job;
    JobManagementService jobManagementService;
    Function<Exception, Void> finalize;

    public RunnableJob(Job job, JobManagementService jobManagementService, Function<Exception, Void> finalize) {
        this.job = job;
        this.jobManagementService = jobManagementService;
        this.finalize = finalize;
    }

    public Job getJob() {
        return job;
    }

    @Override
    public void run() {
        try {
            this.jobManagementService.start(this.job);
            finalize.apply(null);
        } catch (Exception e) {
            this.jobManagementService.fail(job, e.getMessage());
            finalize.apply(e);
        }
    }
}
