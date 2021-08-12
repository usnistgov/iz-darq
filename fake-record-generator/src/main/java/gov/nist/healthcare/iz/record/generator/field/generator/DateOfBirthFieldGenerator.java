package gov.nist.healthcare.iz.record.generator.field.generator;

import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.distribution.Distribution;
import gov.nist.healthcare.iz.record.generator.field.DateOfBirthField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;

public class DateOfBirthFieldGenerator extends FieldGenerator {
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private final DateOfBirthField configuration;
    private final Distribution dateOfBirth;

    public DateOfBirthFieldGenerator(DateOfBirthField configuration, int numberOfRecords, LocalDate asOf) throws Exception {
        this.configuration = configuration;
        this.dateOfBirth = this.makeDobDistribution(
                asOf,
                configuration.getAgeGroups(),
                configuration.getOverflow(),
                configuration.getDistribution(),
                numberOfRecords
        );
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        return dateOfBirth.getRandomValue();
    }

    String makeDate(LocalDate source, int min, int max) {
        int months = RandomUtils.nextInt(min, max);
        return formatter.print(source.minusMonths(months));
    }

    Distribution makeDobDistribution(LocalDate asOf, List<Integer> ageGroupsByMonths, int overflowDistribution, Map<Integer, Integer> distribution, int total) throws Exception {
        LocalDate asOfModified = asOf.withDayOfMonth(4);
        Map<String, Integer> values = new HashMap<>();
        Set<String> possibleValues = new HashSet<>();
        int ageBottom = 0;
        for(int ageTop: ageGroupsByMonths) {
            String dateValue = makeDate(asOfModified, ageBottom, ageTop);
            if(distribution.containsKey(ageTop)) {
                values.put(dateValue, distribution.get(ageTop));
            }
            possibleValues.add(dateValue);
            ageBottom = ageTop;
        }
        String overflow = makeDate(asOfModified, ageBottom, ageBottom + 10);
        if(overflowDistribution != 0) {
            possibleValues.add(overflow);
            if(overflowDistribution > 0) {
                values.put(overflow, overflowDistribution);
            }
        }
        return new Distribution(
                possibleValues,
                values,
                total
        );
    }
}
