package gov.nist.healthcare.iz.darq.digest.app.exception;

import java.util.HashSet;

public class FileErrorCode {
    final boolean patients;
    final boolean vaccinations;
    final boolean configuration;

    public FileErrorCode(boolean patients, boolean vaccinations, boolean configuration) {
        this.patients = patients;
        this.vaccinations = vaccinations;
        this.configuration = configuration;
    }

    public String writeLocation() {
        HashSet<String> locations = new HashSet<>();
        if(patients) locations.add("Patients file");
        if(vaccinations) locations.add("Vaccinations file");
        if(configuration) locations.add("Configuration file");
        return String.join(", ", locations);
    }

    public int getLocationErrorCode() {
        int i = 0;
        if(patients) i += 1;
        if(vaccinations) i+= 2;
        if(configuration) i+= 4;
        return i;
    }
}
