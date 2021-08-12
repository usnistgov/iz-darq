package gov.nist.healthcare.iz.record.generator.field;
import java.util.HashMap;

public class NameField extends Field {
    private boolean family;
    public NameField() {
        super(FieldType.NAME, new HashMap<String, Boolean>() {{
            put("GENDER", false);
        }});
    }

    public boolean isFamily() {
        return family;
    }

    public void setFamily(boolean family) {
        this.family = family;
    }
}
