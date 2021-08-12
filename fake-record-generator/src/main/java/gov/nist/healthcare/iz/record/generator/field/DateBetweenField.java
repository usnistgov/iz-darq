package gov.nist.healthcare.iz.record.generator.field;

import java.util.HashMap;

public class DateBetweenField extends Field {
    public DateBetweenField() {
        super(FieldType.DATE_BETWEEN);
        this.setParams(new HashMap<String, Boolean>() {{
            put("START", true);
            put("END", true);
        }});
    }

}
