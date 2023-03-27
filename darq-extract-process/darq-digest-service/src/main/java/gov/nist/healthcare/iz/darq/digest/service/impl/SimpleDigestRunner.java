package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.configuration.validation.ConfigurationPayloadValidator;
import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.digest.service.ConfigurationProvider;
import gov.nist.healthcare.iz.darq.digest.service.DigestRunner;
import gov.nist.healthcare.iz.darq.adf.service.MergeService;
import gov.nist.healthcare.iz.darq.digest.service.exception.InvalidPatientRecord;
import gov.nist.healthcare.iz.darq.digest.service.patient.matching.PatientMatchingService;
import gov.nist.healthcare.iz.darq.parser.service.model.AggregateParsedRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.ParseError;

import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public Path createTemporaryDirectory(Optional<String> directory) throws FileNotFoundException {
	  logger.info("Creating Temporary directory");
	  if(directory.isPresent()) {
		logger.info("Directory location provided");
		File location = new File(directory.get());
		if(!location.exists()) {
		  logger.error("[TMP DIRECTORY] provided location'" + directory.get() + "' does not exist");
		  throw new FileNotFoundException("provided location'" + directory.get() + "' does not exist");
		}

		if(!location.isDirectory()) {
		  logger.error("[TMP DIRECTORY] provided location'" + directory.get() + "' is not directory");
		  throw new FileNotFoundException("provided location'" + directory.get() + "' is not directory");
		}
	  }

	  Path tmpDir = this.createDirectory(directory);
	  logger.info("Directory created at " + tmpDir);
	  return tmpDir;
	}

	private Path createDirectory(Optional<String> location) {
	  String name = RandomStringUtils.random(10, true, true);
	  Path path = location.map(s -> Paths.get(s, name)).orElseGet(() -> Paths.get(name));
	  path.toFile().mkdir();
	  return path.toAbsolutePath();
	}

	@Override
	public ADChunk digest(ConfigurationPayload configuration, String patient, String vaccines, DqDateFormat dateFormat, PatientMatchingService matchingService, Path output, Optional<String> directory) throws Exception {
		logger.info("[PREPROCESS] Validating Configuration");
		configurationPayloadValidator.validateConfigurationPayload(configuration);
		logger.info("[START] Processing Extract");
		Path patientsFilePath = Paths.get(patient);
		size = (int) Files.lines(patientsFilePath).parallel().count();
		logger.info("Number of patient records : "+size);

		ConfigurationProvider config = new SimpleConfigurationProvider(configuration);
		Path temporaryDirectory = this.createTemporaryDirectory(directory);
		iterator = new LucenePatientRecordIterator(patientsFilePath, Paths.get(vaccines), temporaryDirectory, dateFormat);

		if(matchingService != null) {
		  logger.info("Initializing the patient matching service");
		  matchingService.initialize(temporaryDirectory, output);
		}

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
						ADChunk chunk = chewer.munch(config, parsed.getApr(), date, matchingService);
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
				file.addIssue("[ERROR] Unexpected error while processing record, view logs for more information. message : " + e.getMessage());
			}
		}

		logger.info("[END] Closing streams and deleting temp directory");

		try {
			if(matchingService != null) {
				logger.info("Closing patient matching service resources");
				matchingService.close();
			}
		} catch (Exception e) {
			logger.error("[END][PATIENT MATCHING CLOSING ERROR]", e);
		}


		try {
			iterator.close();
		  	FileUtils.deleteDirectory(temporaryDirectory.toFile());
		} catch (Exception e) {
			logger.error("[END][ITERATOR CLOSING ERROR]", e);
			System.err.println("ALERT: Unable to delete temporary directory " + temporaryDirectory);
			System.err.println("Due to - " + e.getMessage());
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
