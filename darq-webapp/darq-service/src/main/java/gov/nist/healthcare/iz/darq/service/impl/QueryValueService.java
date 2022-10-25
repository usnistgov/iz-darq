package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.iz.darq.analyzer.model.variable.DynamicQueryVariableRef;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRef;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.analyzer.service.QueryValueResolverService;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.repository.ExternalQueryVariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueryValueService implements QueryValueResolverService {

    @Autowired
    ADFQueryVariableService adfQueryVariableService;
    @Autowired
    ExternalQueryVariableService externalQueryVariableService;
    @Autowired
    ExternalQueryVariableRepository repository;

    public List<QueryVariableDisplay> getVariablesDisplay() {
        List<QueryVariable> variables = new ArrayList<>();
        variables.addAll(repository.findAll());
        variables.addAll(adfQueryVariableService.getListOfADFVariable());

        List<QueryVariableDisplay> displays = new ArrayList<>();
        for(QueryVariable variable: variables) {
            QueryVariableDisplay display = new QueryVariableDisplay();
            display.setId(variable.getId());
            display.setName(variable.getName());
            display.setDescription(variable.getDescription());
            display.setType(variable.getType());
            if(variable instanceof ExternalQueryVariable) {
                if(variable instanceof GlobalExternalQueryVariable) {
                    GlobalExternalQueryVariable globalExternalQueryVariable = (GlobalExternalQueryVariable) variable;
                    display.setScope(globalExternalQueryVariable.getScope());
                    display.setSnapshot(this.getVariableValue(globalExternalQueryVariable));
                    DynamicQueryVariableRef dynamicQueryVariableRef = new DynamicQueryVariableRef();
                    dynamicQueryVariableRef.setId(variable.getId());
                    dynamicQueryVariableRef.setType(variable.getType());
                    dynamicQueryVariableRef.setScope(globalExternalQueryVariable.getScope());
                    display.setDynamic(dynamicQueryVariableRef);
                } else if(variable instanceof IISExternalQueryVariable) {
                    IISExternalQueryVariable iisExternalQueryVariable = (IISExternalQueryVariable) variable;
                    display.setScope(iisExternalQueryVariable.getScope());
                    DynamicQueryVariableRef dynamicQueryVariableRef = new DynamicQueryVariableRef();
                    dynamicQueryVariableRef.setId(variable.getId());
                    dynamicQueryVariableRef.setType(variable.getType());
                    dynamicQueryVariableRef.setScope(iisExternalQueryVariable.getScope());
                    display.setDynamic(dynamicQueryVariableRef);
                }
            } else if(variable instanceof ADFQueryVariable) {
                DynamicQueryVariableRef dynamicQueryVariableRef = new DynamicQueryVariableRef();
                dynamicQueryVariableRef.setId(variable.getId());
                dynamicQueryVariableRef.setType(variable.getType());
                display.setDynamic(dynamicQueryVariableRef);
            }
            displays.add(display);
        }
        return displays;
    }

    @Override
    public QueryVariableRefInstance resolveInstanceValue(QueryVariableRef value, ADFile file, String facilityId) throws Exception {
        switch (value.getQueryValueType()) {
            case STATIC:
                if(value instanceof QueryVariableRefInstance) {
                    return (QueryVariableRefInstance) value;
                } else {
                    throw new Exception("Invalid variable reference");
                }
            case DYNAMIC:
                if(value instanceof DynamicQueryVariableRef) {
                    return this.getDynamicQueryVariableValue((DynamicQueryVariableRef) value, file, facilityId);
                } else {
                    throw new Exception("Invalid variable reference");
                }
        }
        throw new Exception("Not able to resolve variable reference");
    }

    public QueryVariable getDynamicQueryVariableRef(DynamicQueryVariableRef value) throws Exception {
        switch (value.getType()) {
            case ADF:
                return this.adfQueryVariableService.getADFVariableById(value.getId());
            case EXTERNAL:
                return this.repository.findOne(value.getId());
        }
        throw new Exception("Unknown variable type");
    }

    public QueryVariableRefInstance getDynamicQueryVariableValue(DynamicQueryVariableRef value, ADFile file, String facilityId) throws Exception {
        QueryVariable variable = this.getDynamicQueryVariableRef(value);
        if(variable == null) {
            throw new Exception("Variable (type = "+ value.getType() + ", id = "+ value.getId() + ")");
        }
        switch (variable.getType()) {
            case ADF:
                if(!(variable instanceof ADFQueryVariable)) {
                    throw new Exception("Invalid variable reference");
                } else {
                    return this.getVariableValue((ADFQueryVariable) variable, file);
                }
            case EXTERNAL:
                if(!(variable instanceof ExternalQueryVariable)) {
                    throw new Exception("Invalid variable reference");
                } else {
                    ExternalQueryVariable externalQueryVariable = (ExternalQueryVariable) variable;
                    switch (externalQueryVariable.getScope()) {
                        case IIS:
                            if(!(externalQueryVariable instanceof IISExternalQueryVariable)) {
                                throw new Exception("Invalid variable reference");
                            } else {
                                return this.getVariableValue((IISExternalQueryVariable) externalQueryVariable, facilityId);
                            }
                        case GLOBAL:
                            if(!(externalQueryVariable instanceof GlobalExternalQueryVariable)) {
                                throw new Exception("Invalid variable reference");
                            } else {
                                return this.getVariableValue((GlobalExternalQueryVariable) externalQueryVariable);
                            }
                    }
                }
        }
        throw new Exception("Invalid variable reference");
    }

    public QueryVariableRefInstance getVariableValue(GlobalExternalQueryVariable variable) {
        return new QueryVariableRefInstance(
                variable.getId(),
                variable.getType(),
                variable.getScope(),
                variable.getValue(),
                variable.getDateValueUpdated(),
                null,
                variable.getComment(),
                variable.getName(),
                variable.getDescription()
        );
    }

    public QueryVariableRefInstance getVariableValue(IISExternalQueryVariable variable, String facilityId) throws Exception {
        IISVariableValue value = variable.getValues().stream()
                .filter((v) -> v.getFacilityId().equals(facilityId))
                .findFirst()
                .orElse(null);

        if(value == null) {
            throw new Exception("Variable value not found for this facility (ID = "+ facilityId+")");
        } else {
            return new QueryVariableRefInstance(
                    variable.getId(),
                    variable.getType(),
                    variable.getScope(),
                    value.getValue(),
                    value.getDateValueUpdated(),
                    facilityId,
                    value.getComment(),
                    variable.getName(),
                    variable.getDescription()
            );
        }
    }

    public QueryVariableRefInstance getVariableValue(ADFQueryVariable variable, ADFile file) throws Exception {
        return this.adfQueryVariableService.getADFValueInstance(variable, file);
    }
}
