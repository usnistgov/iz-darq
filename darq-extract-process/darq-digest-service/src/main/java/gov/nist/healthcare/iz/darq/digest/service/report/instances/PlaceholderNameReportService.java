package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.AggregatedRecordDetections;
import gov.nist.healthcare.iz.darq.digest.service.report.AggregateLocalReportService;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import org.apache.commons.lang3.StringUtils;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.engine.codes.KnowNameList;
import org.immregistries.mqe.validator.engine.codes.KnownName;

import java.util.*;

public class PlaceholderNameReportService extends AggregateLocalReportService {

	private final List<KnownName> knownNames = KnowNameList.INSTANCE.getNames();
	private final Set<String> MQE_DETECTIONS = new HashSet<>(Arrays.asList(
			Detection.PatientNameHasJunkName.getMqeMqeCode(),
			Detection.PatientNameMayBeTestName.getMqeMqeCode(),
			Detection.PatientNameMayBeTemporaryNewbornName.getMqeMqeCode(),
			Detection.PatientNameMiddleIsInvalid.getMqeMqeCode(),
			Detection.PatientNameLastIsInvalid.getMqeMqeCode(),
			Detection.PatientNameFirstIsInvalid.getMqeMqeCode()
	));

	public PlaceholderNameReportService() {
		super("placeholder_names.csv");
	}

	@Override
	public List<String> getHeader() {
		return Arrays.asList(
				"Name First",
				"Name Middle",
				"Name Last",
				"Name Type",
				"Count"
		);
	}

	@Override
	public List<List<String>> getRows(AggregatePatientRecord patientRecord, AggregatedRecordDetections detections) {
		boolean hasPlaceholderName = MQE_DETECTIONS.stream().anyMatch((detection) -> detections.getPatient().containsKey(detection) && detections.getPatient().get(detection).getPositive() > 0);
		if(hasPlaceholderName) {
			String first = patientRecord.patient.own_name.first.getValue();
			String middle = patientRecord.patient.own_name.middle.getValue();
			String last = patientRecord.patient.own_name.last.getValue();
			List<KnownName> matches = getKnownName(first, middle, last);
			if(!matches.isEmpty()) {
				ArrayList<List<String>> rows = new ArrayList<>();
				for(KnownName knownName : matches) {
					rows.add(
							Arrays.asList(
									knownName.getNameFirst(),
									knownName.getNameMiddle(),
									knownName.getNameLast(),
									knownName.getNameType().name()
							)
					);
				}
				return rows;
			}
		}
		return null;
	}

	private List<KnownName> getKnownName(String first, String middle, String last) {
		Map<Integer, KnownName> matches = new HashMap<>();
		for(KnownName name : knownNames) {
			if (!StringUtils.isBlank(name.getNameLast()) && !StringUtils.isBlank(last)
					&& name.getNameLast().equalsIgnoreCase(last)) {
				matches.put(name.getKnownNameId(), name);
			}

			if (!StringUtils.isBlank(name.getNameFirst()) && !StringUtils.isBlank(first)
					&& name.getNameFirst().equalsIgnoreCase(first)) {
				matches.put(name.getKnownNameId(), name);
			}

			if (!StringUtils.isBlank(name.getNameMiddle()) && !StringUtils.isBlank(middle)
					&& name.getNameMiddle().equalsIgnoreCase(middle)) {
				matches.put(name.getKnownNameId(), name);
			}
		}

		return new ArrayList<>(matches.values());
	}
}
