package gov.nist.healthcare.iz.darq.analyzer.model.template;

public class QueryThreshold {
    GlobalThreshold global;
    CustomThreshold custom;

    public GlobalThreshold getGlobal() {
        return global;
    }

    public void setGlobal(GlobalThreshold global) {
        this.global = global;
    }

    public CustomThreshold getCustom() {
        return custom;
    }

    public void setCustom(CustomThreshold custom) {
        this.custom = custom;
    }
}