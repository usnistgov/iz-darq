package gov.nist.healthcare.iz.darq.digest.service.detection.provider.mqe;

import gov.nist.healthcare.iz.darq.detections.*;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.TransformResult;
import gov.nist.healthcare.iz.darq.digest.service.impl.MQETransformService;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.immregistries.mqe.util.validation.MqeDetection;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.detection.MqeCode;
import org.immregistries.mqe.validator.detection.ValidationReport;
import org.immregistries.mqe.validator.engine.MessageValidator;
import org.immregistries.mqe.validator.engine.RulePairBuilder;
import org.immregistries.mqe.validator.engine.ValidationRule;
import org.immregistries.mqe.validator.engine.ValidationRuleResult;
import org.immregistries.mqe.validator.engine.rules.ValidationRuleEntityLists;
import org.immregistries.mqe.vxu.MqeMessageReceived;
import org.immregistries.mqe.vxu.MqeVaccination;
import org.immregistries.mqe.vxu.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MQEDetectionProvider implements DetectionProvider {

	private final MessageValidator validator = MessageValidator.INSTANCE;
	private final Set<DetectionDescriptor> descriptors = new HashSet<>();
	private final MQETransformService transformer = new MQETransformService();
	private final static Logger logger = LoggerFactory.getLogger(MQEDetectionProvider.class.getName());

	public MQEDetectionProvider() {
		Set<Detection> all = new HashSet<>(Arrays.asList(Detection.values()));
		Set<Detection> active =  ValidationRuleEntityLists.activeDetectionsForTargets(new HashSet<>(Arrays.asList(
				TargetType.Patient,
				TargetType.NextOfKin,
				TargetType.Vaccination
		)));
		for(Detection d : all) {
			descriptors.add(new DetectionDescriptor(d.getMqeMqeCode(), d.getDisplayText(),d.getTargetObject().toString(), active.contains(d)));
		}
	}

	@Override
	public void configure(DetectionEngineConfiguration configuration) {
		logger.info("Configuring MQE Validator");
		MessageValidator.INSTANCE.configure(
				configuration.getConfigurationPayload().getDetections().stream()
						.map((code) -> {
							try {
								return MqeCode.valueOf(code);
							} catch (Exception e) {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toSet())
		);
		logger.info("MQE Validator Configured");
		Set<ValidationRule> rules = RulePairBuilder.INSTANCE.getActiveValidationRules().getRules();
		logger.info("MQE Active Rules (" + rules.size() + ") :");
		rules.forEach(
				(r) -> logger.info("* Active Rules : " + r.getClass())
		);
	}

	@Override
	public boolean hasSideEffect() {
		return false;
	}

	@Override
	public Set<String> getEnabledDetectionCodes() {
		return ValidationRuleEntityLists.activeDetectionsForTargets(new HashSet<>(Arrays.asList(
				TargetType.Patient,
				TargetType.NextOfKin,
				TargetType.Vaccination
		))).stream().map(Detection::getMqeMqeCode).collect(Collectors.toSet());
	}

	@Override
	public Set<DetectionDescriptor> provides() {
		return Collections.unmodifiableSet(descriptors);
	}

	@Override
	public void close() {}

	@Override
	public AggregatedRecordDetections processRecordAndGetDetections(PreProcessRecord record, DetectionContext context) {
		AggregatedRecordDetections detections = new AggregatedRecordDetections();
		// Provider, Age Group, DetectionCode
		Map<String, Map<String, Map<String, DetectionSum>>> vaccinations = new HashMap<>();
		// Age Group, DetectionCode
		Map<String, DetectionSum> patient = new HashMap<>();
		detections.setPatient(patient);
		detections.setVaccinations(vaccinations);


		// Transform APR
		TransformResult<VaccineRecord, MqeVaccination, MqeMessageReceived> transformed = this.transformer.transform(record.getRecord());
		MqeMessageReceived msg = transformed.getPayload();

		// Prepare Vaccine Id Map
		Map<Integer, String> vaccinationIdByPositionId = new HashMap<>();
		for(MqeVaccination vaccination: msg.getVaccinations()) {
			vaccinationIdByPositionId.put(
					vaccination.getPositionId(),
					transformed.getOriginalFromTargetVaccinationEvent(String.valueOf(vaccination.getPositionId())).vax_event_id.getValue()
			);
		}

		// Call MQE Validator
		List<ValidationRuleResult> results = this.validator.validateMessageNIST(msg);
		Map<String, Set<VaccineDetection>> detectionsByVaccination = new HashMap<>();
		Set<PatientRecordDetection> detectionsForPatient = new HashSet<>();

		// Process MQE Rule Results
		for(ValidationRuleResult r : results) {

			// Rule target
			boolean ruleTargetIsVaccination = r.getTargetType() == TargetType.Vaccination;

			Integer positionId = r.getPositionId();
			String vaccineId = ruleTargetIsVaccination ? vaccinationIdByPositionId.get(positionId) : "";
			String ageGroup = ruleTargetIsVaccination ? record.getAgeGroupAtVaccinationByVaccinationId().get(vaccineId) : "";
			String provider = ruleTargetIsVaccination ? record.getProvidersByVaccinationId().get(vaccineId) : "";

			// Get Possible Detections for Rule
			Set<MqeDetection> possible = r.getPossible().stream().filter(Objects::nonNull).collect(Collectors.toSet());

			for (ValidationReport report : r.getIssues()) {

				// If detection is part of the configuration
				if (context.keepDetection(report.getDetection().getMqeMqeCode())) {
					possible.remove(report.getDetection());

					if (ruleTargetIsVaccination) {
						detectionsByVaccination.computeIfAbsent(vaccineId, (k) -> new HashSet<>());
						detectionsByVaccination
								.get(vaccineId)
								.add(
										new VaccineDetection(
												provider,
												ageGroup,
												report.getDetection().getMqeMqeCode(),
												false
										)
								);
					} else {
						detectionsForPatient.add(
								new PatientRecordDetection(
										ageGroup,
										report.getDetection().getMqeMqeCode(),
										false
								)
						);
					}
				}

			}

			for (MqeDetection positive : possible) {
				if (context.keepDetection(positive.getMqeMqeCode())) {
					if (ruleTargetIsVaccination) {
						detectionsByVaccination.computeIfAbsent(vaccineId, (k) -> new HashSet<>());
						detectionsByVaccination
								.get(vaccineId)
								.add(
										new VaccineDetection(
												provider,
												ageGroup,
												positive.getMqeMqeCode(),
												true
										)
								);
					} else {
						detectionsForPatient.add(
								new PatientRecordDetection(
										ageGroup,
										positive.getMqeMqeCode(),
										true
								)
						);
					}
				}
			}
		}

		detectionsByVaccination.values().stream()
				.flatMap(Set::stream)
				.forEach((detection) -> addVaccineDetection(vaccinations, detection.getAgeGroup(), detection.getReportingGroup(), detection.getCode(), detection.isPositive()));

		detectionsForPatient
				.forEach((detection) -> addPatientDetection(patient, detection.getCode(), detection.isPositive()));

		return detections;
	}

	private void addPatientDetection(Map<String, DetectionSum> patient, String code, boolean positive) {
		if(!patient.containsKey(code)) {
			patient.put(code, new DetectionSum());
		}

		DetectionSum sum = patient.get(code);

		if(positive) {
			sum.addPositive(1);
		} else {
			sum.addNegative(1);
		}
	}

	private void addVaccineDetection(Map<String, Map<String, Map<String, DetectionSum>>> vaccinations, String ageGroup, String provider, String code, boolean positive) {
		if(!vaccinations.containsKey(provider)) {
			vaccinations.put(provider, new HashMap<>());
		}

		if(!vaccinations.get(provider).containsKey(ageGroup)) {
			vaccinations.get(provider).put(ageGroup, new HashMap<>());
		}

		if(!vaccinations.get(provider).get(ageGroup).containsKey(code)) {
			vaccinations.get(provider).get(ageGroup).put(code, new DetectionSum());
		}

		DetectionSum sum = vaccinations.get(provider).get(ageGroup).get(code);

		if(positive) {
			sum.addPositive(1);
		} else {
			sum.addNegative(1);
		}
	}



}
