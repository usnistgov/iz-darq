package gov.nist.healthcare.iz.darq.analyzer.model.template;

public class ThresholdFilter extends Filter {
    private boolean pass;

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}
