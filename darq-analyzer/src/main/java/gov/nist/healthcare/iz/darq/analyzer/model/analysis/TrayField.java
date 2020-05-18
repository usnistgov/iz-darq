package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.Objects;

public class TrayField {
    Field field;
    String v;

    public TrayField(Field field, String v) {
        super();
        this.field = field;
        this.v = v;
    }
    public Field getField() {
        return field;
    }
    public void setField(Field field) {
        this.field = field;
    }
    public String getV() {
        return v;
    }
    public void setV(String v) {
        this.v = v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrayField trayField = (TrayField) o;
        return field == trayField.field;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
