package gov.nist.healthcare.iz.record.generator.field;

import java.util.HashMap;

public class StateAwareField extends Field {
    private StateAwareFieldName field;
    public StateAwareField() {
        super(FieldType.STATE_AWARE, new HashMap<String, Boolean>() {{
            put("STATE", false);
        }});
    }

    public StateAwareFieldName getField() {
        return field;
    }

    public void setField(StateAwareFieldName field) {
        this.field = field;
    }
}
