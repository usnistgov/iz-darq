package gov.nist.healthcare.iz.darq.digest.service.patient.matching.mismo;

import gov.nist.healthcare.iz.darq.digest.service.patient.matching.PatientMatchingService;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.patient.matching.model.mismo.MismoMatchResult;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientBlockHandler;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientMatcherService;
import org.immregistries.mismo.match.model.Patient;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class MismoPatientMatchingService extends PatientMatchingService<Patient, MismoMatchResult> {

	private final String MATCH_FILE = "pm_matches.csv";
	private FileWriter file;

	public MismoPatientMatchingService(PatientMatcherService<Patient, MismoMatchResult> matcher, PatientBlockHandler<Patient> blockHandler) {
		super(matcher, blockHandler);
	}

	@Override
	public void onInit() throws Exception {
		file = new FileWriter(Paths.get(this.outputs.toAbsolutePath().toString(), MATCH_FILE).toFile());
	}

	@Override
	public Patient transform(AggregatePatientRecord record) {
		Patient patient = new Patient();
		patient.setNameFirst(record.patient.own_name.first.getValueIfExists());
		patient.setNameLast(record.patient.own_name.last.getValueIfExists());
		patient.setNameMiddle(record.patient.own_name.middle.getValueIfExists());
		patient.setBirthDate(record.patient.date_of_birth.getValueIfExists());
		patient.setAddressStreet1(record.patient.address.street.getValueIfExists());
		patient.setAddressState(record.patient.address.state.getValueIfExists());
		patient.setAddressCity(record.patient.address.city.getValueIfExists());
		patient.setAddressZip(record.patient.address.zip.getValueIfExists());
		patient.setPhone(record.patient.phone.getValueIfExists());
		return patient;
	}

	@Override
	public void onMatchesFound(String record, Map<String, MismoMatchResult> matches) throws IOException {
		String matchString = matches.keySet().stream().map((k) -> k + ", " + matches.get(k).getSignature()).collect(Collectors.joining(", "));
		file.write(record + ", " + matchString + "\n");
		file.flush();
	}

	@Override
	public void onClose() throws Exception {
		file.close();
	}


}
