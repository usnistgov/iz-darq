package gov.nist.healthcare.iz.darq.adf.writer;

import gov.nist.healthcare.iz.darq.digest.domain.*;

import java.util.List;
import java.util.Map;

public abstract class ADFWriter {
	public final int MAX_ISSUES = 50;
	public abstract void open(String temporaryDirectory) throws Exception;
	public abstract void addIssue(String issue);
	public abstract void addIssues(List<String> issues);
	public abstract ProcessingCount getCounts();
	public abstract void close() throws Exception;
	public abstract void export(String location) throws Exception;
	public abstract String getSupportedADFVersion();

	public void write(ADChunk chunk) throws Exception {
		this.write_metadata(chunk);

		for (Map.Entry<String, PatientPayload> e : chunk.getGeneralPatientPayload().entrySet()) {
			String ageGroup = e.getKey();
			PatientPayload payload = e.getValue();
			for (Map.Entry<String, DetectionSum> entry : payload.getDetection().entrySet()) {
				String detection_code = entry.getKey();
				DetectionSum sum = entry.getValue();
				this.write_p_detections(ageGroup, detection_code, sum.getPositive(), sum.getNegative());
			}
			for (Map.Entry<String, TablePayload> mapEntry : payload.getCodeTable().entrySet()) {
				String table = mapEntry.getKey();
				TablePayload codes = mapEntry.getValue();
				for (Map.Entry<String, Integer> entry : codes.getCodes().entrySet()) {
					String code = entry.getKey();
					Integer count = entry.getValue();
					this.write_p_vocab(ageGroup, table, code, count);
				}
			}
		}

		for (Map.Entry<String, Map<String, ADPayload>> entry1 : chunk.getReportingGroupPayload().entrySet()) {
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

	protected abstract void write_metadata(ADChunk chunk) throws Exception;
	protected abstract void write_p_detections(String ageGroup, String code, int p, int n) throws Exception;
	protected abstract void write_p_vocab(String ageGroup, String table, String code, int nb) throws Exception;
	protected abstract void provider_detections(String provider, String ageGroup, String code, int p, int n, boolean patient) throws Exception;
	protected abstract void provider_vocab(String provider, String ageGroup, String table, String code, int nb, boolean patient) throws Exception;
	protected abstract void write_v_events(String provider, String ageGroup, String year, String gender, String source, String code, int nb) throws Exception;
}
