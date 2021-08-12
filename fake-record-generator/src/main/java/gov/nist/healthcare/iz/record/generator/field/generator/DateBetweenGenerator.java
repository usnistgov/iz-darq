package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.DateBetweenField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Map;

public class DateBetweenGenerator extends FieldGenerator {
    DateBetweenField configuration;
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    public DateBetweenGenerator(DateBetweenField configuration) {
        this.configuration = configuration;
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        Map<String, String> params = configuration.getParamValues(current, record.getPatient());
        LocalDate start = this.formatter.parseLocalDate(params.get("START"));
        LocalDate end = this.formatter.parseLocalDate(params.get("END"));
        if(end.isBefore(start)) {
            throw new Exception("End is before Start date");
        }
        long diff = (end.toDateTimeAtStartOfDay().getMillis() - start.toDateTimeAtStartOfDay().getMillis());
        long millis = RandomUtils.nextLong(0, diff);
        return formatter.print(
                new LocalDate(
                        new Date(millis + start.toDateTimeAtStartOfDay().getMillis())
                )
        );
    }

}
