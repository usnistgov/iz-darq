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
		Set<Detection> active =  ValidationRuleEntityLists.activeDetectionsForTargets(new HashSet<>(Arrays.asList(
				TargetType.Patient,
				TargetType.NextOfKin,
				TargetType.Vaccination
		)));
		for(Detection d : active) {
			descriptors.add(new DetectionDescriptor(d.getMqeMqeCode(), d.getDisplayText(),d.getTargetObject().toString(), active.contains(d)));
		}
	}

	@Override
	public void configure(DetectionEngineConfiguration configuration, List<DetectionProvider> before) {
		logger.info("Configuring MQE Detection Provider");
		MessageValidator.INSTANCE.configure(
				configuration.getConfigurationPayload().getAllDetectionCodes().stream()
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
	public boolean include(DetectionEngineConfiguration configuration) {
		return configuration.getConfigurationPayload()
		                    .getAllDetectionCodes()
		                    .stream()
		                    .anyMatch((code) -> descriptors.stream().anyMatch((descriptors) -> descriptors.getCode().equals(code)));
	}

	@Override
	public Set<DetectionDescriptor> getDetections() {
		return Collections.unmodifiableSet(descriptors);
	}

	@Override
	public void close() {}

	@Override
	public void process(PreProcessRecord record, DetectionContext context, RecordDetectionEngineResult detections) {
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
												report.getDetection().getMqeMqeCode(),
												false
										)
								);
					} else {
						detectionsForPatient.add(
								new PatientRecordDetection(
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
												positive.getMqeMqeCode(),
												true
										)
								);
					} else {
						detectionsForPatient.add(
								new PatientRecordDetection(
										positive.getMqeMqeCode(),
										true
								)
						);
					}
				}
			}
		}

		for(String vaccinationId: detectionsByVaccination.keySet()) {
			detectionsByVaccination
					.get(vaccinationId)
					.forEach((detection) -> {
						addVaccineDetection(detections.getVaccinationDetectionsById(), vaccinationId, detection.getCode(), detection.isPositive());
					});

		}

		detectionsForPatient
				.forEach((detection) -> addPatientDetection(detections.getPatientDetections(), detection.getCode(), detection.isPositive()));
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

	private void addVaccineDetection(Map<String, Map<String, DetectionSum>> vaccinations, String vaccinationId, String code, boolean positive) {
		if(!vaccinations.containsKey(vaccinationId)) {
			vaccinations.put(vaccinationId, new HashMap<>());
		}

		if(!vaccinations.get(vaccinationId).containsKey(code)) {
			vaccinations.get(vaccinationId).put(code, new DetectionSum());
		}

		DetectionSum sum = vaccinations.get(vaccinationId).get(code);

		if(positive) {
			sum.addPositive(1);
		} else {
			sum.addNegative(1);
		}
	}



}
