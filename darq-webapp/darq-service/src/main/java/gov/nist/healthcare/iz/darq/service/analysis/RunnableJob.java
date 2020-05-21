package gov.nist.healthcare.iz.darq.service.analysis;
import gov.nist.healthcare.iz.darq.model.AnalysisJob;
import gov.nist.healthcare.iz.darq.service.impl.SimpleAnalysisJobRunner;

import java.util.concurrent.CancellationException;

public class RunnableJob implements Runnable {

    AnalysisJob job;
    SimpleAnalysisJobRunner jobRunner;

    public RunnableJob(AnalysisJob job, SimpleAnalysisJobRunner jobRunner) {
        this.job = job;
        this.jobRunner = jobRunner;
    }

    public AnalysisJob getJob() {
        return job;
    }

    @Override
    public void run() {
        try {
            this.jobRunner.startJob(this.job);
        } catch (Exception e) {
            this.jobRunner.failJob(job, e.getMessage());
        }
    }
}
