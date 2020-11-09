package gov.nist.healthcare.iz.darq.parser.type;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DqDateFormat {

    private final String pattern;
    private final DateTimeFormatter formatter;

    public DqDateFormat(String pattern) {
        this.pattern = pattern;
        this.formatter = DateTimeFormat.forPattern(pattern);
    }

    public static DqDateFormat forPattern(String pattern) {
        return new DqDateFormat(pattern);
    }

    public LocalDate getDate(String date) throws Exception {
       return this.formatter.parseLocalDate(date);
    }

    public String getString(LocalDate date) {
        return this.formatter.print(date);
    }

    public String getPattern() {
        return pattern;
    }

}
