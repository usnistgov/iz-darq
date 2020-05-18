package gov.nist.healthcare.iz.darq.analyzer.model.template;

import java.util.List;

public class CustomThreshold extends Filter {
    List<ComplexThreshold> thresholds;

    public List<ComplexThreshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<ComplexThreshold> thresholds) {
        this.thresholds = thresholds;
    }
}
