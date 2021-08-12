package gov.nist.healthcare.iz.record.generator.field;

import java.util.HashMap;

public class CopyField extends Field {
    public CopyField() {
        super(FieldType.COPY);
        this.setParams(new HashMap<String, Boolean>() {{
            put("COPY_OF", true);
        }});
    }
}
