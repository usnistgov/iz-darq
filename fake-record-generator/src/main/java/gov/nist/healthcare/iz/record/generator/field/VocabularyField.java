package gov.nist.healthcare.iz.record.generator.field;

import gov.nist.healthcare.iz.record.generator.vocabulary.VocabularyTable;

public class VocabularyField extends Field {
    private VocabularyTable table;
    private String field;

    public VocabularyField() {
        super(FieldType.VOCABULARY);
    }

    public VocabularyTable getTable() {
        return table;
    }

    public void setTable(VocabularyTable table) {
        this.table = table;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
