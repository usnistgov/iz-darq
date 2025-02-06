package gov.nist.healthcare.iz.darq.digest.service.detection.provider.vaccinationduplicate;

import gov.nist.healthcare.iz.darq.detections.*;
import gov.nist.healthcare.iz.darq.detections.codes.VaccinationDuplicateDetection;
import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.immregistries.mqe.vxu.VxuObject;
import org.immregistries.vaccination_deduplication.Immunization;
import org.immregistries.vaccination_deduplication.computation_classes.Deterministic;
import org.immregistries.vaccination_deduplication.reference.ComparisonResult;
import org.immregistries.vaccination_deduplication.reference.ImmunizationSource;

import java.text.ParseException;
import java.util.*;

public class VaccinationDuplicateDetectionProvider implements DetectionProvider {
	Deterministic comparer = new Deterministic();
	private final Set<DetectionDescriptor> descriptors = new HashSet<>();
	private final static String VD0001 = VaccinationDuplicateDetection.VD0001.name();

	public VaccinationDuplicateDetectionProvider() {
		descriptors.add(
				new DetectionDescriptor(
						VaccinationDuplicateDetection.VD0001.name(),
						VaccinationDuplicateDetection.VD0001.getMessage(),
						VxuObject.VACCINATION.name(),
						true
				)
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
	public void process(
			PreProcessRecord record,
			DetectionContext context,
			RecordDetectionEngineResult detections
	) throws Exception {
		List<Immunization> immunizations = new ArrayList<>();
		for(VaccineRecord vaccination: record.getRecord().history) {
			String vaccinationEventId = vaccination.vax_event_id.getValue();
			Immunization immunization = transformImmunization(vaccination);
			boolean duplicate = isDuplicate(immunization, immunizations);
			Map<String, DetectionSum> vaccinationDetections = detections.getVaccinationDetectionsById().computeIfAbsent(
					vaccinationEventId,
					(key) -> new HashMap<>()
			);
			addDetectionResult(
					VD0001,
					duplicate,
					vaccinationDetections
			);
			immunizations.add(immunization);
		}
	}

	private boolean isDuplicate(Immunization immunization, List<Immunization> immunizations) {
		for(Immunization candidate: immunizations) {
			ComparisonResult comparison = comparer.compare(immunization, candidate);
			if (comparison.equals(ComparisonResult.EQUAL) || comparison.equals(ComparisonResult.UNSURE)) {
				return true;
			}
		}
		return false;
	}

	private Immunization transformImmunization(VaccineRecord record) throws ParseException {
		Immunization immunization = new Immunization();
		immunization.setImmunizationID(record.vax_event_id.getValueIfExists());
		immunization.setMVX(record.manufacturer.getValueIfExists());
		immunization.setCVX(record.vaccine_type_cvx.getValueIfExists());
		immunization.setDate(record.administration_date.getValueIfExists());
		immunization.setLotNumber(record.lot_number.getValueIfExists());
		immunization.setOrganisationID(record.reporting_group.getValueIfExists());
		String event = record.event_information_source.getValueIfExists();
		if(event.equals("00")) {
			immunization.setSource(ImmunizationSource.SOURCE);
		} else {
			immunization.setSource(ImmunizationSource.HISTORICAL);
		}
		return immunization;
	}

	private void addDetectionResult(String code, boolean found, Map<String, DetectionSum> detections) {
		if(!detections.containsKey(code)) {
			detections.put(code, new DetectionSum(!found ? 1 : 0, found ? 1 : 0));
		} else {
			DetectionSum sum = detections.get(code);
			sum.addPositive(!found ? 1 : 0);
			sum.addNegative(found ? 1 : 0);
		}
	}

	@Override
	public void configure(DetectionEngineConfiguration configuration, List<DetectionProvider> before) throws Exception {

	}

	@Override
	public void close() throws Exception {

	}
}
