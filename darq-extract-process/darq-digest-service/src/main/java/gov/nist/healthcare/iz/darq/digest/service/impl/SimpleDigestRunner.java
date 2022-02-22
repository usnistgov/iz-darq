package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.configuration.validation.ConfigurationPayloadValidator;
import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.digest.service.ConfigurationProvider;
import gov.nist.healthcare.iz.darq.digest.service.DigestRunner;
import gov.nist.healthcare.iz.darq.adf.service.MergeService;
import gov.nist.healthcare.iz.darq.digest.service.exception.InvalidPatientRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.AggregateParsedRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.ParseError;

import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimpleDigestRunner implements DigestRunner {

	@Autowired
	SimpleRecordChewer chewer;
	private final static Logger logger = LoggerFactory.getLogger(SimpleDigestRunner.class.getName());
	@Autowired
	MergeService merge;
	@Autowired
	ConfigurationPayloadValidator configurationPayloadValidator;
	private LucenePatientRecordIterator iterator;
	private int size = 0;
	ADChunk file;

	@Override
	public ADChunk digest(ConfigurationPayload configuration, String patient, String vaccines, DqDateFormat dateFormat, Optional<String> directory) throws Exception {
		logger.info("[PREPROCESS] Validating Configuration");
		configurationPayloadValidator.validateConfigurationPayload(configuration);
		logger.info("[START] Processing Extract");
		size = (int) Files.lines(Paths.get(patient)).parallel().count();
		logger.info("Number of patient records : "+size);

		ConfigurationProvider config = new SimpleConfigurationProvider(configuration);
		iterator = new LucenePatientRecordIterator(Paths.get(patient), Paths.get(vaccines), directory, dateFormat);
		this.file = new ADChunk();
		LocalDate date = new LocalDate(configuration.getAsOfDate());
		iterator.getSanityCheckErrors().forEach(formatIssue -> file.addIssue(
				"[ LINE : "+ formatIssue.getLine()+" ][ RECORD TYPE : VACCINATION ] " + formatIssue.getMessage()
		));
		while(iterator.hasNext()) {
			try {

				AggregateParsedRecord parsed = iterator.next();

				if(parsed.isValid()) {
					logger.info("[VALID RECORD][PROCESSING RECORD] processing valid record");

					try {
						ADChunk chunk = chewer.munch(config, parsed.getApr(), date);
						merge.mergeChunk(file, chunk);
					}
					catch (Exception e) {
						logger.error("[RECORD PROCESSING ISSUE][ ID "+parsed.getPatient().getID()+"]", e);
						file.setUnreadPatients(file.getUnreadPatients() + 1);
						file.setUnreadVaccinations(file.getUnreadVaccinations() + parsed.getVaccinations().size());
						file.addIssue(new ParseError(parsed.getPatient().getID(), "", "", "Encountered critical issue while processing record : "+e.getMessage()+" see logs for stacktrace", true, parsed.getPatient().getLine()).toString());
					}

				} else {
					logger.info("[INVALID RECORD] record is invalid (contains one or more critical issues see summary)");
				}

				file.setUnreadPatients(file.getUnreadPatients() + parsed.getSkippedPatient());
				file.setUnreadVaccinations(file.getUnreadVaccinations() + parsed.getSkippedVaccination());
				file.addIssues(parsed.getIssues().stream().map(ParseError::toString).collect(Collectors.toList()));

			}
			catch(InvalidPatientRecord e) {
				logger.info("[INVALID RECORD] Record can't be processed (record ID not populated)");
				file.setUnreadPatients(file.getUnreadPatients() + 1);
				file.addIssues(e.getIssues().stream().map(ParseError::toString).collect(Collectors.toList()));
				file.addIssue("[WARNING] A patient record was not parsed or ID not found which means that the according vaccinations were not read and not taken into account in summary count skipped vaccinations");
			}
			catch (Exception e) {
				logger.error("[UNEXPECTED ISSUE]", e);
			}
		}

		logger.info("[END] Closing streams and deleting temp directory");
		try {
			iterator.close();
		} catch (Exception e) {
			logger.error("[END][UNEXPECTED ISSUE]", e);
		}
		return file;
	}


	@Override
	public Fraction spy() {
		if(this.iterator == null){
			return new Fraction(0,0);
		}
		else {
			return new Fraction(this.iterator.progress(), size);
		}		
	}

}
