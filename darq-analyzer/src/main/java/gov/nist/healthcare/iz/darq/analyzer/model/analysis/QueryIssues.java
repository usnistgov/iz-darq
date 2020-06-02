package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import java.util.Set;

public class QueryIssues {
    private Set<String> inactiveDetections;

    public Set<String> getInactiveDetections() {
        return inactiveDetections;
    }

    public void setInactiveDetections(Set<String> inactiveDetections) {
        this.inactiveDetections = inactiveDetections;
    }
}
