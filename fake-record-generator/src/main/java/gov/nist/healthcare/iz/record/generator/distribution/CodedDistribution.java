package gov.nist.healthcare.iz.record.generator.distribution;

import org.immregistries.codebase.client.CodeMap;
import org.immregistries.codebase.client.CodeMapBuilder;
import org.immregistries.codebase.client.generated.Code;
import org.immregistries.codebase.client.reference.CodesetType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CodedDistribution extends Distribution {
    private static final CodeMap CODE_MAP = CodeMapBuilder.INSTANCE.getDefaultCodeMap();
    public CodedDistribution(CodesetType CODE_SET, Set<String> withLinks, Map<String, Integer> distribution, int numberOfRecords) throws Exception {
        super(
                CODE_MAP.getCodesForTable(CODE_SET)
                        .stream()
                        .filter((code) -> withLinks == null || code.getReference() != null && code
                                .getReference()
                                .getLinkTo()
                                .stream()
                                .allMatch((link) -> withLinks.contains(link.getCodeset())))
                        .map(Code::getValue)
                        .collect(Collectors.toSet()),
                distribution,
                numberOfRecords
        );
    }
}
