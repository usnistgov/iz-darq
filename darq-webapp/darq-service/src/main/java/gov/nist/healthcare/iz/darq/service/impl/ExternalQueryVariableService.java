package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.ExternalQueryVariableScope;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableType;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.repository.ExternalQueryVariableRepository;
import gov.nist.healthcare.iz.darq.repository.FacilityRepository;
import gov.nist.healthcare.iz.darq.service.exception.VariableImportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExternalQueryVariableService {

    @Autowired
    ExternalQueryVariableRepository repository;
    @Autowired
    FacilityRepository facilityRepository;
    private final int VARIABLE_ID = 0;
    private final int VARIABLE_NAME = 1;
    private final int VARIABLE_DESCRIPTION = 2;
    private final int GLOBAL_VARIABLE_VALUE = 3;
    private final int IIS_VARIABLE_VALUE = 3;
    private final int GLOBAL_VARIABLE_COMMENT = 4;

    public List<ExternalQueryVariable> importFromCSV(InputStream variablesInputStream, ExternalQueryVariableScope scope) throws VariableImportException {
        InputStreamReader inputStreamReader = new InputStreamReader(variablesInputStream);
        List<ExternalQueryVariable> imported = new ArrayList<>();
        try (CSVReader reader = new CSVReader(inputStreamReader)) {
            String[] header = reader.readNext();
            if(header == null) {
                throw new CsvValidationException("Empty CSV File");
            }
            List<ExternalQueryVariable> variables = readVariables(reader, header, scope);
            for(ExternalQueryVariable variable: variables) {
                this.createOrUpdateVariable(variable);
                imported.add(variable);
            }
            return imported;
        } catch (Exception e) {
            throw new VariableImportException(e.getMessage(), imported, e);
        }
    }

    public void exportToCSV(OutputStream outputStream, ExternalQueryVariableScope scope) throws IOException {
        List<ExternalQueryVariable> variables = this.repository.findByScope(scope);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        try(CSVWriter writer = new CSVWriter(outputStreamWriter)) {
            if(scope.equals(ExternalQueryVariableScope.GLOBAL)) {
                String[] header = {
                        "VARIABLE_ID",
                        "VARIABLE_NAME",
                        "VARIABLE_DESCRIPTION",
                        "VALUE",
                        "COMMENT"
                };
                writer.writeNext(header);
                for(ExternalQueryVariable variable: variables) {
                    this.writeGlobalVariable(writer, (GlobalExternalQueryVariable) variable);
                }
            } else {
                List<String> headerList = new ArrayList<>(
                        Arrays.asList("VARIABLE_ID", "VARIABLE_NAME", "VARIABLE_DESCRIPTION")
                );
                Map<String, String> facilityNames = variables.stream()
                        .map((v) -> (IISExternalQueryVariable) v)
                        .flatMap((iv) -> iv.getValues().stream())
                        .distinct()
                        .collect(Collectors.toMap(
                                IISVariableValue::getFacilityId,
                                (vv) -> {
                                    Facility f = this.facilityRepository.findOne(vv.getFacilityId());
                                    return f == null ? "[DELETED]" : f.getName();
                                }
                        ));
                List<String> sortedFacilitiesByName = facilityNames.keySet().stream().sorted((a, b) -> {
                   String nameA = facilityNames.get(a);
                   String nameB = facilityNames.get(b);
                   return nameA.compareTo(nameB);
                }).collect(Collectors.toList());
                Map<String, Integer> facilitiesLocations = sortedFacilitiesByName.stream()
                        .collect(
                                Collectors.toMap(
                                    (f) -> f,
                                    (f) -> sortedFacilitiesByName.indexOf(f) * 2 + headerList.size()
                                )
                        );
                sortedFacilitiesByName.forEach((facilityId) -> {
                    headerList.add(facilityNames.get(facilityId));
                    headerList.add("COMMENT");
                });
                writer.writeNext(headerList.toArray(new String[0]));
                for(ExternalQueryVariable variable: variables) {
                    this.writeIISVariable(writer, (IISExternalQueryVariable) variable, facilitiesLocations, headerList.size());
                }
            }
        } catch (IOException e) {
            throw e;
        }
    }

    void writeGlobalVariable(CSVWriter writer, GlobalExternalQueryVariable variable) {
        String[] record = {
                variable.getId(),
                variable.getName(),
                variable.getDescription(),
                variable.getValue() + "",
                variable.getComment()
        };
        writer.writeNext(record);
    }
    void writeIISVariable(CSVWriter writer, IISExternalQueryVariable variable, Map<String, Integer> facilities, int size) {
        String[] record = new String[size];
        record[VARIABLE_ID] = variable.getId();
        record[VARIABLE_NAME] = variable.getName();
        record[VARIABLE_DESCRIPTION] = variable.getDescription();
        for(int i = VARIABLE_DESCRIPTION + 1; i < size; i++) {
            record[i] = "";
        }
        for(IISVariableValue value: variable.getValues()) {
            int location = facilities.get(value.getFacilityId());
            record[location] = value.getValue() + "";
            record[location + 1] = Strings.nullToEmpty(value.getComment());
        }
        writer.writeNext(record);
    }

    List<ExternalQueryVariable> readVariables(CSVReader reader, String[] headers, ExternalQueryVariableScope scope) throws CsvValidationException, IOException {
        List<ExternalQueryVariable> variables = new ArrayList<>();
        int minColumnNumber = scope.equals(ExternalQueryVariableScope.IIS) ? 3 : 4;
        String[] record;
        int line = 1;
        while ((record = reader.readNext()) != null) {
            if(record.length < minColumnNumber) {
                throw new CsvValidationException("Invalid number of columns in line :" + line);
            }
            String id = record[VARIABLE_ID];
            String name = record[VARIABLE_NAME];
            String description = record[VARIABLE_DESCRIPTION];
            if(scope.equals(ExternalQueryVariableScope.IIS)) {
                variables.add(makeIISQueryVariable(id, name, description, record, headers));
            } else if(scope.equals(ExternalQueryVariableScope.GLOBAL)) {
                variables.add(makeGlobalQueryVariable(id, name, description, record, headers));
            }
            line++;
        }
        return variables;
    }


    GlobalExternalQueryVariable makeGlobalQueryVariable(String id, String name, String description, String[] record, String[] headers) throws CsvValidationException {
        try {
            int value = Integer.parseInt(record[GLOBAL_VARIABLE_VALUE]);
            String comment = record.length > GLOBAL_VARIABLE_COMMENT ? record[GLOBAL_VARIABLE_COMMENT] : "";
            GlobalExternalQueryVariable globalExternalQueryVariable = new GlobalExternalQueryVariable();
            globalExternalQueryVariable.setId(id);
            globalExternalQueryVariable.setName(name);
            globalExternalQueryVariable.setDescription(description);
            globalExternalQueryVariable.setValue(value);
            globalExternalQueryVariable.setComment(comment);
            return globalExternalQueryVariable;
        } catch (Exception e) {
            throw new CsvValidationException(e.getMessage());
        }
    }

    IISExternalQueryVariable makeIISQueryVariable(String id, String name, String description, String[] record, String[] headers) throws CsvValidationException {
        IISExternalQueryVariable iisExternalQueryVariable = new IISExternalQueryVariable();
        iisExternalQueryVariable.setId(id);
        iisExternalQueryVariable.setName(name);
        iisExternalQueryVariable.setDescription(description);
        Set<IISVariableValue> values = new HashSet<>();
        int i = IIS_VARIABLE_VALUE;
        while (i < record.length) {
            if(!Strings.isNullOrEmpty(record[i])) {
                try {
                    int value = Integer.parseInt(record[i]);
                    String comment = record.length > (i + 1) ? record[i + 1] : "";
                    if(i >= headers.length) {
                        throw new Exception("Facility name not found in the header for column : " + i);
                    }
                    String facilityName = headers[i];
                    Facility facility = this.facilityRepository.findByName(facilityName.trim());
                    if(facility == null) {
                        throw new Exception("Facility " + facilityName + " not found");
                    }
                    IISVariableValue variableValue = new IISVariableValue();
                    variableValue.setValue(value);
                    variableValue.setComment(comment);
                    variableValue.setFacilityId(facility.getId());
                    values.add(variableValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CsvValidationException(e.getMessage());
                }
            }
            i += 2;
        }
        iisExternalQueryVariable.setValues(values);
        return iisExternalQueryVariable;
    }

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

    public ExternalQueryVariable createOrUpdateVariable(ExternalQueryVariable variable) throws Exception {
        boolean exists = !Strings.isNullOrEmpty(variable.getId()) && this.repository.exists(variable.getId());
        if(variable instanceof GlobalExternalQueryVariable) {
            return exists ?
                    this.updateGlobalVariable((GlobalExternalQueryVariable) variable) :
                    this.createGlobalVariable((GlobalExternalQueryVariable) variable);
        } else if(variable instanceof IISExternalQueryVariable) {
            return exists ?
                    this.updateIISVariable((IISExternalQueryVariable) variable) :
                    this.createIISVariable((IISExternalQueryVariable) variable);
        }
        throw new Exception("Unknown variable type");
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
