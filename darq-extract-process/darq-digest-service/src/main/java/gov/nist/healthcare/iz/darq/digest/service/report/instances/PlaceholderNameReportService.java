package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.digest.service.report.AggregateLocalReportService;
import gov.nist.healthcare.iz.darq.digest.service.report.model.AggregateRow;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
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
	public static final String FILENAME = "placeholder_names.csv";

	public PlaceholderNameReportService() {
		super(FILENAME);
	}

	@Override
	public List<String> getHeader() {
		return Arrays.asList(
				"Known Name Id",
				"Name First",
				"Name Middle",
				"Name Last",
				"Name Type",
				"Count"
		);
	}

	@Override
	public List<AggregateRow> getRows(PreProcessRecord context, RecordDetectionEngineResult detections) {
		boolean hasPlaceholderName = MQE_DETECTIONS.stream().anyMatch((detection) -> detections.getPatientDetections().containsKey(detection) && detections.getPatientDetections().get(detection).exists());
		if(hasPlaceholderName) {
			String first = context.getRecord().patient.own_name.first.getValue();
			String middle = context.getRecord().patient.own_name.middle.getValue();
			String last = context.getRecord().patient.own_name.last.getValue();
			List<KnownName> matches = getKnownName(first, middle, last);
			if(!matches.isEmpty()) {
				List<AggregateRow> rows = new ArrayList<>();
				for(KnownName knownName : matches) {
					rows.add(
							new AggregateRow(
									Collections.singletonList(
											knownName.getKnownNameId() + ""
									),
									Arrays.asList(
											knownName.getNameFirst(),
											knownName.getNameMiddle(),
											knownName.getNameLast(),
											knownName.getNameType().name()
									)
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
