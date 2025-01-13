package gov.nist.healthcare.iz.darq.digest.service.report.instances;

import gov.nist.healthcare.iz.darq.detections.RecordDetectionEngineResult;
import gov.nist.healthcare.iz.darq.localreport.AggregateLocalReportService;
import gov.nist.healthcare.iz.darq.localreport.AggregateRow;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.apache.commons.lang3.StringUtils;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.engine.codes.KnowNameList;
import org.immregistries.mqe.validator.engine.codes.KnownName;

import java.util.*;
import java.util.stream.Collectors;

public class PlaceholderNameReportService extends AggregateLocalReportService {
	private static class Name {
		String first;
		String last;
		String middle;
		Set<KnownName.NameType> types = new HashSet<>();

		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(o == null || getClass() != o.getClass()) {
				return false;
			}
			Name name = (Name) o;
			return Objects.equals(first, name.first) && Objects.equals(last, name.last) && Objects.equals(
					middle,
					name.middle
			);
		}

		@Override
		public int hashCode() {
			return Objects.hash(first, last, middle);
		}
	}

	private final List<KnownName> knownNames = KnowNameList.INSTANCE.getNames();
	private static final Set<String> MQE_DETECTIONS = new HashSet<>(Arrays.asList(
			Detection.PatientNameHasJunkName.getMqeMqeCode(),
			Detection.PatientNameMayBeTestName.getMqeMqeCode(),
			Detection.PatientNameMayBeTemporaryNewbornName.getMqeMqeCode()
	));
	public static final String FILENAME = "placeholder_names.csv";

	public PlaceholderNameReportService() {
		super(FILENAME, MQE_DETECTIONS.toArray(new String[0]));
	}

	@Override
	public List<String> getHeader() {
		return Arrays.asList(
				"Name First",
				"Name Middle",
				"Name Last",
				"Name Types",
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
			Set<Name> matches = getKnownName(first, middle, last);
			if(!matches.isEmpty()) {
				List<AggregateRow> rows = new ArrayList<>();
				for(Name knownName : matches) {
					rows.add(
							new AggregateRow(
									Arrays.asList(
											knownName.first,
											knownName.middle,
											knownName.last
									),
									Collections.singletonList(
											knownName.types.stream()
											               .map(KnownName.NameType::toString)
											               .sorted()
											               .collect(
																   Collectors.joining("\t")
											               )
									)
							)
					);
				}
				return rows;
			}
		}
		return null;
	}

	private Set<Name> getKnownName(String first, String middle, String last) {
		Map<Integer, Name> matches = new HashMap<>();
		for(KnownName name : knownNames) {
			boolean shouldMatchFirstName = !StringUtils.isBlank(name.getNameFirst());
			boolean shouldMatchLastName = !StringUtils.isBlank(name.getNameLast());
			boolean shouldMatchMiddleName = !StringUtils.isBlank(name.getNameMiddle());

			if(shouldMatchFirstName && !(!StringUtils.isBlank(first) && name.getNameFirst().equalsIgnoreCase(first))) {
				continue;
			}

			if(shouldMatchLastName && !(!StringUtils.isBlank(last) && name.getNameLast().equalsIgnoreCase(last))) {
				continue;
			}

			if(shouldMatchMiddleName && !(!StringUtils.isBlank(middle) && name.getNameMiddle().equalsIgnoreCase(middle))) {
				continue;
			}

			Integer key = Objects.hash(name.getNameFirst(), name.getNameLast(), name.getNameMiddle());
			matches.compute(key, (k, v) -> {
				if(v == null) {
					v = new Name();
					v.first = name.getNameFirst();
					v.last = name.getNameLast();
					v.middle = name.getNameMiddle();
				}
				v.types.add(name.getNameType());
				return v;
			});
		}
		return new HashSet<>(matches.values());
	}
}
