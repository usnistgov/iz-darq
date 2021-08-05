package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public enum Field {
	PROVIDER(_CG.V, _CG.VD, _CG.VT, _CG.PD_RG, _CG.PT_RG),
	AGE_GROUP(_CG.values()), 
	TABLE(_CG.VT, _CG.PT), 
	CODE(_CG.VT, _CG.PT), 
	DETECTION(_CG.VD, _CG.PD), 
	EVENT(_CG.V), 
	GENDER(_CG.V), 
	VACCINE_CODE(_CG.V),
	VACCINATION_YEAR(_CG.V);
	
	//-- FIELDS COMPATIBILITY GROUP
	public static enum _CG {
		V(Analysis.REPORTING_GROUP_AGG_SECTION),
		VD(Analysis.REPORTING_GROUP_AGG_SECTION),
		VT(Analysis.REPORTING_GROUP_AGG_SECTION),
		PD(Analysis.GENERAL_PATIENT_SECTION),
		PT(Analysis.GENERAL_PATIENT_SECTION),
		PD_RG(Analysis.REPORTING_GROUP_AGG_SECTION),
		PT_RG(Analysis.REPORTING_GROUP_AGG_SECTION);
		
		Analysis scope;
		private _CG(Analysis scope) {
			this.scope = scope;
		}
		
		public Analysis getScope() {
			return scope;
		}
	}
	
	_CG[] compatibilyGroups;
	private Field(_CG... compatibility) {
		this.compatibilyGroups = compatibility;
	}
		
	public _CG[] getCompatibilyGroups() {
		return compatibilyGroups;
	}
	
	public static boolean areCompatible(Analysis analysis, Field... f){
		if(f.length == 1) return inAnalysis(f[0], analysis);
		else {
			Set<_CG> set = new HashSet<_CG>(Arrays.asList(f[0].getCompatibilyGroups()));
			for(int i = 1; i < f.length; i++){
				set.retainAll(Arrays.asList(f[i].getCompatibilyGroups()));
			}
			for(_CG cg : set){
				if(cg.getScope().equals(analysis))
					return true;
			}
			return false;
		}	
	}
	
	private static boolean inAnalysis(Field f, Analysis a) {
		for(_CG cg : f.getCompatibilyGroups()){
			if(cg.getScope().equals(a))
				return true;
		}
		return false;
	}
	
}
