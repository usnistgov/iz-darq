package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ADChunk {
	
	private int unreadPatients = 0;
	private int unreadVaccinations = 0;
	private int nbVaccinations = 0;
	private int nbPatients = 0;
	private int maxVaccination = 0;
	private int minVaccination = 99999;
	private List<String> issues;
	private Map<String, String> providers;
	private Map<String, Map<String, VaccinationPayload>> vaccinationSection;
	private Map<String, PatientPayload> patientSection;
	private Map<String, Fraction> extraction;
	private Map<Field, Set<String>> values;
	private Map<String, Set<String>> codes;
	
	public ADChunk(Map<String, String> providers, Map<String, Map<String, VaccinationPayload>> vaccinationSection,
			Map<String, PatientPayload> patientSection, Map<String, Fraction> extraction, List<String> issues, int nbVaccinations, int nbPatients, Map<Field, Set<String>> values, Map<String, Set<String>> codes) {
		super();
		this.providers = providers;
		this.vaccinationSection = vaccinationSection;
		this.patientSection = patientSection;
		this.extraction = extraction;
		this.issues = issues;
		this.nbVaccinations = nbVaccinations;
		this.nbPatients = nbPatients;
		this.values = values;
		this.codes = codes;
	}
	
	
	public ADChunk() {
		super();
		vaccinationSection = new HashMap<>();
		patientSection = new HashMap<>();
		extraction = new HashMap<>();
		providers = new HashMap<>();
		this.issues = new ArrayList<>();
	}


	public Map<String, Map<String, VaccinationPayload>> getVaccinationSection() {
		return vaccinationSection;
	}
	public void setVaccinationSection(Map<String, Map<String, VaccinationPayload>> vaccinationSection) {
		this.vaccinationSection = vaccinationSection;
	}
	public Map<String, PatientPayload> getPatientSection() {
		return patientSection;
	}
	public void setPatientSection(Map<String, PatientPayload> patientSection) {
		this.patientSection = patientSection;
	}
	public Map<String, Fraction> getExtraction() {
		return extraction;
	}
	public void setExtraction(Map<String, Fraction> extraction) {
		this.extraction = extraction;
	}
	public Map<String, String> getProviders() {
		return providers;
	}
	public void setProviders(Map<String, String> providers) {
		this.providers = providers;
	}


	public List<String> getIssues() {
		return issues;
	}


	public void setIssues(List<String> issues) {
		this.issues = issues;
	}


	@Override
	public String toString() {
		return "ADChunk [providers=" + providers + ", vaccinationSection=" + vaccinationSection + ", patientSection="
				+ patientSection + ", extraction=" + extraction + "]";
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
		this.maxVaccination = v > maxVaccination ? v : maxVaccination;
	}


	public int getMinVaccination() {
		return minVaccination;
	}


	public void setMinVaccination(int v) {
		this.minVaccination = v < minVaccination ? v : minVaccination;
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
