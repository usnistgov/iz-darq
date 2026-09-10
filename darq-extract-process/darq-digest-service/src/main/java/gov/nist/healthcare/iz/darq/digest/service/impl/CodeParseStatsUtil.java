package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.parser.exception.InvalidValueException;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.type.DqString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CodeParseStatsUtil {


    public void processVaccinationManufacturer(VaccineRecord record, Map<String,Integer> mvxCountMap) {
        String mvx = record.manufacturer.getValue();
        if (!StringUtils.isAllUpperCase(mvx) && StringUtils.isNotBlank(mvx)) {
            try {
                record.manufacturer = new DqString(mvx.toUpperCase(), mvx.toUpperCase());
            } catch (InvalidValueException ignored) {
                // Will not throw as null value checked
            }
            mvxCountMap.put(mvx, mvxCountMap.getOrDefault(mvx, 0) + 1);
        }
    }

    public void processVaccineCodes(VaccineRecord record, Map<String,Integer> cvxCountMap) {
        String cvx = record.vaccine_type_cvx.getValue();
        if (StringUtils.isNotBlank(cvx)) {
            cvxCountMap.put(cvx, cvxCountMap.getOrDefault(cvx, 0) + 1);
        }
    }
}
