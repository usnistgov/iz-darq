package gov.nist.healthcare.iz.darq.digest.service.impl;

public class DetectionTestResultRow {

	public enum Result {
		DETECTED,
		NOT_DETECTED,
		NOT_EVALUABLE
	}

	private final int sourceRow;
	private final String patientId;
	private final String vaccinationId;
	private final String detectionId;
	private final String target;
	private final Result result;
	private final String observedValue;
	private final String explanation;

	public DetectionTestResultRow(
			int sourceRow,
			String patientId,
			String vaccinationId,
			String detectionId,
			String target,
			Result result,
			String observedValue,
			String explanation
	) {
		this.sourceRow = sourceRow;
		this.patientId = patientId;
		this.vaccinationId = vaccinationId;
		this.detectionId = detectionId;
		this.target = target;
		this.result = result;
		this.observedValue = observedValue;
		this.explanation = explanation;
	}

	public static String header() {
		return "source_row\tpatient_id\tvaccination_id\tdetection_id\ttarget\tresult\tobserved_value\texplanation";
	}

	public String toTsv() {
		return sourceRow + "\t" +
				escape(patientId) + "\t" +
				escape(vaccinationId) + "\t" +
				escape(detectionId) + "\t" +
				escape(target) + "\t" +
				result.name() + "\t" +
				escape(observedValue) + "\t" +
				escape(explanation);
	}

	public Result getResult() {
		return result;
	}

	public String getDetectionId() {
		return detectionId;
	}

	private String escape(String value) {
		return value == null ? "" : value.replace('\t', ' ').replace('\r', ' ').replace('\n', ' ');
	}
}
