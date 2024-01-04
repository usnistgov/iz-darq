package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public enum Field {
	PROVIDER(AnalysisType.V, AnalysisType.VD, AnalysisType.VT, AnalysisType.PD_RG, AnalysisType.PT_RG),
	AGE_GROUP(AnalysisType.values()),
	TABLE(AnalysisType.VT, AnalysisType.PT),
	CODE(AnalysisType.VT, AnalysisType.PT),
	DETECTION(AnalysisType.VD, AnalysisType.PD),
	EVENT(AnalysisType.V),
	GENDER(AnalysisType.V),
	VACCINE_CODE(AnalysisType.V),
	VACCINATION_YEAR(AnalysisType.V);

	final AnalysisType[] compatibilityGroups;
	Field(AnalysisType... compatibility) {
		this.compatibilityGroups = compatibility;
	}
		
	public AnalysisType[] getCompatibilityGroups() {
		return compatibilityGroups;
	}
	
	public static boolean areCompatible(Analysis analysis, Field... f){
		if(f.length == 1) return inAnalysis(f[0], analysis);
		else {
			Set<AnalysisType> set = new HashSet<>(Arrays.asList(f[0].getCompatibilityGroups()));
			for(int i = 1; i < f.length; i++){
				set.retainAll(Arrays.asList(f[i].getCompatibilityGroups()));
			}
			for(AnalysisType cg : set){
				if(cg.getScope().equals(analysis))
					return true;
			}
			return false;
		}	
	}
	
	private static boolean inAnalysis(Field f, Analysis a) {
		for(AnalysisType cg : f.getCompatibilityGroups()){
			if(cg.getScope().equals(a))
				return true;
		}
		return false;
	}
	
}
