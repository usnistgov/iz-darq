package gov.nist.healthcare.iz.darq.digest.service.patient.matching;

import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.patient.matching.model.EndOfBlockException;
import gov.nist.healthcare.iz.darq.patient.matching.model.MatchResult;
import gov.nist.healthcare.iz.darq.patient.matching.model.PatientRecord;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientBlock;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientBlockHandler;
import gov.nist.healthcare.iz.darq.patient.matching.service.PatientMatcherService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class PatientMatchingService<T, E extends MatchResult> {

	protected final PatientMatcherService<T, E> matcher;
	protected final PatientBlockHandler<T> blockHandler;
	protected final String matcherDirectoryName = "MATCHER_INDEX";
	protected Path outputs;

	public PatientMatchingService(PatientMatcherService<T, E> matcher, PatientBlockHandler<T> blockHandler) {
		this.matcher = matcher;
		this.blockHandler = blockHandler;
	}

	public void initialize(Path temporary, Path output) throws Exception {
		this.outputs = output;
		Path tmp = Paths.get(temporary.toAbsolutePath().toString(), matcherDirectoryName).toAbsolutePath();
		tmp.toFile().mkdirs();
		this.blockHandler.initialize(tmp.toAbsolutePath().toString());
		this.onInit();
	}

	public abstract void configure(Object configuration) throws Exception;

	public void close() throws Exception {
		this.blockHandler.close();
		this.onClose();
	}

	public abstract void onInit() throws Exception;

	public abstract T transform(AggregatePatientRecord record);

	public abstract void onMatchesFound(String record, Map<String, E> matches) throws IOException;

	public abstract void onClose() throws Exception;

	public boolean process(AggregatePatientRecord patientRecord) throws Exception {
		T matcherPatientModel = transform(patientRecord);
		if (this.matcher.consider(matcherPatientModel)) {
			PatientBlock<T> candidates = blockHandler.getCandidates(matcherPatientModel);
			Map<String, E> matches = findMatches(matcherPatientModel, candidates);
			boolean matchesFound = !matches.isEmpty();
			if (matchesFound) {
				this.onMatchesFound(patientRecord.ID, matches);
			}
			blockHandler.store(patientRecord.ID, matcherPatientModel);
			return matchesFound;
		} else {
			return false;
		}
	}

	Map<String, E> findMatches(T record, PatientBlock<T> candidates) {
		Map<String, E> matches = new HashMap<>();
		while (true) {
			try {
				PatientRecord<T> candidate = candidates.next();
				E result = this.matcher.match(record, candidate.getPatient());
				if (result.isMatch()) {
					matches.put(candidate.getId(), result);
				}
			} catch (EndOfBlockException e) {
				break;
			}
		}
		return matches;
	}

}
