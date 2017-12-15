package gov.nist.healthcare.iz.darq.analysis.service;

import java.util.List;
import gov.nist.healthcare.iz.darq.analysis.SubjectAnalysis;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

public abstract class StatisticCalculator {
	
	public abstract String id();
	public abstract void count(List<SubjectAnalysis> collector, AggregatePatientRecord record) throws IllegalArgumentException, IllegalAccessException;
	public static void merge(List<SubjectAnalysis> map, List<SubjectAnalysis> list){
		for(SubjectAnalysis sub : list){
			int i = map.indexOf(sub);
			if(i != -1){
				SubjectAnalysis e_sub = map.get(i);
				e_sub.factorIn(sub);
			}
			else {
				map.add(sub);
			}
		}
	}

}
