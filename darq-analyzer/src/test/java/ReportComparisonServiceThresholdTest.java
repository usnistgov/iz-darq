import gov.nist.healthcare.iz.darq.analyzer.model.template.Comparator;
import gov.nist.healthcare.iz.darq.analyzer.model.template.Threshold;
import gov.nist.healthcare.iz.darq.analyzer.service.comparison.ReportComparisonService;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class ReportComparisonServiceThresholdTest {

    private String format(Threshold threshold) throws Exception {
        Method m = ReportComparisonService.class.getDeclaredMethod("formatThreshold", Threshold.class);
        m.setAccessible(true);
        return (String) m.invoke(new ReportComparisonService(), threshold);
    }

    private Threshold threshold(Comparator comparator, double value) {
        Threshold t = new Threshold();
        t.setComparator(comparator);
        t.setValue(value);
        return t;
    }

    @Test
    public void testGreaterThanWholeNumberHasNoDecimals() throws Exception {
        assertEquals(">99%", format(threshold(Comparator.GT, 99.0)));
    }

    @Test
    public void testLessThanKeepsDecimals() throws Exception {
        assertEquals("<5.5%", format(threshold(Comparator.LT, 5.5)));
    }

    @Test
    public void testNullThresholdIsBlank() throws Exception {
        assertEquals("", format(null));
    }

    @Test
    public void testNullComparatorIsBlank() throws Exception {
        assertEquals("", format(threshold(null, 99.0)));
    }
}
