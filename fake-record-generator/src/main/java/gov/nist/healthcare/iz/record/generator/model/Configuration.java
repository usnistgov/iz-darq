package gov.nist.healthcare.iz.record.generator.model;
import gov.nist.healthcare.iz.record.generator.field.Field;
import org.joda.time.LocalDate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Configuration {
    Date asOf;
    int numberOfPatients;
    int minVxPerPatient;
    int maxVxPerPatient;
    int numberOfVaccinations;
    Map<String, Field> patients;
    Map<String, Field> vaccinations;
    Map<String, Set<String>> enums;
    List<ValueMapping> valueMappings;

    public Date getAsOf() {
        return asOf;
    }

    public void setAsOf(Date asOf) {
        this.asOf = asOf;
    }

    public int getNumberOfPatients() {
        return numberOfPatients;
    }

    public void setNumberOfPatients(int numberOfPatients) {
        this.numberOfPatients = numberOfPatients;
    }

    public Map<String, Field> getPatients() {
        return patients;
    }

    public void setPatients(Map<String, Field> patients) {
        this.patients = patients;
    }

    public Map<String, Field> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(Map<String, Field> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public int getMinVxPerPatient() {
        return minVxPerPatient;
    }

    public void setMinVxPerPatient(int minVxPerPatient) {
        this.minVxPerPatient = minVxPerPatient;
    }

    public int getMaxVxPerPatient() {
        return maxVxPerPatient;
    }

    public void setMaxVxPerPatient(int maxVxPerPatient) {
        this.maxVxPerPatient = maxVxPerPatient;
    }

    public int getNumberOfVaccinations() {
        return numberOfVaccinations;
    }

    public void setNumberOfVaccinations(int numberOfVaccinations) {
        this.numberOfVaccinations = numberOfVaccinations;
    }

    public Map<String, Set<String>> getEnums() {
        return enums;
    }

    public void setEnums(Map<String, Set<String>> enums) {
        this.enums = enums;
    }

    public List<ValueMapping> getValueMappings() {
        return valueMappings;
    }

    public void setValueMappings(List<ValueMapping> valueMappings) {
        this.valueMappings = valueMappings;
    }
}
