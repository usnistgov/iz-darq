package gov.nist.healthcare.iz.darq.digest.service;
import gov.nist.healthcare.iz.darq.configuration.validation.ConfigurationPayloadValidator;
import gov.nist.healthcare.iz.darq.digest.domain.Bracket;
import gov.nist.healthcare.iz.darq.digest.domain.Range;
import gov.nist.healthcare.iz.darq.digest.service.impl.AgeGroupCalculator;
import org.joda.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class AgeGroupTestCases {

    ConfigurationPayloadValidator getValidator() {
        return new ConfigurationPayloadValidator();
    }

    Range range(Bracket min, Bracket max) {
        return new Range(min, max);
    }

    Bracket bracket(int year, int month, int day) {
        return new Bracket(year, month, day);
    }

    @Test
    public void ageGroupsAreDefined() {
        ConfigurationPayloadValidator validator = this.getValidator();
        List<String> errors = validator.validateAgeGroups(null, true);
        assertEquals(1, errors.size());
        assertTrue(errors.contains("Configuration Age Groups is not defined"));
    }

    @Test
    public void ageGroupsCanBeEmpty() {
        ConfigurationPayloadValidator validator = this.getValidator();
        List<String> errors = validator.validateAgeGroups(Collections.emptyList(), true);
        assertEquals(0, errors.size());
    }



    @Test
    public void ageGroupsStartWithBirth() {
        ConfigurationPayloadValidator validator = this.getValidator();
        Range r0 = range(bracket(0,0,0), bracket(0, 1, 0));
        Range r1 = range(bracket(0,1,0), bracket(0, 2, 0));
        Range r2 = range(bracket(0,2,0), bracket(0, 3, 0));
        List<String> errorsFail = validator.validateAgeGroups(Arrays.asList(r2, r1), true);
        List<String> errorsPass = validator.validateAgeGroups(Arrays.asList(r2, r1, r0), true);

        assertEquals(1, errorsFail.size());
        assertTrue(errorsFail.contains("Configuration Age Groups : Age groups do not start from birth"));
        assertEquals(0, errorsPass.size());
    }

    @Test
    public void ageGroupsBracketsAreNotNull() {
        ConfigurationPayloadValidator validator = this.getValidator();
        Range r1 = range(bracket(0,0,0), bracket(0, 1, 0));
        Range r1_null = range(bracket(0,0,0), null);
        Range r2 = range(bracket(0,2,0), bracket(0, 3, 0));
        Range r2_null = range(null, bracket(0, 3, 0));

        List<String> errorsFailMin = validator.validateAgeGroups(Arrays.asList(r2_null, r1), true);
        List<String> errorsFailMax = validator.validateAgeGroups(Arrays.asList(r1_null, r2), true);

        assertEquals(1, errorsFailMin.size());
        assertTrue(errorsFailMin.contains("Configuration Age Groups : Min bracket is missing for age group index :" + 0));

        assertEquals(1, errorsFailMax.size());
        assertTrue(errorsFailMax.contains("Configuration Age Groups : Max bracket is missing for age group index :" + 0));
    }

    @Test
    public void ageGroupsDaysAreNullOrZero() {
        ConfigurationPayloadValidator validator = this.getValidator();
        Range r1_d_maxy = range(bracket(0,0,0), bracket(0, 1, 1));
        Range r1 = range(bracket(0,0,0), bracket(0, 1, 0));
        Range r2 = range(bracket(0,2,0), bracket(0, 3, 0));
        Range r2_d_minx = range(bracket(0,2,2), bracket(0, 3, 0));

        List<String> errorsFailMax = validator.validateAgeGroups(Arrays.asList(r1_d_maxy, r2), true);
        List<String> errorsFailMin = validator.validateAgeGroups(Arrays.asList(r1, r2_d_minx), true);

        assertTrue(errorsFailMin.contains("Configuration Age Groups : Min bracket has a non-zero value for days. Days Value: '"+ 2 +"' for age group index :" + 1));
        assertTrue(errorsFailMax.contains("Configuration Age Groups : Max bracket has a non-zero value for days. Days Value: '"+ 1 +"' for age group index :" + 0));
    }

    @Test
    public void ageGroupsMinAlwaysLowerThanMax() {
        ConfigurationPayloadValidator validator = this.getValidator();
        Range r1 = range(bracket(0,0,0), bracket(1, 0, 0));

        // R2 : MIN < MAX => PASS
        Range r2_2 = range(bracket(1,0,0), bracket(0, 13, 0));
        List<String> errors_pass_2 = validator.validateAgeGroups(Arrays.asList(r2_2, r1), true);

        // R2 : MIN > MAX => FAIL
        Range r2_3 = range(bracket(1,0,0), bracket(0, 11, 0));
        List<String> errors_fail_3 = validator.validateAgeGroups(Arrays.asList(r1, r2_3), true);

        // R2 : MIN > MAX => FAIL
        Range r2_4 = range(bracket(0,14,0), bracket(0, 13, 0));
        List<String> errors_fail_4 = validator.validateAgeGroups(Arrays.asList(r1, r2_4), true);

        // R2 : MIN > MAX => FAIL
        Range r2_5 = range(bracket(1,11,0), bracket(0, 12, 0));
        List<String> errors_fail_5 = validator.validateAgeGroups(Arrays.asList(r1, r2_5), true);

        assertEquals(0, errors_pass_2.size());
        assertTrue(errors_fail_3.contains("Configuration Age Groups : Min bracket is greater than Max bracket for range "+ r2_3.toString()));
        assertTrue(errors_fail_4.contains("Configuration Age Groups : Min bracket is greater than Max bracket for range "+ r2_4.toString()));
        assertTrue(errors_fail_5.contains("Configuration Age Groups : Min bracket is greater than Max bracket for range "+ r2_5.toString()));
    }

    @Test
    public void ageGroupsMinAlwaysNotEqualMax() {
        ConfigurationPayloadValidator validator = this.getValidator();
        Range r1 = range(bracket(0,0,0), bracket(1, 0, 0));

        // R2 : MIN = MAX => FAIL
        Range r2_1 = range(bracket(1,0,0), bracket(0, 12, 0));
        List<String> errors_fail_1 = validator.validateAgeGroups(Arrays.asList(r2_1, r1), false);

        // R2 : MIN = MAX (allow) => PASS
        Range r2_1_1 = range(bracket(1,0,0), bracket(0, 12, 0));
        List<String> errors_pass_1_1 = validator.validateAgeGroups(Arrays.asList(r2_1_1, r1), true);

        // R2 : MIN = MAX => FAIL
        Range r2_2 = range(bracket(1,0,0), bracket(1, 0, 0));
        List<String> errors_fail_2 = validator.validateAgeGroups(Arrays.asList(r1, r2_2), false);

        // R2 : MIN = MAX (allow) => PASS
        Range r2_2_1 = range(bracket(1,0,0), bracket(1, 0, 0));
        List<String> errors_pass_2_1 = validator.validateAgeGroups(Arrays.asList(r1, r2_2_1), true);

        // R2 : MIN < MAX => PASS
        Range r2_3 = range(bracket(1,0,0), bracket(0, 13, 0));
        List<String> errors_pass_3 = validator.validateAgeGroups(Arrays.asList(r2_3, r1), false);

        // R2 : MIN < MAX => PASS
        Range r2_4 = range(bracket(1,0,0), bracket(1, 1, 0));
        List<String> errors_pass_4 = validator.validateAgeGroups(Arrays.asList(r1, r2_4), false);


        assertEquals(0, errors_pass_3.size());
        assertEquals(0, errors_pass_4.size());
        assertEquals(0, errors_pass_1_1.size());
        assertEquals(0, errors_pass_2_1.size());

        assertTrue(errors_fail_1.contains("Configuration Age Groups : Min bracket equals Max bracket for range "+ r2_1.toString()));
        assertTrue(errors_fail_2.contains("Configuration Age Groups : Min bracket equals Max bracket for range "+ r2_2.toString()));
    }

    @Test
    public void ageGroupsDoNotHaveGapOrOverlap() {
        ConfigurationPayloadValidator validator = this.getValidator();
        Range r1 = range(bracket(0,0,0), bracket(1, 0, 0));

        Range r2_1 = range(bracket(1,1,0), bracket(0, 15, 0));
        List<String> errors_fail_1 = validator.validateAgeGroups(Arrays.asList(r2_1, r1), true);
        assertTrue(errors_fail_1.contains("Configuration Age Groups : Age groups have a gap / overlap between " + r1.getMax() + " and " + r2_1.getMin()));

        Range r2_2 = range(bracket(0,13,0), bracket(0, 13, 0));
        List<String> errors_fail_2 = validator.validateAgeGroups(Arrays.asList(r2_2, r1), true);
        assertTrue(errors_fail_2.contains("Configuration Age Groups : Age groups have a gap / overlap between " + r1.getMax() + " and " + r2_2.getMin()));

        Range r2_3 = range(bracket(0,12,0), bracket(1, 1, 0));
        List<String> errors_pass_3 = validator.validateAgeGroups(Arrays.asList(r2_3, r1), true);
        assertEquals(0, errors_pass_3.size());

        Range r2_4 = range(bracket(0,9,0), bracket(0, 13, 0));
        List<String> errors_fail_4 = validator.validateAgeGroups(Arrays.asList(r2_4, r1), true);
        assertTrue(errors_fail_4.contains("Configuration Age Groups : Age groups have a gap / overlap between " + r1.getMax() + " and " + r2_4.getMin()));

        Range r2_5 = range(bracket(0,0,0), bracket(0, 13, 0));
        List<String> errors_fail_5 = validator.validateAgeGroups(Arrays.asList(r1, r2_5), true);
        assertTrue(errors_fail_5.contains("Configuration Age Groups : Age groups have a gap / overlap between " + r1.getMax() + " and " + r2_5.getMin()));
    }

    @Test
    public void ageGroupsDoNotHaveDuplicates() {
        ConfigurationPayloadValidator validator = this.getValidator();
        Range r1 = range(bracket(0,0,0), bracket(1, 0, 0));
        Range r2 = range(bracket(0,12,0), bracket(1, 5, 0));

        Range r3_1 = range(bracket(0,12,0), bracket(1, 5, 0));
        List<String> errors_fail_1 = validator.validateAgeGroups(Arrays.asList(r1, r2, r3_1), true);
        assertTrue(errors_fail_1.contains("Configuration Age Groups : Age groups " + r2 + " has a duplicate"));

        Range r3_2 = range(bracket(1,0,0), bracket(1, 5, 0));
        List<String> errors_fail_2 = validator.validateAgeGroups(Arrays.asList(r1, r2, r3_2), true);
        assertTrue(errors_fail_2.contains("Configuration Age Groups : Age groups " + r2 + " has a duplicate"));

        Range r3_3 = range(bracket(1,0,0), bracket(0, 17, 0));
        List<String> errors_fail_3 = validator.validateAgeGroups(Arrays.asList(r1, r2, r3_3), true);
        assertTrue(errors_fail_3.contains("Configuration Age Groups : Age groups " + r2 + " has a duplicate"));

        Range r3_4 = range(bracket(0,12,0), bracket(0, 17, 0));
        List<String> errors_fail_4 = validator.validateAgeGroups(Arrays.asList(r1, r2, r3_4), true);
        assertTrue(errors_fail_4.contains("Configuration Age Groups : Age groups " + r2 + " has a duplicate"));
    }

    @Test
    public void isInside0to5() {
        Range r1 = range(bracket(0,0,0), bracket(0, 1, 0));
        Range r2 = range(bracket(0,1,0), bracket(0, 2, 0));
        Range r3 = range(bracket(0,2,0), bracket(0, 3, 0));
        Range r4 = range(bracket(0,3,0), bracket(0, 4, 0));
        Range r5 = range(bracket(0,4,0), bracket(0, 5, 0));

        ConfigurationPayloadValidator validator = this.getValidator();
        List<Range> ranges = Arrays.asList(r3,r2,r4,r5,r1);
        assertEquals(validator.validateAgeGroups(ranges, true).size(), 0);

        AgeGroupCalculator calculator = new AgeGroupCalculator(ranges);
        LocalDate to = LocalDate.parse("2022-01-01");

        assertEquals(calculator.getGroup(LocalDate.parse("2021-12-01"), to),"1g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-12-15"), to),"0g");

        assertEquals(calculator.getGroup(LocalDate.parse("2021-11-01"), to),"2g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-11-23"), to),"1g");

        assertEquals(calculator.getGroup(LocalDate.parse("2021-10-01"), to),"3g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-10-02"), to),"2g");

        assertEquals(calculator.getGroup(LocalDate.parse("2021-09-01"), to),"4g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-09-06"), to),"3g");

        assertEquals(calculator.getGroup(LocalDate.parse("2021-08-01"), to),"5g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-08-30"), to),"4g");

        assertEquals(calculator.getGroup(LocalDate.parse("2021-07-01"), to),"5g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-07-24"), to),"5g");
    }

    @Test
    public void isInside0to2years() {
        Range r1 = range(bracket(0,0,0), bracket(0, 5, 0));
        Range r2 = range(bracket(0,5,0), bracket(0, 10, 0));
        Range r3 = range(bracket(0,10,0), bracket(0, 12, 0));
        Range r4 = range(bracket(0,12,0), bracket(0, 13, 0));
        Range r5 = range(bracket(0,13,0), bracket(0, 14, 0));
        Range r6 = range(bracket(1,2,0), bracket(0, 19, 0));
        Range r7 = range(bracket(0,19,0), bracket(0, 24, 0));
        Range r8 = range(bracket(0,24,0), bracket(2, 1, 0));

        ConfigurationPayloadValidator validator = this.getValidator();
        List<Range> ranges = Arrays.asList(r3,r2,r4,r5,r1, r6, r7, r8);
        assertEquals(validator.validateAgeGroups(ranges, true).size(), 0);

        AgeGroupCalculator calculator = new AgeGroupCalculator(ranges);
        LocalDate to = LocalDate.parse("2022-01-01");

        //r1 0,0,0 -> 0,5,0
        assertEquals(calculator.getGroup(LocalDate.parse("2022-01-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-10-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-08-02"), to),"0g");

        //r2 0,5,0 -> 0,10,0
        assertEquals(calculator.getGroup(LocalDate.parse("2021-08-01"), to),"1g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-05-15"), to),"1g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-03-02"), to),"1g");

        //r3 0,10,0 -> 0,12,0
        assertEquals(calculator.getGroup(LocalDate.parse("2021-03-01"), to),"2g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-02-01"), to),"2g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-01-02"), to),"2g");

        //r4 0,12,0 -> 0,13,0
        assertEquals(calculator.getGroup(LocalDate.parse("2021-01-01"), to),"3g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-12-15"), to),"3g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-12-02"), to),"3g");

        //r5 0,13,0 -> 0,14,0
        assertEquals(calculator.getGroup(LocalDate.parse("2020-12-01"), to),"4g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-11-15"), to),"4g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-11-02"), to),"4g");

        //r6 1,2,0 -> 0,19,0
        assertEquals(calculator.getGroup(LocalDate.parse("2020-11-01"), to),"5g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-08-15"), to),"5g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-06-02"), to),"5g");

        //r7 0,19,0 -> 0,24,0
        assertEquals(calculator.getGroup(LocalDate.parse("2020-06-01"), to),"6g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-03-15"), to),"6g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-01-02"), to),"6g");

        //r8 0,24,0 -> 2,0,0
        assertEquals(calculator.getGroup(LocalDate.parse("2020-01-01"), to),"7g");
        assertEquals(calculator.getGroup(LocalDate.parse("2019-12-15"), to),"7g");
        assertEquals(calculator.getGroup(LocalDate.parse("2019-12-02"), to),"7g");

        //+
        assertEquals(calculator.getGroup(LocalDate.parse("2019-12-01"), to),"8g");
        assertEquals(calculator.getGroup(LocalDate.parse("2019-05-15"), to),"8g");
        assertEquals(calculator.getGroup(LocalDate.parse("2017-01-02"), to),"8g");
    }

    @Test
    public void whenEmptyAllAre0g() {
        ConfigurationPayloadValidator validator = this.getValidator();
        List<Range> ranges = Collections.emptyList();
        assertEquals(validator.validateAgeGroups(ranges, true).size(), 0);

        AgeGroupCalculator calculator = new AgeGroupCalculator(ranges);
        LocalDate to = LocalDate.parse("2022-01-01");

        //r1 0,0,0 -> 0,5,0
        assertEquals(calculator.getGroup(LocalDate.parse("2022-01-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-10-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-08-02"), to),"0g");

        //r2 0,5,0 -> 0,10,0
        assertEquals(calculator.getGroup(LocalDate.parse("2021-08-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-05-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-03-02"), to),"0g");

        //r3 0,10,0 -> 0,12,0
        assertEquals(calculator.getGroup(LocalDate.parse("2021-03-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-02-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2021-01-02"), to),"0g");

        //r4 0,12,0 -> 0,13,0
        assertEquals(calculator.getGroup(LocalDate.parse("2021-01-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-12-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-12-02"), to),"0g");

        //r5 0,13,0 -> 0,14,0
        assertEquals(calculator.getGroup(LocalDate.parse("2020-12-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-11-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-11-02"), to),"0g");

        //r6 1,2,0 -> 0,19,0
        assertEquals(calculator.getGroup(LocalDate.parse("2020-11-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-08-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-06-02"), to),"0g");

        //r7 0,19,0 -> 0,24,0
        assertEquals(calculator.getGroup(LocalDate.parse("2020-06-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-03-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2020-01-02"), to),"0g");

        //r8 0,24,0 -> 2,0,0
        assertEquals(calculator.getGroup(LocalDate.parse("2020-01-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2019-12-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2019-12-02"), to),"0g");

        //+
        assertEquals(calculator.getGroup(LocalDate.parse("2019-12-01"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2019-05-15"), to),"0g");
        assertEquals(calculator.getGroup(LocalDate.parse("2017-01-02"), to),"0g");
    }

    @Test
    public void equalityHashSetExactValue() {
        Range r1 = range(bracket(0,0,0), bracket(0, 1, 0));
        Range r2 = range(bracket(0,1,0), bracket(0, 2, 0));
        Range r3 = range(bracket(0,2,0), bracket(0, 3, 0));
        Range r4 = range(bracket(0,3,0), bracket(0, 4, 0));
        Range r5 = range(bracket(0,4,0), bracket(0, 5, 0));

        List<Range> a = Arrays.asList(r1, r3, r5, r4, r2);
        List<Range> b = Arrays.asList(r2, r5, r3, r4, r1);

        List<Range> a_1 = Arrays.asList(r1, r3, r5, r4);
        List<Range> b_1 = Arrays.asList(r2, r5, r4, r1);

        assertEquals(new HashSet(a), new HashSet(b));
        assertNotEquals(new HashSet(a_1), new HashSet(b_1));
    }

    @Test
    public void equalityHashSetEquivalentValue() {
        Range r1 = range(bracket(0,0,0), bracket(1, 1, 0));
        Range r2 = range(bracket(1,1,0), bracket(1, 2, 0));
        Range r2_1 = range(bracket(0,13,0), bracket(1, 2, 0));
        Range r3 = range(bracket(1,2,0), bracket(1, 3, 0));
        Range r4 = range(bracket(1,3,0), bracket(1, 4, 0));
        Range r4_1 = range(bracket(0,15,0), bracket(0, 16, 0));
        Range r5 = range(bracket(1,4,0), bracket(1, 5, 0));

        List<Range> a = Arrays.asList(r1, r3, r5, r4, r2);
        List<Range> b = Arrays.asList(r2_1, r5, r3, r4_1, r1);

        List<Range> a_1 = Arrays.asList(r1, r3, r5, r4_1);
        List<Range> b_1 = Arrays.asList(r2_1, r5, r4, r1);

        assertEquals(new HashSet(a), new HashSet(b));
        assertEquals(r2_1, r2);
        assertEquals(r4_1, r4);
        assertNotEquals(new HashSet(a_1), new HashSet(b_1));
    }

    @Test
    public void test() {
        Range r1 = range(bracket(0,0,0), bracket(0, 1, 0));
        Range r2 = range(bracket(0,1,0), bracket(0, 2, 0));
        Range r3 = range(bracket(0,2,0), bracket(0, 3, 0));
        Range r4 = range(bracket(0,3,0), bracket(0, 4, 0));
        Range r5 = range(bracket(0,4,0), bracket(0, 5, 0));
        Range r6 = range(bracket(0,5,0), bracket(0, 6, 0));
        Range r7 = range(bracket(0,6,0), bracket(0, 7, 0));
        Range r8 = range(bracket(0,7,0), bracket(0, 8, 0));
        Range r9 = range(bracket(0,8,0), bracket(0, 9, 0));
        Range r10 = range(bracket(0,9,0), bracket(0, 10, 0));
        Range r11 = range(bracket(0,10,0), bracket(0, 11, 0));
        Range r12 = range(bracket(0,11,0), bracket(0, 12, 0));
        Range r13 = range(bracket(0,12,0), bracket(0, 13, 0));
        Range r14 = range(bracket(0,13,0), bracket(0, 14, 0));
        Range r15 = range(bracket(0,14,0), bracket(0, 15, 0));
        Range r16 = range(bracket(0,15,0), bracket(0, 16, 0));
        Range r17 = range(bracket(0,16,0), bracket(0, 17, 0));
        Range r18 = range(bracket(0,17,0), bracket(0, 18, 0));
        Range r19 = range(bracket(0,18,0), bracket(0, 19, 0));
        Range r20 = range(bracket(0,19,0), bracket(0, 20, 0));
        Range r21 = range(bracket(0,20,0), bracket(0, 21, 0));
        Range r22 = range(bracket(0,21,0), bracket(0, 22, 0));
        Range r23 = range(bracket(0,22,0), bracket(0, 23, 0));
        Range r24 = range(bracket(0,23,0), bracket(2, 0, 0));

        ConfigurationPayloadValidator validator = this.getValidator();
        List<Range> ranges = Arrays.asList(r3,r2,r4,r5,r1, r6, r7, r8, r24, r20, r10, r9, r11, r12, r13, r17, r16, r14, r15, r18, r22, r23, r19, r21);
        assertEquals(validator.validateAgeGroups(ranges, true).size(), 0);

        AgeGroupCalculator calculator = new AgeGroupCalculator(ranges);
        LocalDate to = LocalDate.parse("2020-12-31");

        assertEquals(calculator.getGroup(LocalDate.parse("2020-07-31"), to),"5g");
        assertEquals(calculator.getGroup(LocalDate.parse("2019-07-31"), to),"17g");
        assertEquals(calculator.getGroup(LocalDate.parse("2019-01-01"), to),"23g");
        assertEquals(calculator.getGroup(LocalDate.parse("2012-01-05"), to),"24g");
    }


}
