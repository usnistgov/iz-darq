package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.model.ADFQueryVariable;
import gov.nist.healthcare.iz.darq.model.ADFQueryVariableID;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ADFQueryVariableService {

    private final Set<ADFQueryVariable> VARIABLES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    new ADFQueryVariable(
                            ADFQueryVariableID.NUMBER_OF_PATIENTS,
                            "Total number of patients",
                            "Total number of patients in the extract"
                    ),
                    new ADFQueryVariable(
                            ADFQueryVariableID.NUMBER_OF_VACCINATIONS,
                            "Total number of vaccinations",
                            "Total number of vaccinations in the extract"
                    ),
                    new ADFQueryVariable(
                            ADFQueryVariableID.NUMBER_OF_ADMINISTERED_VACCINATIONS,
                            "Total number of administered vaccinations",
                            "Total number of administered vaccinations in the extract"
                    ),
                    new ADFQueryVariable(
                            ADFQueryVariableID.NUMBER_OF_HISTORICAL_VACCINATIONS,
                            "Total number of historical vaccinations",
                            "Total number of historical vaccinations in the extract"
                    ),
                    new ADFQueryVariable(
                            ADFQueryVariableID.NUMBER_OF_PROVIDERS,
                            "Total number of providers",
                            "Total number of providers in the extract"
                    )
            ))
    );

    public Set<ADFQueryVariable> getListOfADFVariable() {
        return this.VARIABLES;
    }

    public ADFQueryVariable getADFVariableById(String id) {
        return this.getListOfADFVariable().stream().filter((var) -> var.getId().equals(id)).findFirst().orElse(null);
    }

    public ADFQueryVariableID getIDFromString(String id) throws Exception {
        try {
            return ADFQueryVariableID.valueOf(id);
        } catch (Exception e) {
            throw new Exception("ADF Variable " + id + " not found");
        }
    }

    public QueryVariableRefInstance getADFValueInstance(ADFQueryVariable variable, ADFReader file) throws Exception {
        ADFQueryVariableID adfVariableId = this.getIDFromString(variable.getId());
        switch (adfVariableId) {
            case NUMBER_OF_PATIENTS:
                return this.getInstanceOfVariable(variable, file.getMetadata().getAnalysisDate(), file.getSummary().getCounts().totalReadPatientRecords);
            case NUMBER_OF_VACCINATIONS:
                return this.getInstanceOfVariable(variable, file.getMetadata().getAnalysisDate(), file.getSummary().getCounts().totalReadVaccinations);
            case NUMBER_OF_HISTORICAL_VACCINATIONS:
                return this.getInstanceOfVariable(variable, file.getMetadata().getAnalysisDate(), file.getSummary().getCounts().historical);
            case NUMBER_OF_ADMINISTERED_VACCINATIONS:
                return this.getInstanceOfVariable(variable, file.getMetadata().getAnalysisDate(), file.getSummary().getCounts().administered);
            case NUMBER_OF_PROVIDERS:
                return this.getInstanceOfVariable(variable, file.getMetadata().getAnalysisDate(), file.getSummary().getCounts().numberOfProviders);
            default:
                throw new Exception("ADF Variable " + adfVariableId + " not recognized");
        }
    }

    private QueryVariableRefInstance getInstanceOfVariable(ADFQueryVariable variable, Date adfAnalysisDate, double value) {
        return new QueryVariableRefInstance(
                variable.getId(),
                variable.getType(),
                null,
                value,
                adfAnalysisDate,
                null,
                null,
                variable.getName(),
                variable.getDescription()
        );
    }

}
