package gov.nist.healthcare.iz.darq.model;

import gov.nist.healthcare.iz.darq.digest.domain.Fraction;

public class ActiveJob {
    AnalysisJob job;
    Fraction progress;

    public ActiveJob() {
    }

    public ActiveJob(AnalysisJob job, Fraction progress) {
        this.job = job;
        this.progress = progress;
    }

    public AnalysisJob getJob() {
        return job;
    }

    public void setJob(AnalysisJob job) {
        this.job = job;
    }

    public Fraction getProgress() {
        return progress;
    }

    public void setProgress(Fraction progress) {
        this.progress = progress;
    }
}
