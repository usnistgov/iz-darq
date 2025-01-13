package gov.nist.healthcare.iz.darq.digest.service.detection.provider.complex;

import gov.nist.healthcare.iz.darq.detections.*;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.expression.*;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;

import java.util.*;

public class ComplexDetectionProvider implements DetectionProvider {
	private Set<ComplexDetection> detections;
	private Map<String, Set<String>> dependencies;
	private Set<DetectionDescriptor> detectionDescriptors = new HashSet<>();

	@Override
	public void configure(DetectionEngineConfiguration configuration, List<DetectionProvider> before) {
		this.detections = new HashSet<>();
		this.dependencies = new HashMap<>();
		this.detectionDescriptors = new HashSet<>();
		for(ComplexDetection detection : configuration.getConfigurationPayload().getComplexDetections()) {
			Set<String> dependenciesCodes =  detection.getExpression().getLeafDetectionCodes();
			boolean dependenciesAreActive = dependenciesCodes.stream()
			                                                 .allMatch((code) -> isDetectedUpstream(code, before));
			if(dependenciesAreActive) {
				detections.add(detection);
				dependencies.put(detection.getCode(), dependenciesCodes);
				detectionDescriptors.add(new DetectionDescriptor(
						detection.getCode(),
						detection.getDescription(),
						detection.getTarget().name(),
						true,
						true
				));
			}
		}
	}

	public boolean isDetectedUpstream(String code, List<DetectionProvider> before) {
		return before.stream()
		             .anyMatch((provider) -> provider.getDetections().stream()
		                                             .anyMatch((detection) -> detection.getCode().equals(code))
		             );
	}

	@Override
	public boolean include(DetectionEngineConfiguration configuration) {
		return !configuration.getConfigurationPayload().getComplexDetections().isEmpty();
	}

	@Override
	public Set<DetectionDescriptor> getDetections() {
		return Collections.unmodifiableSet(detectionDescriptors);
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public void process(PreProcessRecord record, DetectionContext context, RecordDetectionEngineResult detected) throws Exception {
		Map<String, DetectionSum> allRecordDetections = getRecordLevelDetections(detected);
		for(ComplexDetection detection: detections) {
			evaluate(
					detection,
					record,
					detected,
					allRecordDetections
			);
		}
	}

	private void evaluate(ComplexDetection detection, PreProcessRecord context, RecordDetectionEngineResult detections, Map<String, DetectionSum> allRecordDetections) {
		switch(detection.getTarget()) {
			case RECORD:
				evaluate(
						detection,
						detections.getPatientDetections(), // we are adding to the patient detections
						allRecordDetections // we are looking at the whole record detections
				);
				break;
			case VACCINATION:
				for(VaccineRecord vaccination : context.getRecord().history) {
					Map<String, DetectionSum> detected = detections.getVaccinationDetectionsById().get(vaccination.vax_event_id.getValue());
					if(detected == null) {
						detected = new HashMap<>();
					}
					evaluate(
							detection,
							detected, // we are adding to the vaccinations detections
							detected // we are only looking at the vaccinations detections
					);
					if(!detected.isEmpty()) {
						detections.getVaccinationDetectionsById().put(vaccination.vax_event_id.getValue(), detected);
					}
				}
				break;
		}

	}

	private void evaluate(ComplexDetection detection, Map<String, DetectionSum> accumulator, Map<String, DetectionSum> detected) {
		Set<String> dependencies = this.dependencies.get(detection.getCode());
		if(dependenciesAreChecked(dependencies, detected)) {
			boolean triggered = evaluate(detection.getExpression(), accumulator);
			addDetection(detection.getCode(), triggered, accumulator);
		}
	}

	private void addDetection(String code, boolean triggered, Map<String, DetectionSum> detections) {
		if(triggered) {
			addDetectionResult(
					code,
					0,
					1,
					detections
			);
		} else {
			addDetectionResult(
					code,
					1,
					0,
					detections
			);
		}
	}

	private void addDetectionResult(String code, int positive, int negative, Map<String, DetectionSum> detections) {
		if(!detections.containsKey(code)) {
			detections.put(code, new DetectionSum(positive, negative));
		} else {
			DetectionSum sum = detections.get(code);
			sum.addPositive(positive);
			sum.addNegative(negative);
		}
	}

	private boolean evaluate(Expression expression, Map<String, DetectionSum> detections) {
		if(expression instanceof ANDExpression) {
			return ((ANDExpression) expression).getExpressions().stream().allMatch((operand) -> evaluate(operand, detections));
		} else if(expression instanceof ORExpression) {
			return ((ORExpression) expression).getExpressions().stream().anyMatch((operand) -> evaluate(operand, detections));
		} else if(expression instanceof XORExpression) {
			return ((XORExpression) expression).getExpressions()
			                                   .stream()
			                                   .map((operand) -> evaluate(operand, detections))
			                                   .filter((e) -> e)
			                                   .count() == 1;
		} else if(expression instanceof IMPLYExpression) {
			IMPLYExpression imply = (IMPLYExpression) expression;
			return !evaluate(imply.getLeft(), detections) || evaluate(imply.getRight(), detections);
		} else if(expression instanceof NOTExpression) {
			return !evaluate(((NOTExpression) expression).getExpression(), detections);
		} else if(expression instanceof DetectionExpression) {
			DetectionExpression detection = (DetectionExpression) expression;
			return detections.containsKey(detection.getCode()) && detections.get(detection.getCode()).exists();
		}
		return false;
	}

	private Map<String, DetectionSum> getRecordLevelDetections(RecordDetectionEngineResult detections) {
		Map<String, DetectionSum> recordLevelDetections = new HashMap<>(detections.getPatientDetections());
		for(String vaccinationId: detections.getVaccinationDetectionsById().keySet()) {
			Map<String, DetectionSum> vaccinationDetections = detections.getVaccinationDetectionsById().get(vaccinationId);
			vaccinationDetections.forEach((code, sum) -> {
				recordLevelDetections.merge(code, sum, (existing, value) -> new DetectionSum(
						existing.getPositive() + sum.getPositive(),
						existing.getNegative() + sum.getNegative()
				));
			});
		}
		return recordLevelDetections;
	}

	private boolean dependenciesAreChecked(Set<String> dependencies, Map<String, DetectionSum> detections) {
		return dependencies
				.stream()
				.allMatch(dependency -> detections.containsKey(dependency) && detections.get(dependency).isChecked());
	}
}
