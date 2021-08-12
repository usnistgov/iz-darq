package gov.nist.healthcare.iz.record.generator.field;

import java.util.HashMap;

public class DateOpField extends Field {
    public DateOpField() {
        super(FieldType.DATE_OP);
        this.setParams(new HashMap<String, Boolean>() {{
            put("SOURCE", true);
            put("FACTOR", true);
            put("MONTHS", false);
            put("DAYS", false);
            put("YEARS", false);
        }});
    }
}
