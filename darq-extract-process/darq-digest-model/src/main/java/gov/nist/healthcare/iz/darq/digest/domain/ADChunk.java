package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.*;


public class ADChunk {

	public static final int MAX_ISSUES = 50;
	private int unreadPatients = 0;
	private int unreadVaccinations = 0;
	private int nbVaccinations = 0;
	private int nbPatients = 0;
	private int maxVaccination = 0;
	private int minVaccination = 99999;
	private IssueList issues;
	private Map<String, String> providers;
	private Map<String, PatientPayload> generalPatientPayload;
	private Map<String, Map<String, ADPayload>> reportingGroupPayload;
	private Map<String, ExtractFraction> extraction;
	private Map<Field, Set<String>> values;
	private Map<String, Set<String>> codes;
	
	public ADChunk(
			Map<String, String> providers,
			Map<String, PatientPayload> generalPatientPayload,
			Map<String, Map<String, ADPayload>> reportingGroupPayload,
			Map<String, ExtractFraction> extraction,
			int nbVaccinations,
			int nbPatients,
			Map<Field, Set<String>> values,
			Map<String, Set<String>> codes) {
		super();
		this.providers = providers;
		this.generalPatientPayload = generalPatientPayload;
		this.reportingGroupPayload = reportingGroupPayload;
		this.extraction = extraction;
		this.nbVaccinations = nbVaccinations;
		this.nbPatients = nbPatients;
		this.values = values;
		this.codes = codes;
	}
	
	
	public ADChunk() {
		super();
		generalPatientPayload = new HashMap<>();
		reportingGroupPayload = new HashMap<>();
		extraction = new HashMap<>();
		providers = new HashMap<>();
		this.issues = new IssueList(MAX_ISSUES);
	}

	public Map<String, PatientPayload> getGeneralPatientPayload() {
		return generalPatientPayload;
	}

	public void setGeneralPatientPayload(Map<String, PatientPayload> generalPatientPayload) {
		this.generalPatientPayload = generalPatientPayload;
	}

	public Map<String, Map<String, ADPayload>> getReportingGroupPayload() {
		return reportingGroupPayload;
	}

	public void setReportingGroupPayload(Map<String, Map<String, ADPayload>> reportingGroupPayload) {
		this.reportingGroupPayload = reportingGroupPayload;
	}

	public Map<String, ExtractFraction> getExtraction() {
		return extraction;
	}
	public void setExtraction(Map<String, ExtractFraction> extraction) {
		this.extraction = extraction;
	}
	public Map<String, String> getProviders() {
		return providers;
	}
	public void setProviders(Map<String, String> providers) {
		this.providers = providers;
	}


	public void addIssue(String issue) {
		this.issues.add(issue);
	}

	public void addIssues(ADChunk chunk) {
		this.issues.addAllPossible(chunk.issues);
	}

	public void addIssues(List<String> issues) {
		this.issues.addAllPossible(issues);
	}

	public List<String> issueList() {
		return this.issues.toList();
	}

	@Override
	public String toString() {
		return "ADChunk [providers=" + providers + ", reportingGroupPayload=" + this.reportingGroupPayload + ", generalPatientPayload="
				+ this.generalPatientPayload + ", extraction=" + extraction + "]";
	}


	public int getNbVaccinations() {
		return nbVaccinations;
	}


	public void setNbVaccinations(int nbVaccinations) {
		this.nbVaccinations = nbVaccinations;
	}


	public int getNbPatients() {
		return nbPatients;
	}


	public void setNbPatients(int nbPatients) {
		this.nbPatients = nbPatients;
	}


	public int getMaxVaccination() {
		return maxVaccination;
	}


	public void setMaxVaccination(int v) {
		this.maxVaccination = Math.max(v, maxVaccination);
	}


	public int getMinVaccination() {
		return minVaccination;
	}


	public void setMinVaccination(int v) {
		this.minVaccination = Math.min(v, minVaccination);
	}


	public Map<Field, Set<String>> getValues() {
		return values;
	}


	public void setValues(Map<Field, Set<String>> values) {
		this.values = values;
	}


	public Map<String, Set<String>> getCodes() {
		return codes;
	}


	public void setCodes(Map<String, Set<String>> codes) {
		this.codes = codes;
	}

	public int getUnreadPatients() {
		return unreadPatients;
	}

	public void setUnreadPatients(int unreadPatients) {
		this.unreadPatients = unreadPatients;
	}

	public int getUnreadVaccinations() {
		return unreadVaccinations;
	}

	public void setUnreadVaccinations(int unreadVaccinations) {
		this.unreadVaccinations = unreadVaccinations;
	}
}
