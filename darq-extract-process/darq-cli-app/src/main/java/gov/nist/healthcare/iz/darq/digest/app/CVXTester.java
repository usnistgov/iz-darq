package gov.nist.healthcare.iz.darq.digest.app;

import org.apache.commons.io.IOUtils;
import org.immregistries.codebase.client.CodeMapBuilder;
import org.immregistries.codebase.client.generated.Code;
import org.immregistries.codebase.client.reference.CodesetType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CVXTester {

    public static void main(String[] args) throws IOException {
        List<String> codes = IOUtils.readLines(CVXTester.class.getResourceAsStream("/cvx.csv"), StandardCharsets.UTF_8);
        List<String> cdc = IOUtils.readLines(CVXTester.class.getResourceAsStream("/draft.csv"), StandardCharsets.UTF_8);

        Collection<Code> cvxCodes = CodeMapBuilder.INSTANCE.getDefaultCodeMap().getCodesForTable(CodesetType.VACCINATION_CVX_CODE);
        List<String> recognized = new ArrayList<>();
        List<String> unrecognized = new ArrayList<>();
        List<String> cdcUnrecognized = new ArrayList<>();
        List<String> cdcRecognized = new ArrayList<>();

        for(String code : codes) {
            boolean matches = cvxCodes.stream().anyMatch(cvx -> cvx.getValue().equals(code));
            boolean cdcMatch = cdc.contains(code);

            if(!matches) {
                unrecognized.add(code);
                if(!cdcMatch) {
                    cdcUnrecognized.add(code);
                } else {
                    cdcRecognized.add(code);
                }
            } else {
                recognized.add(code);
            }
        }

        System.out.println("Total Codes : " + codes.size());
        System.out.println("Recognized : " + recognized.size());
        System.out.println("Unrecognized : " + unrecognized.size());
        System.out.println("CDC Unrecognized : " + cdcUnrecognized.size());
        System.out.println("False Unrecognized : " + cdcRecognized.size());

        System.out.println(unrecognized);
        System.out.println(cdcUnrecognized);
        System.out.println(cdcRecognized);

    }
}
