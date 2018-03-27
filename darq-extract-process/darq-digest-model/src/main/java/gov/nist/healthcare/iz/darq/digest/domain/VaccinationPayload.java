package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.Map;

public class VaccinationPayload {
	
	private int countAdministred;
	private int countHistorical;
	private Map<String, DetectionSum> detection;
	private Map<String, TablePayload> codeTable;
	private Map<String, Map<String, Map<String, Map<String, Integer>>>> vaccinations;
	
	
	
	public VaccinationPayload(Map<String, DetectionSum> detection,
			Map<String, TablePayload> codeTable,
			Map<String, Map<String, Map<String, Map<String, Integer>>>> vaccinations) {
		super();
		this.detection = detection;
		this.codeTable = codeTable;
		this.vaccinations = vaccinations;
		this.countAdministred = countVx(vaccinations, "00");
		this.countHistorical = countVx(vaccinations, "01");
	}
	
	public int countVx(Map<String, Map<String, Map<String, Map<String, Integer>>>> vaccinations, String ev){
		int i = 0;
		for(String code : vaccinations.keySet()){
			for(String year : vaccinations.get(code).keySet()){
				for(String gender : vaccinations.get(code).get(year).keySet()){
					for(String event : vaccinations.get(code).get(year).get(gender).keySet()){
						if(event.equals(ev)){
							i += vaccinations.get(code).get(year).get(gender).get(event);
						}
					}
				}
			}
		}
		return i;
	}
		
	public VaccinationPayload() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getCountAdministred() {
		return countAdministred;
	}
	public void setCountAdministred(int countAdministred) {
		this.countAdministred = countAdministred;
	}
	public int getCountHistorical() {
		return countHistorical;
	}
	public void setCountHistorical(int countHistorical) {
		this.countHistorical = countHistorical;
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

	public Map<String, Map<String, Map<String, Map<String, Integer>>>> getVaccinations() {
		return vaccinations;
	}
	public void setVaccinations(Map<String, Map<String, Map<String, Map<String, Integer>>>> vaccinations) {
		this.vaccinations = vaccinations;
	}

	@Override
	public String toString() {
		return "VaccinationPayload [countAdministred=" + countAdministred + ", countHistorical=" + countHistorical
				+ ", detection=" + detection + ", codeTable=" + codeTable + ", vaccinations=" + vaccinations + "]";
	}
	
	
	
}
