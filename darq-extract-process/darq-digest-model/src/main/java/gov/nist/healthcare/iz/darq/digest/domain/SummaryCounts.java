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
	
	
	public static SummaryCounts merge(SummaryCounts source, SummaryCounts target) {
		SummaryCounts result = new SummaryCounts();
		result.totalReadVaccinations = source.totalReadVaccinations + target.totalReadVaccinations;
		result.totalReadPatientRecords = source.totalReadPatientRecords + target.totalReadPatientRecords;
		result.totalSkippedPatientRecords = source.totalSkippedPatientRecords + target.totalSkippedPatientRecords;
		result.totalSkippedVaccinationRecords = source.totalSkippedVaccinationRecords + target.totalSkippedVaccinationRecords;
		result.maxVaccinationsPerRecord = Math.max(source.maxVaccinationsPerRecord, target.maxVaccinationsPerRecord);
		result.minVaccinationsPerRecord = Math.min(source.minVaccinationsPerRecord, target.minVaccinationsPerRecord);
		result.numberOfProviders = source.numberOfProviders + target.numberOfProviders;
		result.avgVaccinationsPerRecord = (source.avgVaccinationsPerRecord + target.avgVaccinationsPerRecord) / 2;
		result.maxVaccinationsPerProvider = Math.max(source.maxVaccinationsPerProvider, target.maxVaccinationsPerProvider);
		result.minVaccinationsPerProvider = Math.min(source.minVaccinationsPerProvider, target.minVaccinationsPerProvider);
		result.avgVaccinationsPerProvider = (source.avgVaccinationsPerProvider + target.avgVaccinationsPerProvider) / 2;
		return result;
	}

}
