package gov.nist.healthcare.iz.darq.digest.domain;

public class SummaryCounts {
	
	public int totalReadVaccinations;
	public int totalReadPatientRecords;
	public int totalSkippedPatientRecords;
	public int totalSkippedVaccinationRecords;
	public int maxVaccinationsPerRecord;
	public int minVaccinationsPerRecord;
	public int numberOfProviders;
	public int avgVaccinationsPerRecord;
	public int maxVaccinationsPerProvider;
	public int minVaccinationsPerProvider;
	public int avgVaccinationsPerProvider;
	
	
	public SummaryCounts() {
		super();
	}


	public SummaryCounts(int totalReadVaccinations, int totalReadPatientRecords, int totalSkippedPatientRecords,
						 int totalSkippedVaccinationRecords,
			int maxVaccinationsPerRecord, int minVaccinationsPerRecord, int numberOfProviders,
			int avgVaccinationsPerRecord, int maxVaccinationsPerProvider, int minVaccinationsPerProvider,
			int avgVaccinationsPerProvider) {
		super();
		this.totalReadVaccinations = totalReadVaccinations;
		this.totalReadPatientRecords = totalReadPatientRecords;
		this.totalSkippedPatientRecords = totalSkippedPatientRecords;
		this.totalSkippedVaccinationRecords = totalSkippedVaccinationRecords;
		this.maxVaccinationsPerRecord = maxVaccinationsPerRecord;
		this.minVaccinationsPerRecord = minVaccinationsPerRecord;
		this.numberOfProviders = numberOfProviders;
		this.avgVaccinationsPerRecord = avgVaccinationsPerRecord;
		this.maxVaccinationsPerProvider = maxVaccinationsPerProvider;
		this.minVaccinationsPerProvider = minVaccinationsPerProvider;
		this.avgVaccinationsPerProvider = avgVaccinationsPerProvider;
	}
	
	
	

}
