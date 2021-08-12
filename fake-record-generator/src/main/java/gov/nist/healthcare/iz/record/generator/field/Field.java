package gov.nist.healthcare.iz.record.generator.field;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DistributedCodedField.class, name = "CODED"),
        @JsonSubTypes.Type(value = RandomField.class, name = "RANDOM"),
        @JsonSubTypes.Type(value = FixedField.class, name = "FIXED"),
        @JsonSubTypes.Type(value = VocabularyField.class, name = "VOCABULARY"),
        @JsonSubTypes.Type(value = CodeField.class, name = "CODED"),
        @JsonSubTypes.Type(value = DateOfBirthField.class, name = "DOB"),
        @JsonSubTypes.Type(value = NameField.class, name = "NAME"),
        @JsonSubTypes.Type(value = StateAwareField.class, name = "STATE_AWARE"),
        @JsonSubTypes.Type(value = EnumField.class, name = "ENUM"),
        @JsonSubTypes.Type(value = ValueDistributionField.class, name = "VALUE_DISTRIBUTION"),
        @JsonSubTypes.Type(value = ValueLengthField.class, name = "VALUE_LENGTH"),
        @JsonSubTypes.Type(value = MappedField.class, name = "MAPPED"),
        @JsonSubTypes.Type(value = DateOpField.class, name = "DATE_OP"),
        @JsonSubTypes.Type(value = DateBetweenField.class, name = "DATE_BETWEEN"),
        @JsonSubTypes.Type(value = CopyField.class, name = "COPY"),
        @JsonSubTypes.Type(value = CrossRecordField.class, name = "CROSS"),

})
public class Field {
    protected int position;
    protected boolean isTransient;
    protected FieldType type;
    protected Map<String, Boolean> params = new HashMap<>();
    protected Map<String, String> paramsMapTo = new HashMap<>();
    protected Map<String, String> paramsValue = new HashMap<>();

    public Field(FieldType type) {
        this.type = type;
        this.params = new HashMap<>();
        this.paramsMapTo = new HashMap<>();
    }

    public Field(FieldType type,  Map<String, Boolean> params) {
        this.type = type;
        this.params = params;
    }

    public Set<String> getDependencies() {
        return new HashSet<>(paramsMapTo.values());
    }

    public boolean allParamsAreMapped() {
        return params.entrySet().stream()
                .filter(Map.Entry::getValue)
                .allMatch((e) -> paramsMapTo.containsKey(e.getKey()) || paramsValue.containsKey(e.getKey()));
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public FieldType getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean aTransient) {
        isTransient = aTransient;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Map<String, Boolean> getParams() {
        return params;
    }

    public void setParams(Map<String, Boolean> params) {
        this.params = params;
    }

    public Map<String, String> getParamsMapTo() {
        return paramsMapTo;
    }

    public void setParamsMapTo(Map<String, String> paramsMapTo) {
        this.paramsMapTo = paramsMapTo;
    }

    public Map<String, String> getParamsValue() {
        return paramsValue;
    }

    public void setParamsValue(Map<String, String> paramsValue) {
        this.paramsValue = paramsValue;
    }

    public Map<String, String> getParamValues(Map<String, String> current) throws Exception {
        return getParamValues(current, null);
    }

    public Map<String, String> getParamValues(Map<String, String> current, Map<String, String> additional) throws Exception {
        Map<String, String> paramValues = new HashMap<>();
        for(String key: params.keySet()) {
            String paramField = paramsMapTo.get(key);
            String value = current.get(paramField);
            if(!Strings.isNullOrEmpty(value)) {
                paramValues.put(key, value);
            } else if(additional != null && additional.containsKey(paramField)) {
                paramValues.put(key, additional.get(paramField));
            } else if(paramsValue.containsKey(key)) {
                paramValues.put(key, paramsValue.get(key));
            } else if(params.get(key)) {
                throw new Exception("Required param " + key + " at " + paramField + "not found");
            }
        }
        return paramValues;
    }
}
