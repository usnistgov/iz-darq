package gov.nist.healthcare.iz.darq.digest.domain;

//-- FIELDS COMPATIBILITY GROUP
public enum AnalysisType {
	V(Analysis.REPORTING_GROUP_AGG_SECTION),
	VD(Analysis.REPORTING_GROUP_AGG_SECTION),
	VT(Analysis.REPORTING_GROUP_AGG_SECTION),
	PD(Analysis.GENERAL_PATIENT_SECTION),
	PT(Analysis.GENERAL_PATIENT_SECTION),
	PD_RG(Analysis.REPORTING_GROUP_AGG_SECTION),
	PT_RG(Analysis.REPORTING_GROUP_AGG_SECTION);

	Analysis scope;

	AnalysisType(Analysis scope) {
		this.scope = scope;
	}

	public Analysis getScope() {
		return scope;
	}
}
