package gov.nist.healthcare.iz.darq.service.exception;

import gov.nist.healthcare.iz.darq.model.ExternalQueryVariable;

import java.util.List;

public class VariableImportException extends Exception {
    List<ExternalQueryVariable> imported;

    public VariableImportException(String message, List<ExternalQueryVariable> imported, Throwable cause) {
        super(message, cause);
        this.imported = imported;
    }

    public List<ExternalQueryVariable> getImported() {
        return imported;
    }
}
