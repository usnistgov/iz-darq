package gov.nist.healthcare.iz.record.generator.field;

import java.util.HashMap;

public class CodeField extends CodedField {
    public CodeField() {
        super(FieldType.CODED);
        this.setParams(new HashMap<String, Boolean>() {{
            put("LINK_TO", false);
        }});
    }
}
