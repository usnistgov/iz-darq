package gov.nist.healthcare.iz.darq.parser.model;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import gov.nist.healthcare.iz.darq.parser.annotation.Field;

public class AggregatePatientRecord {
	
	public String ID;
	@Field(name = "Patient Information")
	public Patient patient;
	@Field(name = "Vaccination History")
	public List<VaccineRecord> history;
	
	public AggregatePatientRecord(String ID, Patient patient, List<VaccineRecord> history) {
		super();
		this.ID = ID;
		this.patient = patient;
		this.history = history;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}