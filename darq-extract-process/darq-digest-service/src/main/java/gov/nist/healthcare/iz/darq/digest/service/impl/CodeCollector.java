package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.parser.model.DataElement;

public class CodeCollector {
	
	public class Code {
		String value;
		String table;
		boolean vaccine;
		String provider;
		String ageGroup;
		
		public Code(String value, String table, boolean vaccine, String provider, String ageGroup) {
			super();
			this.value = value;
			this.table = table;
			this.vaccine = vaccine;
			this.provider = provider;
			this.ageGroup = ageGroup;
		}

		public String getValue() {
			return value;
		}

		public String getTable() {
			return table;
		}

		public boolean isVaccine() {
			return vaccine;
		}

		public String getProvider() {
			return provider;
		}

		public String getAgeGroup() {
			return ageGroup;
		}
		
		
		
	}
	
	public List<Code> codes;
	public Map<String, Integer> counts;
	public Map<String, Fraction> extract;
	
	
	public CodeCollector() {
		super();
		this.codes = new ArrayList<>();
		this.counts = new HashMap<>();
		this.extract = new HashMap<>();
	}
	
	

	public Map<String, Fraction> getExtract() {
		return extract;
	}



	public void setExtract(Map<String, Fraction> extract) {
		this.extract = extract;
	}



	public void readExtractionData(DataElement de){
		if(extract.containsKey(de.getKey())){
			if(de.getValue().hasValue())
				extract.get(de.getName()).incCount();
			else
				extract.get(de.getName()).incTotal();
		}
		else {
			extract.put(de.getName(), de.getValue().hasValue() ? new Fraction(1,1) : new Fraction(0,1));
		}
	}
	
	public void collectPatient(List<DataElement> _de, String ageGroup){
		this.collect(_de, false, ageGroup, null);
	}
	
	public void collectVaccination(List<DataElement> _de, String ageGroup, String provider){
		this.collect(_de, true, ageGroup, provider);
	}
	
	public void collect(List<DataElement> _de, boolean vx, String ageGroup, String provider){
		for(DataElement de : _de){
			this.readExtractionData(de);
			if(de.isCoded()){
				this.incTable(de.getTable());
				if(de.getValue().hasValue()){
					this.codes.add(new Code(de.getValue().toString(), de.getTable(), vx, provider, ageGroup));
				}
			}
		}
	}
	
	public void incTable(String table){
		if(counts.containsKey(table)){
			counts.put(table, counts.get(table) + 1);
		}
		else {
			counts.put(table, 1);	
		}
	}
	
	//--------- AGE ------- TABLE ----- CODE --- VALUE
	public Map<String, Map<String, TablePayload>> getPatientCodes(){
		return this.codes.stream().filter(c -> !c.vaccine)
		.collect(
				Collectors.groupingBy(
						Code::getAgeGroup,
						Collectors.groupingBy(
								Code::getTable,
								Collectors.collectingAndThen(
										Collectors.toList(),
										(x) -> {
											TablePayload t = new TablePayload();
											t.setTotal(x.size());
											t.setCodes(
													x.stream()
													.collect(
														Collectors.groupingBy(
															Code::getValue,
															Collectors.collectingAndThen(
																	Collectors.toList(),
																	(y) -> {
																		return y.size();
																	})
															)	
														)
													);
											return t;
											
										}
								)
						)
				)
			);
	}
	
	//--------- PROVIDER --- AGE ------- TABLE ----- CODE --- VALUE
	public Map<String, Map<String, Map<String, TablePayload>>> getVaccinationCodes(){
		return this.codes.stream().filter(c -> c.vaccine)
				.collect(
						Collectors.groupingBy(
								Code::getProvider,
								Collectors.groupingBy(
										Code::getAgeGroup,
										Collectors.groupingBy(
												Code::getTable,
												Collectors.collectingAndThen(
														Collectors.toList(),
														(x) -> {
															TablePayload t = new TablePayload();
															t.setTotal(x.size());
															t.setCodes(
																	x.stream()
																	.collect(
																		Collectors.groupingBy(
																			Code::getValue,
																			Collectors.collectingAndThen(
																					Collectors.toList(),
																					(y) -> {
																						return y.size();
																					})
																			)	
																		)
																	);
															return t;
															
														}
												)
											)
										)
								)
						);
	}
	
	public Map<String, Set<String>> codes(){
		return this.codes.stream()
		.collect(
				Collectors.groupingBy(
						Code::getTable,
						Collectors.collectingAndThen(
								Collectors.groupingBy(Code::getValue),
								(x) -> {
									return x.keySet();
								}
						)
				)
		);
	}


}
