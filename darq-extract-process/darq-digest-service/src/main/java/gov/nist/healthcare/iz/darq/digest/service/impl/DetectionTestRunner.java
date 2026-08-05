package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.configuration.validation.ConfigurationPayloadValidator;
import gov.nist.healthcare.iz.darq.detections.AvailableDetectionEngines;
import gov.nist.healthcare.iz.darq.detections.DetectionContext;
import gov.nist.healthcare.iz.darq.detections.DetectionDescriptor;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.detections.DetectionEngineConfiguration;
import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.expression.ComplexDetection;
import gov.nist.healthcare.iz.darq.digest.service.ConfigurationProvider;
import gov.nist.healthcare.iz.darq.digest.service.detection.SimpleDetectionContext;
import gov.nist.healthcare.iz.darq.digest.service.exception.InvalidPatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.AggregateParsedRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.ParseError;
import gov.nist.healthcare.iz.darq.parser.service.model.ParsedRecord;
import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DetectionTestRunner {

	private static final String VACCINATION_TARGET = "VACCINATION";
	private static final String PATIENT_TARGET = "PATIENT";

	@Autowired
	ConfigurationPayloadValidator configurationPayloadValidator;
	@Autowired
	DetectionEngine detectionEngine;

	public DetectionTestRunner() {
	}

	public DetectionTestRunner(ConfigurationPayloadValidator configurationPayloadValidator, DetectionEngine detectionEngine) {
		this.configurationPayloadValidator = configurationPayloadValidator;
		this.detectionEngine = detectionEngine;
	}

	public DetectionTestResult run(
			ConfigurationPayload configuration,
			List<String> selectedDetections,
			String patient,
			String vaccinations,
			DqDateFormat dateFormat,
			Path outputDirectory,
			Path temporaryDirectory
	) throws Exception {
		if(selectedDetections == null || selectedDetections.isEmpty()) {
			throw new IllegalArgumentException("At least one detection must be selected");
		}

		Files.createDirectories(outputDirectory);
		configureDetectionEngine(configuration, selectedDetections, outputDirectory, temporaryDirectory);
		configurationPayloadValidator.validateConfigurationPayload(configuration, true);

		Path details = outputDirectory.resolve("detection-test-results.tsv");
		DetectionTestResult result = new DetectionTestResult(outputDirectory, details);
		Map<String, String> targets = getTargets(configuration, selectedDetections);

		ConfigurationProvider config = new SimpleConfigurationProvider(configuration);
		LocalDate evaluationDate = new LocalDate(configuration.getAsOfDate());
		SimpleDetectionContext context = new SimpleDetectionContext(
				config.ageGroupService(),
				config.detectionFilter(),
				config.vaxGroupMapper(),
				evaluationDate,
				DateTimeFormat.forPattern(dateFormat.getPattern())
		);

		LucenePatientRecordIterator iterator = null;
		try(BufferedWriter writer = Files.newBufferedWriter(details, StandardCharsets.UTF_8)) {
			iterator = new LucenePatientRecordIterator(Paths.get(patient), Paths.get(vaccinations), temporaryDirectory, dateFormat);
			writer.write(DetectionTestResultRow.header());
			writer.newLine();

			while(iterator.hasNext()) {
				try {
					AggregateParsedRecord parsed = iterator.next();
					if(parsed.isValid()) {
						processRecord(parsed, context, selectedDetections, targets, writer, result);
					} else {
						processInvalidRecord(parsed, selectedDetections, targets, writer, result);
					}
				} catch(InvalidPatientRecord ignored) {
					// Records without a patient ID cannot be matched to vaccinations and are not evaluated.
				}
			}
		} finally {
			if(iterator != null) {
				iterator.close();
			}
			detectionEngine.close();
		}

		return result;
	}

	private void configureDetectionEngine(ConfigurationPayload configuration, List<String> selectedDetections, Path outputDirectory, Path temporaryDirectory) throws Exception {
		filterConfigurationDetections(configuration, selectedDetections);

		DetectionEngineConfiguration detectionEngineConfiguration = new DetectionEngineConfiguration();
		detectionEngineConfiguration.setOutputDirectory(outputDirectory.toAbsolutePath().toString());
		detectionEngineConfiguration.setTemporaryDirectory(temporaryDirectory.toAbsolutePath().toString());
		detectionEngineConfiguration.setConfigurationPayload(configuration);
		detectionEngineConfiguration.addActiveProvider(AvailableDetectionEngines.DP_ID_MQE);
		if(configuration.isActivatePatientMatching()) {
			detectionEngineConfiguration.addActiveProvider(AvailableDetectionEngines.DP_ID_PM);
		}
		detectionEngineConfiguration.addActiveProvider(AvailableDetectionEngines.DP_ID_VD);
		if(!configuration.getComplexDetections().isEmpty()) {
			detectionEngineConfiguration.addActiveProvider(AvailableDetectionEngines.DP_ID_COMPLEX_DETECTIONS);
		}

		detectionEngine.configure(detectionEngineConfiguration);

		Set<String> active = detectionEngine.getActiveDetectionCodes();
		List<String> inactive = selectedDetections.stream()
				.filter(detection -> !active.contains(detection))
				.collect(Collectors.toList());
		if(!inactive.isEmpty()) {
			throw new IllegalArgumentException("Detection is not active or not available in this CLI build: " + String.join(", ", inactive));
		}
	}

	private void filterConfigurationDetections(ConfigurationPayload configuration, List<String> selectedDetections) {
		Set<String> selected = new HashSet<>(selectedDetections);
		Set<String> complexCodes = configuration.getComplexDetections()
				.stream()
				.map(ComplexDetection::getCode)
				.collect(Collectors.toSet());
		List<ComplexDetection> selectedComplex = configuration.getComplexDetections()
				.stream()
				.filter(detection -> selected.contains(detection.getCode()))
				.collect(Collectors.toList());
		List<String> selectedBase = selectedDetections
				.stream()
				.filter(detection -> !complexCodes.contains(detection))
				.collect(Collectors.toList());

		configuration.setDetections(selectedBase);
		configuration.setComplexDetections(selectedComplex);
	}

	private void processRecord(
			AggregateParsedRecord parsed,
			DetectionContext context,
			List<String> selectedDetections,
			Map<String, String> targets,
			BufferedWriter writer,
			DetectionTestResult result
	) throws Exception {
		PreProcessRecord record = preProcessRecord(parsed.getApr(), context);
		RecordDetectionEngineResult detections = detectionEngine.process(record, context);
		Map<String, Integer> vaccinationLines = getVaccinationLines(parsed);

		for(String detection : selectedDetections) {
			String target = targets.get(detection);
			if(VACCINATION_TARGET.equals(target)) {
				for(VaccineRecord vaccination : parsed.getApr().history) {
					DetectionTestResultRow row = vaccinationRow(parsed, vaccination, vaccinationLines, detection, detections);
					write(row, writer, result);
				}
			} else {
				DetectionTestResultRow row = patientRow(parsed, detection, target, detections);
				write(row, writer, result);
			}
		}
	}

	private void processInvalidRecord(
			AggregateParsedRecord parsed,
			List<String> selectedDetections,
			Map<String, String> targets,
			BufferedWriter writer,
			DetectionTestResult result
	) throws Exception {
		for(String detection : selectedDetections) {
			DetectionTestResultRow row = new DetectionTestResultRow(
					parsed.getPatient().getLine(),
					parsed.getPatient().getID(),
					"",
					detection,
					targets.get(detection),
					classifyParserIssues(detection, parsed.getIssues()),
					"",
					explainParserIssues(detection, parsed.getIssues())
			);
			write(row, writer, result);
		}
	}

	private Map<String, Integer> getVaccinationLines(AggregateParsedRecord parsed) {
		Map<String, Integer> lines = new HashMap<>();
		for(ParsedRecord<VaccineRecord> vaccination : parsed.getVaccinations()) {
			if(vaccination.getRecord() != null && vaccination.getRecord().vax_event_id != null) {
				lines.put(vaccination.getRecord().vax_event_id.getValue(), vaccination.getLine());
			}
		}
		return lines;
	}

	private DetectionTestResultRow patientRow(AggregateParsedRecord parsed, String detection, String target, RecordDetectionEngineResult detections) {
		DetectionSum sum = detections.getPatientDetections().get(detection);
		DetectionTestResultRow.Result result = classify(sum);
		return new DetectionTestResultRow(
				parsed.getPatient().getLine(),
				parsed.getPatient().getID(),
				"",
				detection,
				target,
				result,
				"",
				explain(result)
		);
	}

	private DetectionTestResultRow vaccinationRow(
			AggregateParsedRecord parsed,
			VaccineRecord vaccination,
			Map<String, Integer> vaccinationLines,
			String detection,
			RecordDetectionEngineResult detections
	) {
		String vaccinationId = vaccination.vax_event_id.getValue();
		Map<String, DetectionSum> vaccinationDetections = detections.getVaccinationDetectionsById().getOrDefault(vaccinationId, Collections.emptyMap());
		DetectionTestResultRow.Result result = classify(vaccinationDetections.get(detection));
		String observedValue = observedValue(detection, vaccination);

		return new DetectionTestResultRow(
				vaccinationLines.getOrDefault(vaccinationId, 0),
				parsed.getPatient().getID(),
				vaccinationId,
				detection,
				VACCINATION_TARGET,
				result,
				observedValue,
				explain(result)
		);
	}

	private DetectionTestResultRow.Result classify(DetectionSum sum) {
		if(sum == null || !sum.isChecked()) {
			return DetectionTestResultRow.Result.NOT_EVALUABLE;
		}
		if(sum.exists()) {
			return DetectionTestResultRow.Result.DETECTED;
		}
		return DetectionTestResultRow.Result.NOT_DETECTED;
	}

	private String explain(DetectionTestResultRow.Result result) {
		switch(result) {
			case DETECTED:
				return "Detection was found";
			case NOT_DETECTED:
				return "Detection was checked and not found";
			case NOT_EVALUABLE:
				return "Detection was not checked for this row";
			default:
				return "";
		}
	}

	private String observedValue(String detection, VaccineRecord vaccination) {
		if(detection.startsWith("ADMIN_DOSE_ENTRY_EXACTLY_") &&
				vaccination.administration_date.getValue() != null &&
				vaccination.record_creation_date.getValue() != null) {
			int days = Days.daysBetween(vaccination.administration_date.getValue(), vaccination.record_creation_date.getValue()).getDays();
			return days + " days";
		}
		return "";
	}

	private void write(DetectionTestResultRow row, BufferedWriter writer, DetectionTestResult result) throws Exception {
		writer.write(row.toTsv());
		writer.newLine();
		result.add(row.getDetectionId(), row.getResult());
	}

	private Map<String, String> getTargets(ConfigurationPayload configuration, List<String> selectedDetections) {
		Map<String, String> targets = new LinkedHashMap<>();
		Map<String, DetectionDescriptor> descriptors = new HashMap<>();
		for(DetectionDescriptor descriptor : AvailableDetectionEngines.ALL_DETECTION_DESCRIPTORS) {
			descriptors.put(descriptor.getCode(), descriptor);
		}
		for(ComplexDetection detection : configuration.getComplexDetections()) {
			descriptors.put(
					detection.getCode(),
					new DetectionDescriptor(detection.getCode(), detection.getDescription(), detection.getTarget().name(), true, true)
			);
		}
		for(String detection : selectedDetections) {
			DetectionDescriptor descriptor = descriptors.get(detection);
			String target = descriptor == null ? PATIENT_TARGET : descriptor.getTarget();
			targets.put(detection, isVaccinationTarget(target) ? VACCINATION_TARGET : PATIENT_TARGET);
		}
		return targets;
	}

	private boolean isVaccinationTarget(String target) {
		return target != null && target.toUpperCase().contains(VACCINATION_TARGET);
	}

	private PreProcessRecord preProcessRecord(AggregatePatientRecord apr, DetectionContext detectionContext) {
		String patientAgeGroup = detectionContext.calculateAgeGroupAsOfEvaluationDate(apr.patient.date_of_birth.getValue());
		Map<String, String> providersByVaccinationId = apr.history.stream().collect(Collectors.toMap((vx) -> vx.vax_event_id.getValue(), (vx) -> vx.reporting_group.getValue()));
		Map<String, String> ageGroupAtVaccinationByVaccinationId = apr.history.stream().collect(Collectors.toMap((vx) -> vx.vax_event_id.getValue(), (vx) -> detectionContext.calculateAgeGroup(apr.patient.date_of_birth.getValue(), vx.administration_date.getValue())));
		return new PreProcessRecord(apr, patientAgeGroup, providersByVaccinationId, ageGroupAtVaccinationByVaccinationId);
	}

	private DetectionTestResultRow.Result classifyParserIssues(String detection, List<ParseError> issues) {
		if(parserIssuesMatchDetection(detection, issues)) {
			return DetectionTestResultRow.Result.DETECTED;
		}
		return DetectionTestResultRow.Result.NOT_EVALUABLE;
	}

	private String explainParserIssues(String detection, List<ParseError> issues) {
		String parseIssues = issues.stream()
				.map(ParseError::toString)
				.collect(Collectors.joining("; "));
		if(parserIssuesMatchDetection(detection, issues)) {
			return "Detection was found from extract parse issue: " + parseIssues;
		}
		return "Record could not be evaluated due to extract parse issue: " + parseIssues;
	}

	private boolean parserIssuesMatchDetection(String detection, List<ParseError> issues) {
		return "MQE0117".equals(detection) && hasCriticalIssue(issues, "Patient Record", "Date Of Birth");
	}

	private boolean hasCriticalIssue(List<ParseError> issues, String record, String field) {
		return issues.stream().anyMatch(issue ->
				issue.isCritical() &&
						record.equals(issue.getRecord()) &&
						field.equals(issue.getField())
		);
	}

	public void validateDetections(ConfigurationPayload configuration, List<String> selectedDetections) {
		Set<String> available = new HashSet<>(AvailableDetectionEngines.ALL_DETECTION_CODES);
		configuration.getComplexDetections().forEach(detection -> available.add(detection.getCode()));
		List<String> unknown = new ArrayList<>();
		for(String detection : selectedDetections) {
			if(!available.contains(detection)) {
				unknown.add(detection);
			}
		}
		if(!unknown.isEmpty()) {
			throw new IllegalArgumentException("Unknown detection: " + String.join(", ", unknown));
		}
	}
}
