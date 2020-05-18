package gov.nist.healthcare.iz.darq.analyzer.model.template;

public class GlobalThreshold extends Filter {
    Threshold goal;

    public Threshold getGoal() {
        return goal;
    }

    public void setGoal(Threshold goal) {
        this.goal = goal;
    }
}
