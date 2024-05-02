package gov.nist.healthcare.iz.darq.parser.service.impl;

import gov.nist.healthcare.iz.darq.parser.service.FieldTransformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CVXFieldTransformation implements FieldTransformation {

    private static final CVXFieldTransformation instance = new CVXFieldTransformation();
    Set<String> targetForPadding = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"))
    );
    Set<String> targetForTrimming = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    "001", "002", "003", "004", "005", "006", "007", "008", "009", "010",
                    "011", "012", "013", "014", "015", "016", "017", "018", "019", "020",
                    "021", "022", "023", "024", "025", "026", "027", "028", "029", "030",
                    "031", "032", "033", "034", "035", "036", "037", "038", "039", "040",
                    "041", "042", "043", "044", "045", "046", "047", "048", "049", "050",
                    "051", "052", "053", "054", "055", "056", "057", "058", "059", "060",
                    "061", "062", "063", "064", "065", "066", "067", "068", "069", "070",
                    "071", "072", "073", "074", "075", "076", "077", "078", "079", "080",
                    "081", "082", "083", "084", "085", "086", "087", "088", "089", "090",
                    "091", "092", "093", "094", "095", "096", "097", "098", "099"
            ))
    );

    private CVXFieldTransformation() {}

    public static CVXFieldTransformation getInstance() {
        return instance;
    }

    @Override
    public String transform(String value) {
        if(targetForPadding.contains(value)) return "0" + value;
        else if(targetForTrimming.contains(value)) return value.substring(1);
        return value;
    }

}
