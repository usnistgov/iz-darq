package gov.nist.healthcare.iz.record.generator.distribution;

public class DistributionValue {
    private final int numberOfRecords;
    private final int target;
    private int actual;
    private final double goal;

    public DistributionValue(double goal, int numberOfRecords) {
        this.goal = goal;
        this.numberOfRecords = numberOfRecords;
        this.target = (int) (numberOfRecords * goal);
    }

    public void plusOne() throws Exception {
        if(target > actual) {
            this.actual++;
        } else {
            throw new Exception("Target of "+ target + " records already achieved");
        }
    }

    public boolean isSatisfied() {
        return target == actual;
    }

    public double getRatio() {
        return actual / (double) numberOfRecords;
    }

    public double getGoal() {
        return goal;
    }
}
