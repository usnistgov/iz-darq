package gov.nist.healthcare.iz.darq.parser.service.impl;

import gov.nist.healthcare.iz.darq.parser.service.FieldTransformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CVXFieldTransformation implements FieldTransformation {

    private static final CVXFieldTransformation instance = new CVXFieldTransformation();
    Set<String> target = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"))
    );

    private CVXFieldTransformation() {}

    public static CVXFieldTransformation getInstance() {
        return instance;
    }

    @Override
    public String transform(String value) {
        if(target.contains(value)) return "0" + value;
        return value;
    }

}
