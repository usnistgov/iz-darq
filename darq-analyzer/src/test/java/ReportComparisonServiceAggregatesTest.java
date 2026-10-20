import gov.nist.healthcare.iz.darq.analyzer.model.comparison.RowDifferential;
import gov.nist.healthcare.iz.darq.analyzer.service.comparison.ReportComparisonService;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReportComparisonServiceAggregatesTest {

    private static final double DELTA = 0.0001;

    private RowDifferential compute(Double basePercent, boolean basePass, Double newPercent, boolean newPass) throws Exception {
        RowDifferential diff = new RowDifferential();
        diff.setBasePercent(basePercent);
        diff.setBasePass(basePass);
        diff.setNewPercent(newPercent);
        diff.setNewPass(newPass);

        Method m = ReportComparisonService.class.getDeclaredMethod("computeAggregates", RowDifferential.class);
        m.setAccessible(true);
        m.invoke(new ReportComparisonService(), diff);
        return diff;
    }

    @Test
    public void testBothValuesPresent() throws Exception {
        RowDifferential diff = compute(30.0, false, 50.0, true);
        assertEquals(30.0, diff.getMinPercent(), DELTA);
        assertEquals(40.0, diff.getMeanPercent(), DELTA);
        assertEquals(50.0, diff.getMaxPercent(), DELTA);
        assertEquals(50.0, diff.getPercentOfReportsPassing(), DELTA);
    }

    @Test
    public void testBothPassing() throws Exception {
        RowDifferential diff = compute(100.0, true, 100.0, true);
        assertEquals(100.0, diff.getMinPercent(), DELTA);
        assertEquals(100.0, diff.getMeanPercent(), DELTA);
        assertEquals(100.0, diff.getMaxPercent(), DELTA);
        assertEquals(100.0, diff.getPercentOfReportsPassing(), DELTA);
    }

    @Test
    public void testNeitherPassing() throws Exception {
        RowDifferential diff = compute(10.0, false, 20.0, false);
        assertEquals(0.0, diff.getPercentOfReportsPassing(), DELTA);
    }

    @Test
    public void testOnlyBasePresentIgnoresMissingSide() throws Exception {
        RowDifferential diff = compute(80.0, true, null, false);
        assertEquals(80.0, diff.getMinPercent(), DELTA);
        assertEquals(80.0, diff.getMeanPercent(), DELTA);
        assertEquals(80.0, diff.getMaxPercent(), DELTA);
        // Only one observation contributed, and it passed
        assertEquals(100.0, diff.getPercentOfReportsPassing(), DELTA);
    }

    @Test
    public void testNoValuesLeavesAggregatesNull() throws Exception {
        RowDifferential diff = compute(null, false, null, false);
        assertNull(diff.getMinPercent());
        assertNull(diff.getMeanPercent());
        assertNull(diff.getMaxPercent());
        assertNull(diff.getPercentOfReportsPassing());
    }
}
