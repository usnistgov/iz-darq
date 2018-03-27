package gov.nist.healthcare.iz.darq.parser.model;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;


public class AggregatePatientRecord {
	
	public String ID;
	@FieldName("Patient Information")
	public Patient patient;
	@FieldName("Vaccination History")
	public List<VaccineRecord> history;
	
	
	public AggregatePatientRecord(String iD, Patient patient, List<VaccineRecord> history) {
		super();
		ID = iD;
		this.patient = patient;
		this.history = history;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}