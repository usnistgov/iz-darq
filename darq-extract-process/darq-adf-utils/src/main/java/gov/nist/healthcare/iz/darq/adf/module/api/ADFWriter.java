package gov.nist.healthcare.iz.darq.adf.module.api;

import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.ADFCryptoUtil;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.ProcessingCount;
import gov.nist.healthcare.iz.darq.digest.domain.ADPayload;
import gov.nist.healthcare.iz.darq.digest.domain.PatientPayload;
import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.digest.domain.*;

import java.util.List;
import java.util.Map;

public abstract class ADFWriter implements AutoCloseable {
	protected final static ADFCryptoUtil crypto = new ADFCryptoUtil();
	public final int MAX_ISSUES = 50;
	public abstract void open(String temporaryDirectory) throws Exception;
	public abstract void addIssue(String issue);
	public abstract void addIssues(List<String> issues);

	public abstract List<String> getIssues();
	public abstract Map<String, String> getProviders();
	public abstract Map<String, ExtractFraction> getExtractPercent();
	public abstract Map<String, Integer> getAgeGroupCount();
	public abstract SummaryCounts getSummaryCounts();
	public abstract ProcessingCount getCounts();

	public abstract void close() throws Exception;
	public abstract void exportAndClose(String location) throws Exception;
	public abstract ADFVersion getVersion();
	public abstract String getAsString() throws Exception;
	public abstract boolean supportsPrint();

	public void write(Map<String, PatientPayload> generalPatientPayload, Map<String, Map<String, ADPayload>> reportingGroupPayload) throws Exception {
		if(generalPatientPayload != null) {
			for (Map.Entry<String, PatientPayload> e : generalPatientPayload.entrySet()) {
				String ageGroup = e.getKey();
				PatientPayload payload = e.getValue();
				if(payload.getDetection() != null) {
					for (Map.Entry<String, DetectionSum> entry : payload.getDetection().entrySet()) {
						String detection_code = entry.getKey();
						DetectionSum sum = entry.getValue();
						this.write_p_detections(ageGroup, detection_code, sum.getPositive(), sum.getNegative());
					}
				}
				if(payload.getCodeTable() != null) {
					for (Map.Entry<String, TablePayload> mapEntry : payload.getCodeTable().entrySet()) {
						String table = mapEntry.getKey();
						TablePayload codes = mapEntry.getValue();
						if(codes.getCodes() != null) {
							for (Map.Entry<String, Integer> entry : codes.getCodes().entrySet()) {
								String code = entry.getKey();
								Integer count = entry.getValue();
								this.write_p_vocab(ageGroup, table, code, count);
							}
						}
					}
				}
			}
		}

		if(reportingGroupPayload != null) {
			for (Map.Entry<String, Map<String, ADPayload>> entry1 : reportingGroupPayload.entrySet()) {
				String reportingGroup = entry1.getKey();
				Map<String, ADPayload> adPayloadMap = entry1.getValue();
				for (Map.Entry<String, ADPayload> stringADPayloadEntry : adPayloadMap.entrySet()) {
					String ageGroup = stringADPayloadEntry.getKey();
					ADPayload payload = stringADPayloadEntry.getValue();
					if(payload.getPatientPayload() != null) {
						if(payload.getPatientPayload().getCodeTable() != null) {
							for (Map.Entry<String, TablePayload> tablePayloadEntry : payload.getPatientPayload().getCodeTable().entrySet()) {
								String table = tablePayloadEntry.getKey();
								TablePayload tablePayload1 = tablePayloadEntry.getValue();
								for (Map.Entry<String, Integer> entry : tablePayload1.getCodes().entrySet()) {
									String code = entry.getKey();
									Integer count = entry.getValue();
									this.provider_vocab(reportingGroup, ageGroup, table, code, count, true);
								}
							}
						}
						if(payload.getPatientPayload().getDetection() != null) {
							for (Map.Entry<String, DetectionSum> detectionSumEntry : payload.getPatientPayload().getDetection().entrySet()) {
								String detection_code = detectionSumEntry.getKey();
								DetectionSum v = detectionSumEntry.getValue();
								this.provider_detections(reportingGroup, ageGroup, detection_code, v.getPositive(), v.getNegative(), true);
							}
						}
					}
					if(payload.getVaccinationPayload() != null) {
						if(payload.getVaccinationPayload().getCodeTable() != null) {
							for (Map.Entry<String, TablePayload> stringTablePayloadEntry : payload.getVaccinationPayload().getCodeTable().entrySet()) {
								String table = stringTablePayloadEntry.getKey();
								TablePayload tablePayload = stringTablePayloadEntry.getValue();
								for (Map.Entry<String, Integer> entry : tablePayload.getCodes().entrySet()) {
									String code = entry.getKey();
									Integer count = entry.getValue();
									this.provider_vocab(reportingGroup, ageGroup, table, code, count, false);
								}
							}
						}
						if(payload.getVaccinationPayload().getDetection() != null) {
							for (Map.Entry<String, DetectionSum> stringDetectionSumEntry : payload.getVaccinationPayload().getDetection().entrySet()) {
								String detection_code = stringDetectionSumEntry.getKey();
								DetectionSum sum = stringDetectionSumEntry.getValue();
								this.provider_detections(reportingGroup, ageGroup, detection_code, sum.getPositive(), sum.getNegative(), false);
							}
						}
						if(payload.getVaccinationPayload().getVaccinations() != null) {
							for (Map.Entry<String, Map<String, Map<String, TablePayload>>> stringMapEntry : payload.getVaccinationPayload().getVaccinations().entrySet()) {
								String year = stringMapEntry.getKey();
								Map<String, Map<String, TablePayload>> eventsByGender = stringMapEntry.getValue();
								for (Map.Entry<String, Map<String, TablePayload>> mapEntry : eventsByGender.entrySet()) {
									String gender = mapEntry.getKey();
									Map<String, TablePayload> eventsBySource = mapEntry.getValue();
									for (Map.Entry<String, TablePayload> e : eventsBySource.entrySet()) {
										String source = e.getKey();
										TablePayload value = e.getValue();
										for (Map.Entry<String, Integer> entry : value.getCodes().entrySet()) {
											String code = entry.getKey();
											Integer count = entry.getValue();
											this.write_v_events(reportingGroup, ageGroup, year, gender, source, code, count);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void write(ADChunk chunk) throws Exception {
		this.write_chunk(chunk);
		this.write(chunk.getGeneralPatientPayload(), chunk.getReportingGroupPayload());
	}

	protected abstract void write_chunk(ADChunk chunk) throws Exception;
	public abstract void write_metadata(Metadata metadata, ConfigurationPayload payload) throws Exception;

	public abstract void setSummary(Summary summary);

	public abstract Summary getSummary();

	public abstract void write_patient_age_group(String ageGroupId, int nb);
	protected abstract void write_p_detections(String ageGroup, String code, int p, int n) throws Exception;
	protected abstract void write_p_vocab(String ageGroup, String table, String code, int nb) throws Exception;
	protected abstract void provider_detections(String provider, String ageGroup, String code, int p, int n, boolean patient) throws Exception;
	protected abstract void provider_vocab(String provider, String ageGroup, String table, String code, int nb, boolean patient) throws Exception;
	protected abstract void write_v_events(String provider, String ageGroup, String year, String gender, String source, String code, int nb) throws Exception;
}
