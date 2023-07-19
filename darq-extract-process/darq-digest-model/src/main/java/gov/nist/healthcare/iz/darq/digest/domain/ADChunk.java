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
	private int historical = 0;
	private int administered = 0;
	private Map<String, String> providers;
	private Map<String, PatientPayload> generalPatientPayload;
	private Map<String, Map<String, ADPayload>> reportingGroupPayload;
	private Map<String, ExtractFraction> extraction;

	public ADChunk(
			Map<String, String> providers,
			Map<String, PatientPayload> generalPatientPayload,
			Map<String, Map<String, ADPayload>> reportingGroupPayload,
			Map<String, ExtractFraction> extraction,
			int nbVaccinations,
			int nbPatients,
			int historical,
			int administered
	) {
		super();
		this.providers = providers;
		this.generalPatientPayload = generalPatientPayload;
		this.reportingGroupPayload = reportingGroupPayload;
		this.extraction = extraction;
		this.nbVaccinations = nbVaccinations;
		this.nbPatients = nbPatients;
		this.administered = administered;
		this.historical = historical;
	}
	
	
	public ADChunk() {
		super();
		generalPatientPayload = new HashMap<>();
		reportingGroupPayload = new HashMap<>();
		extraction = new HashMap<>();
		providers = new HashMap<>();
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

	public int getHistorical() {
		return historical;
	}

	public void setHistorical(int historical) {
		this.historical = historical;
	}

	public int getAdministered() {
		return administered;
	}

	public void setAdministered(int administered) {
		this.administered = administered;
	}

}
