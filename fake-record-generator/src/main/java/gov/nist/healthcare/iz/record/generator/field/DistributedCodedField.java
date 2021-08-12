package gov.nist.healthcare.iz.record.generator.field;

import org.immregistries.codebase.client.reference.CodesetType;

import java.util.Map;
import java.util.Set;

public class DistributedCodedField extends CodedField {
    private Map<String, Integer> distribution;
    private Set<CodesetType> withLinks;

    public DistributedCodedField() {
        super(FieldType.CODED);
    }

    public Map<String, Integer> getDistribution() {
        return distribution;
    }

    public void setDistribution(Map<String, Integer> distribution) {
        this.distribution = distribution;
    }

    public Set<CodesetType> getWithLinks() {
        return withLinks;
    }

    public void setWithLinks(Set<CodesetType> withLinks) {
        this.withLinks = withLinks;
    }
}
