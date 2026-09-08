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
public class FixedMvxReportService extends AggregateLocalReportService {

    public final static String FILENAME = "lowercase_mvx.csv";


    public FixedMvxReportService() {
        super(FILENAME);
    }

    @Override
    public List<AggregateRow> getRows(PreProcessRecord context, RecordDetectionEngineResult detections) {
        List<AggregateRow> rows = new ArrayList<>();
        context.getLowercaseMvxCodes().forEach(
                (mvx, count) -> {
                    rows.add(
                            new AggregateRow(
                                    Collections.singletonList(
                                            mvx
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
                "MVX",
                "Count"
        );
    }
}
