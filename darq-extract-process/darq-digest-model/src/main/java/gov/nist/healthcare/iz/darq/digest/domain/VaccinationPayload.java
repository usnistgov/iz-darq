package gov.nist.healthcare.iz.darq.digest.domain;

import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;

import java.util.Map;

public class VaccinationPayload {

	private int total;
	private Map<String, DetectionSum> detection;
	private Map<String, TablePayload> codeTable;
	private Map<String, Map<String, Map<String, TablePayload>>> vaccinations;
	
	
	
	public VaccinationPayload(Map<String, DetectionSum> detection,
			Map<String, TablePayload> codeTable,
			Map<String, Map<String, Map<String, TablePayload>>> vaccinations) {
		super();
		this.detection = detection;
		this.codeTable = codeTable;
		this.vaccinations = vaccinations;
		this.total = vaccinations.keySet().stream().flatMap(
				(year) -> vaccinations.get(year).keySet().stream().flatMap(
						(gender) -> vaccinations.get(year).get(gender).keySet().stream().map(
								(event) -> vaccinations.get(year).get(gender).get(event).getTotal()
						)
				)
		).mapToInt(Integer::intValue).sum();
	}
		
	public VaccinationPayload() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Map<String, DetectionSum> getDetection() {
		return detection;
	}
	public void setDetection(Map<String, DetectionSum> detection) {
		this.detection = detection;
	}

	public Map<String, TablePayload> getCodeTable() {
		return codeTable;
	}

	public void setCodeTable(Map<String, TablePayload> codeTable) {
		this.codeTable = codeTable;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Map<String, Map<String, Map<String, TablePayload>>> getVaccinations() {
		return vaccinations;
	}

	public void setVaccinations(Map<String, Map<String, Map<String, TablePayload>>> vaccinations) {
		this.vaccinations = vaccinations;
	}
}
