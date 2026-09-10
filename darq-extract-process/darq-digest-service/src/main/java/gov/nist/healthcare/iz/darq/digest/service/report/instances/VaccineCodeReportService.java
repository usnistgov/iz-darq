package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.localreport.AggregateLocalReportService;
import gov.nist.healthcare.iz.darq.localreport.AggregateRow;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Reports the MVX that had to be set to Uppercase during preprocess
 */
public class VaccineCodeReportService extends AggregateLocalReportService {

    public final static String FILENAME = "vaccine_code.csv";


    public VaccineCodeReportService() {
        super(FILENAME);
    }

    @Override
    public List<AggregateRow> getRows(PreProcessRecord context, RecordDetectionEngineResult detections) {
        List<AggregateRow> rows = new ArrayList<>();
        context.getVaccineCodes().forEach(
                (cvx, count) -> {
                    rows.add(
                            new AggregateRow(
                                    Collections.singletonList(
                                            cvx
                                    ),
                                    Collections.singletonList(
                                            String.valueOf(count)
                                    )
                            )
                    );
                }
        );
        return rows;

    }

    @Override
    public List<String> getHeader() {
        return Arrays.asList(
                "CVX",
                "Count"
        );
    }
}
