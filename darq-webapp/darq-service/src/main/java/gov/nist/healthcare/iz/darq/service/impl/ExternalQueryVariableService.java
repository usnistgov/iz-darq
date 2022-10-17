package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.repository.ExternalQueryVariableRepository;
import gov.nist.healthcare.iz.darq.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExternalQueryVariableService {

    @Autowired
    ExternalQueryVariableRepository repository;
    @Autowired
    FacilityRepository facilityRepository;

    void validateVariableMetadata(ExternalQueryVariable variable) throws Exception {
        if(Strings.isNullOrEmpty(variable.getName())) {
            throw new Exception("Variable name is required");
        }

        if(Strings.isNullOrEmpty(variable.getDescription())) {
            throw new Exception("Variable description is required");
        }

        if(Strings.isNullOrEmpty(variable.getId())) {
            throw new Exception("Variable ID is required");
        }

        if(!variable.getType().equals(QueryVariableType.EXTERNAL)) {
            throw new Exception("Invalid variable data");
        }
    }

    void validateIISVariableSet(Set<IISVariableValue> values) throws Exception {
        List<String> issues = new ArrayList<>();
        List<Facility> found = new ArrayList<>();
        for(IISVariableValue value: values) {
            if(value == null) {
                issues.add("List of values contain a null value");
            } else {
                Facility facility = this.facilityRepository.findOne(value.getFacilityId());
                if(facility == null) {
                    issues.add("Facility with ID = " + value.getFacilityId() + " is not found");
                } else if(found.stream().anyMatch((f) -> f.getId().equals(value.getFacilityId()))) {
                    issues.add("Facility " + facility.getName() + " has duplicate value");
                }
                found.add(facility);
            }
        }
        if(issues.size() > 0) {
            throw new Exception(String.join(", ", issues));
        }
    }

    void validateGlobalVariable(GlobalExternalQueryVariable variable) throws Exception {
        validateVariableMetadata(variable);
        if(!variable.getScope().equals(ExternalQueryVariableScope.GLOBAL)) {
            throw new Exception("Invalid variable data");
        }
    }

    void validateIISVariable(IISExternalQueryVariable variable) throws Exception {
        validateVariableMetadata(variable);
        if(!variable.getScope().equals(ExternalQueryVariableScope.IIS)) {
            throw new Exception("Invalid variable data");
        }
        validateIISVariableSet(variable.getValues());
    }

    public ExternalQueryVariable createVariable(ExternalQueryVariable variable) throws Exception {
        if(variable instanceof GlobalExternalQueryVariable) {
            return this.createGlobalVariable((GlobalExternalQueryVariable) variable);
        } else if(variable instanceof IISExternalQueryVariable) {
            return this.createIISVariable((IISExternalQueryVariable) variable);
        }
        throw new Exception("Unknown variable type");
    }

    public ExternalQueryVariable updateVariable(ExternalQueryVariable variable) throws Exception {
        if(variable instanceof GlobalExternalQueryVariable) {
            return this.updateGlobalVariable((GlobalExternalQueryVariable) variable);
        } else if(variable instanceof IISExternalQueryVariable) {
            return this.updateIISVariable((IISExternalQueryVariable) variable);
        }
        throw new Exception("Unknown variable type");
    }

    GlobalExternalQueryVariable createGlobalVariable(GlobalExternalQueryVariable variable) throws Exception {
        this.validateGlobalVariable(variable);

        if(this.repository.exists(variable.getId())) {
            throw new Exception("Variable ID already used");
        }

        Date now = new Date();
        variable.setDateUpdated(now);
        variable.setDateValueUpdated(now);
        variable.setDateCreated(now);
        return this.repository.save(variable);
    }

    ExternalQueryVariable updateGlobalVariable(GlobalExternalQueryVariable variable) throws Exception {
        this.validateGlobalVariable(variable);
        ExternalQueryVariable existing = this.repository.findOne(variable.getId());
        if(existing != null) {
            if(
                    !(existing instanceof GlobalExternalQueryVariable) ||
                    !existing.getScope().equals(ExternalQueryVariableScope.GLOBAL)
            ){
                throw new Exception("Incompatible variable type found");
            } else {
                GlobalExternalQueryVariable existingGlobalVariable = (GlobalExternalQueryVariable) existing;
                Date now = new Date();
                variable.setDateCreated(existingGlobalVariable.getDateCreated());
                variable.setDateUpdated(now);
                if(variable.getValue() != existingGlobalVariable.getValue()) {
                    variable.setDateValueUpdated(now);
                } else {
                    variable.setDateValueUpdated(existingGlobalVariable.getDateValueUpdated());
                }
                return this.repository.save(variable);
            }
        } else {
            throw new Exception("Variable " + variable.getId() + " not found");
        }
    }

    ExternalQueryVariable updateIISVariable(IISExternalQueryVariable variable) throws Exception {
        this.validateIISVariable(variable);
        ExternalQueryVariable existing = this.repository.findOne(variable.getId());
        if(existing != null) {
            if(
                    !(existing instanceof IISExternalQueryVariable) ||
                            !existing.getScope().equals(ExternalQueryVariableScope.IIS)
            ){
                throw new Exception("Incompatible variable type found");
            } else {
                IISExternalQueryVariable existingIISVariable = (IISExternalQueryVariable) existing;
                Date now = new Date();
                variable.setDateCreated(existingIISVariable.getDateCreated());
                variable.setDateUpdated(now);
                variable.getValues().forEach((v) -> {
                    IISVariableValue found = existingIISVariable.getValues().stream()
                            .filter((ev) -> ev.getFacilityId().equals(v.getFacilityId()))
                            .findFirst()
                            .orElse(null);

                    if(found != null) {
                        v.setDateUpdated(found.getDateUpdated());
                        v.setDateValueUpdated(found.getDateValueUpdated());

                        if(found.getValue() != v.getValue()) {
                            v.setDateValueUpdated(now);
                            v.setDateUpdated(now);
                        } else if(!Strings.nullToEmpty(found.getComment()).equals(Strings.nullToEmpty(v.getComment()))) {
                            v.setDateUpdated(now);
                        }
                    }
                });
                return this.repository.save(variable);
            }
        } else {
            throw new Exception("Variable " + variable.getId() + " not found");
        }
    }

    IISExternalQueryVariable createIISVariable(IISExternalQueryVariable variable) throws Exception {
        validateVariableMetadata(variable);

        if(!variable.getScope().equals(ExternalQueryVariableScope.IIS)) {
            throw new Exception("Invalid variable data");
        }

        validateIISVariableSet(variable.getValues());

        if(this.repository.exists(variable.getId())) {
            throw new Exception("Variable ID already used");
        }

        Date now = new Date();
        variable.setDateCreated(now);
        variable.getValues().forEach((v) -> {
            v.setDateUpdated(now);
            v.setDateValueUpdated(now);
        });
        return this.repository.save(variable);
    }
}
