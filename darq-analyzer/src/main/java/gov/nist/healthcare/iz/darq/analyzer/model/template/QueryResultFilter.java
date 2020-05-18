package gov.nist.healthcare.iz.darq.analyzer.model.template;

public class QueryResultFilter {
    private CompareFilter denominator;
    private CompareFilter percentage;
    private ThresholdFilter threshold;
    private GroupFilter groups;

    public CompareFilter getDenominator() {
        return denominator;
    }

    public void setDenominator(CompareFilter denominator) {
        this.denominator = denominator;
    }

    public CompareFilter getPercentage() {
        return percentage;
    }

    public void setPercentage(CompareFilter percentage) {
        this.percentage = percentage;
    }

    public ThresholdFilter getThreshold() {
        return threshold;
    }

    public void setThreshold(ThresholdFilter threshold) {
        this.threshold = threshold;
    }

    public GroupFilter getGroups() {
        return groups;
    }

    public void setGroups(GroupFilter groups) {
        this.groups = groups;
    }
}