package gov.nist.healthcare.iz.darq.test.helper;

import java.util.List;

public class Record {
	String patient;
	List<String> vaccinations;

	public Record(String patient, List<String> vaccinations) {
		this.patient = patient;
		this.vaccinations = vaccinations;
	}

	public String getPatient() {
		return patient;
	}

	public List<String> getVaccinations() {
		return vaccinations;
	}

}
