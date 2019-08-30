package gov.nist.healthcare.iz.darq.digest.service;
import gov.nist.healthcare.iz.darq.parser.service.model.AggregateParsedRecord;

import java.io.IOException;
import java.nio.file.Path;

public abstract class PatientRecordIterator {

    protected Path patientFile;
    protected Path vaccinationFile;

    public PatientRecordIterator(Path patientFile, Path vaccinationFile) throws IOException {
        this.patientFile = patientFile;
        this.vaccinationFile = vaccinationFile;
    }

    public abstract boolean hasNext();

    public abstract AggregateParsedRecord next() throws Exception;

    public abstract int progress();

    public abstract void close() throws IOException;
}
