package gov.nist.healthcare.iz.record.generator.field;

import org.immregistries.codebase.client.reference.CodesetType;

public class CodedField extends Field {
    CodesetType codeSet;
    public CodedField(FieldType type) {
        super(type);
    }

    public CodesetType getCodeSet() {
        return codeSet;
    }

    public void setCodeSet(CodesetType codeSet) {
        this.codeSet = codeSet;
    }
}
