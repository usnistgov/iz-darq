package gov.nist.healthcare.iz.darq.analysis.stats;

import java.lang.reflect.Field;
import java.util.List;

import gov.nist.healthcare.iz.darq.analysis.Statistic;
import gov.nist.healthcare.iz.darq.analysis.SubjectAnalysis;
import gov.nist.healthcare.iz.darq.analysis.service.StatisticKind;
import gov.nist.healthcare.iz.darq.parser.model.Address;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.Name;
import gov.nist.healthcare.iz.darq.parser.model.Patient;
import gov.nist.healthcare.iz.darq.parser.model.ResponsibleParty;
import gov.nist.healthcare.iz.darq.parser.model.VISInformation;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.type.DataUnit;
import gov.nist.healthcare.iz.darq.parser.type.DqDate;
import gov.nist.healthcare.iz.darq.parser.type.DqNumeric;
import gov.nist.healthcare.iz.darq.parser.type.DqString;

public class StatisticsProcessor {

	private List<StatisticKind> stators;

	public StatisticsProcessor(List<StatisticKind> stators) {
		super();
		this.stators = stators;
	}
	
	public void process(List<SubjectAnalysis> list, AggregatePatientRecord record) throws IllegalArgumentException, IllegalAccessException{
		count(list, "patient", record.patient);
		for(VaccineRecord vR : record.history){
			count(list, "vaccination", vR);
		}
	}
	
	public boolean isDataUnit(Field f){
		return f.getType().isAssignableFrom(DqString.class) || f.getType().isAssignableFrom(DqNumeric.class) || f.getType().isAssignableFrom(DqDate.class) || f.getType().isAssignableFrom(DqNumeric.class);
	}
	
	
	public void count(List<SubjectAnalysis> map, String field, Patient record) throws IllegalArgumentException, IllegalAccessException {
		Class<Patient> apr = Patient.class;
		for(Field f : apr.getDeclaredFields()){
			String name = field+"."+f.getName();
			
			if(f.getType().isAssignableFrom(Name.class)) {
				count(map, name, (Name) f.get(record));
			}
			else if(f.getType().isAssignableFrom(Address.class)) {
				count(map, name, (Address) f.get(record));
			}
			else if(f.getType().isAssignableFrom(ResponsibleParty.class)) {
				count(map, name, (ResponsibleParty) f.get(record));
			}
			else if(isDataUnit(f)) {
				count(map, name, (DataUnit<?>) f.get(record));
			}
		}
	}
	
	
	
	public void count(List<SubjectAnalysis> map, String field, VaccineRecord record) throws IllegalArgumentException, IllegalAccessException {
		Class<VaccineRecord> apr = VaccineRecord.class;
		for(Field f : apr.getDeclaredFields()){
			String name = field+"."+f.getName();
			
			if(f.getType().isAssignableFrom(Name.class)) {
				count(map, name, (Name) f.get(record));
			}
			else if(f.getType().isAssignableFrom(VISInformation.class)) {
				count(map, name, (VISInformation) f.get(record));
			}
			else if(isDataUnit(f)) {
				count(map, name, (DataUnit<?>) f.get(record));
			}
		}
	}
	
	public void count(List<SubjectAnalysis> map, String field, ResponsibleParty record) throws IllegalArgumentException, IllegalAccessException {
		Class<ResponsibleParty> apr = ResponsibleParty.class;
		for(Field f : apr.getDeclaredFields()){
			String name = field+"."+f.getName();
			
			if(f.getType().isAssignableFrom(Name.class)) {
				count(map, name, (Name) f.get(record));
			}
			else if(isDataUnit(f)) {
				count(map, name, (DataUnit<?>) f.get(record));
			}
		}
	}
	
	public void count(List<SubjectAnalysis> map, String field, Name record) throws IllegalArgumentException, IllegalAccessException {
		Class<Name> apr = Name.class;
		for(Field f : apr.getDeclaredFields()){
			String name = field+"."+f.getName();
			
			if(isDataUnit(f)) {
				count(map, name, (DataUnit<?>) f.get(record));
			}
		}
	}
	
	public void count(List<SubjectAnalysis> map, String field, Address record) throws IllegalArgumentException, IllegalAccessException {
		Class<Address> apr = Address.class;
		for(Field f : apr.getDeclaredFields()){
			String name = field+"."+f.getName();
			
			if(isDataUnit(f)) {
				count(map, name, (DataUnit<?>) f.get(record));
			}
		}
	}
	
	public void count(List<SubjectAnalysis> map, String field, VISInformation record) throws IllegalArgumentException, IllegalAccessException {
		Class<VISInformation> apr = VISInformation.class;
		for(Field f : apr.getDeclaredFields()){
			String name = field+"."+f.getName();
			
			if(isDataUnit(f)) {
				count(map, name, (DataUnit<?>) f.get(record));
			}
		}
	}
	
	public void count(List<SubjectAnalysis> map, String field, DataUnit<?> record) {
		SubjectAnalysis sub = new SubjectAnalysis();
		sub.setFieldOfInterest(field);
		
		for(StatisticKind stK : stators){
			Statistic s = stK.process(record);
			sub.getStatistics().add(s);
		}
	
		
		int i = map.indexOf(sub);
		if(i == -1){
			map.add(sub);
		}
		else {
			map.get(i).factorIn(sub);
		}
	}
}
