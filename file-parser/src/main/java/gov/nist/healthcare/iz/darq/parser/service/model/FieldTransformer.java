package gov.nist.healthcare.iz.darq.parser.service.model;

import gov.nist.healthcare.iz.darq.parser.service.FieldTransformation;
import gov.nist.healthcare.iz.darq.parser.service.impl.CVXFieldTransformation;

public enum FieldTransformer {
    NONE(null),
    CVX_LEADING_ZERO(CVXFieldTransformation.getInstance());

    private final FieldTransformation transformation;

    FieldTransformer(FieldTransformation transformation) {
        this.transformation = transformation;
    }

    public String transform(String value) {
        return transformation != null ? transformation.transform(value) : value;
    }
}
