package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.DateOpField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Map;

public class DateOpFieldGenerator extends FieldGenerator {
    DateOpField configuration;
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    public DateOpFieldGenerator(DateOpField configuration) {
        this.configuration = configuration;
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        Map<String, String> params = configuration.getParamValues(current);
        LocalDate date = this.formatter.parseLocalDate(params.get("SOURCE"));
        int factor = Integer.parseInt(params.get("FACTOR"));
        return formatter.print(date.withFieldAdded(
                DurationFieldType.months(),
                getParam(params, "MONTHS", factor)
        ).withFieldAdded(
                DurationFieldType.days(),
                getParam(params, "DAYS", factor)
        ).withFieldAdded(
                DurationFieldType.years(),
                getParam(params, "YEARS", factor)
        ));
    }

    private int getParam(Map<String, String> params, String value, int factor) {
        if(params.containsKey(value)) {
            return Integer.parseInt(params.get(value)) * factor;
        } else {
            return 0;
        }
    }
}
