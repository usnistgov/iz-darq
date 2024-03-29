package gov.nist.healthcare.iz.darq.digest.domain;

import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;
import gov.nist.healthcare.iz.darq.digest.domain.VaccinationPayload;

public class ADPayload {
    PatientPayload patientPayload;
    VaccinationPayload vaccinationPayload;

    public ADPayload() {
    }

    public ADPayload(PatientPayload patientPayload, VaccinationPayload vaccinationPayload) {
        this.patientPayload = patientPayload;
        this.vaccinationPayload = vaccinationPayload;
    }

    public PatientPayload getPatientPayload() {
        return patientPayload;
    }

    public void setPatientPayload(PatientPayload patientPayload) {
        this.patientPayload = patientPayload;
    }

    public VaccinationPayload getVaccinationPayload() {
        return vaccinationPayload;
    }

    public void setVaccinationPayload(VaccinationPayload vaccinationPayload) {
        this.vaccinationPayload = vaccinationPayload;
    }
}
