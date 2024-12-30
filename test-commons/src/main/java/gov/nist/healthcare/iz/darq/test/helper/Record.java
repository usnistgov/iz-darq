package gov.nist.healthcare.iz.darq.test.helper;

import java.util.List;

public class Record {
	String patient;
	List<String> vaccinations;
	List<String> patientColumns;
	List<List<String>> vaccinationsColumns;

	public Record(
			String patient,
			List<String> patientColumns,
			List<String> vaccinations,
			List<List<String>> vaccinationsColumns
	) {
		this.patient = patient;
		this.patientColumns = patientColumns;
		this.vaccinations = vaccinations;
		this.vaccinationsColumns = vaccinationsColumns;
	}

	public String getPatient() {
		return patient;
	}

	public List<String> getVaccinations() {
		return vaccinations;
	}

	public List<List<String>> getVaccinationsColumns() {
		return vaccinationsColumns;
	}

	public void setVaccinationsColumns(List<List<String>> vaccinationsColumns) {
		this.vaccinationsColumns = vaccinationsColumns;
	}

	public List<String> getPatientColumns() {
		return patientColumns;
	}

	public void setPatientColumns(List<String> patientColumns) {
		this.patientColumns = patientColumns;
	}
}
