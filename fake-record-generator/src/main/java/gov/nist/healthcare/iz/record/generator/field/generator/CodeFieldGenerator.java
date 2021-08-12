package gov.nist.healthcare.iz.record.generator.field.generator;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.record.generator.RecordType;
import gov.nist.healthcare.iz.record.generator.field.CodedField;
import gov.nist.healthcare.iz.record.generator.field.CodeField;
import gov.nist.healthcare.iz.record.generator.model.PatientRecord;
import org.apache.commons.lang3.RandomUtils;
import org.immregistries.codebase.client.CodeMap;
import org.immregistries.codebase.client.CodeMapBuilder;
import org.immregistries.codebase.client.generated.Code;

import java.util.Collection;
import java.util.Map;

public class CodeFieldGenerator extends FieldGenerator {
    private final CodeField configuration;
    private final CodedField relatedTo;
    private final Collection<Code> codes;
    private static final CodeMap CODE_MAP = CodeMapBuilder.INSTANCE.getDefaultCodeMap();

    public CodeFieldGenerator(CodeField configuration, CodedField relateTo) {
        this.configuration = configuration;
        this.relatedTo = relateTo;
        this.codes = CODE_MAP.getCodesForTable(configuration.getCodeSet());
    }

    @Override
    public String generate(PatientRecord record, RecordType type, String key, Map<String, String> current) throws Exception {
        Map<String, String> params = configuration.getParamValues(current);
        if(!params.containsKey("LINK_TO")) {
            return getRandomValue();
        }
        Code code = CODE_MAP.getCodeForCodeset(this.relatedTo.getCodeSet(), params.get("LINK_TO"));
        if(code == null) {
            throw new Exception(relatedTo + " is not in Code Set "+ this.relatedTo.getCodeSet());
        }
        String value = CODE_MAP.getRelatedCodeValue(code, configuration.getCodeSet());
        if(Strings.isNullOrEmpty(value)) {
            throw new Exception("No related code to "+ relatedTo+" in Code Set" + configuration.getCodeSet());
        }
        return value;
    }

    public String getRandomValue() {
        int at = RandomUtils.nextInt(0, codes.size());
        return codes.stream().skip(at).findFirst().map(Code::getValue).orElse(null);
    }

}
