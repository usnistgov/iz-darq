package gov.nist.healthcare.iz.darq.digest.service.impl;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.configuration.validation.ConfigurationPayloadValidator;
import gov.nist.healthcare.iz.darq.detections.DetectionContext;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.digest.service.ConfigurationProvider;
import gov.nist.healthcare.iz.darq.digest.service.DigestRunner;
import gov.nist.healthcare.iz.darq.adf.service.MergeService;
import gov.nist.healthcare.iz.darq.digest.service.detection.SimpleDetectionContext;
import gov.nist.healthcare.iz.darq.digest.service.exception.InvalidPatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.AggregateParsedRecord;
import gov.nist.healthcare.iz.darq.parser.service.model.ParseError;

import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
import gov.nist.healthcare.iz.darq.preprocess.PreProcessRecord;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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
	@Autowired
	DetectionEngine detectionEngine;

	private LucenePatientRecordIterator iterator;
	private int size = 0;

	@Override
	public void digest(ConfigurationPayload configuration, String patient, String vaccines, DqDateFormat dateFormat, ADFWriter writer, Path output, Path temporaryDirectory) throws Exception {
		logger.info("[PREPROCESS] Validating Configuration");
		configurationPayloadValidator.validateConfigurationPayload(configuration);
		logger.info("[START] Processing Extract");
		Path patientsFilePath = Paths.get(patient);
		size = (int) Files.lines(patientsFilePath).parallel().count();
		logger.info("Number of patient records : "+size);

		ConfigurationProvider config = new SimpleConfigurationProvider(configuration);

		iterator = new LucenePatientRecordIterator(patientsFilePath, Paths.get(vaccines), temporaryDirectory, dateFormat);

		LocalDate date = new LocalDate(configuration.getAsOfDate());
		SimpleDetectionContext context = new SimpleDetectionContext(
			config.ageGroupService(),
				config.detectionFilter(),
				config.vaxGroupMapper(),
				date,
				DateTimeFormat.forPattern(dateFormat.getPattern())
		);

		iterator.getSanityCheckErrors().forEach(formatIssue -> writer.addIssue(
				"[ LINE : "+ formatIssue.getLine()+" ][ RECORD TYPE : VACCINATION ] " + formatIssue.getMessage()
		));

		while(iterator.hasNext()) {
			try {

				AggregateParsedRecord parsed = iterator.next();

				if(parsed.isValid()) {
					logger.info("[VALID RECORD][PROCESSING RECORD] processing valid record");

					try {
						PreProcessRecord record = preProcessRecord(parsed.getApr(), context);
						ADChunk chunk = chewer.munch(record, date, context);
						writer.write(chunk);
						writer.write_patient_age_group(record.getPatientAgeGroup(), 1);
					}
					catch (Exception e) {
						logger.error("[RECORD PROCESSING ISSUE][ ID "+parsed.getPatient().getID()+"]", e);
						e.printStackTrace();
						writer.getCounts().addUnreadPatients(1);
						writer.getCounts().addUnreadVaccinations(parsed.getVaccinations().size());
						writer.addIssue(new ParseError(parsed.getPatient().getID(), "", "", "Encountered critical issue while processing record : "+e.getMessage()+" see logs for stacktrace", true, parsed.getPatient().getLine()).toString());
					}

				} else {
					logger.info("[INVALID RECORD] record is invalid (contains one or more critical issues see summary)");
				}

				writer.getCounts().addUnreadPatients(parsed.getSkippedPatient());
				writer.getCounts().addUnreadVaccinations(parsed.getSkippedVaccination());
				writer.addIssues(parsed.getIssues().stream().map(ParseError::toString).collect(Collectors.toList()));

			}
			catch(InvalidPatientRecord e) {
				logger.info("[INVALID RECORD] Record can't be processed (record ID not populated)");
				writer.getCounts().addUnreadPatients(1);
				writer.addIssues(e.getIssues().stream().map(ParseError::toString).collect(Collectors.toList()));
				writer.addIssue("[WARNING] A patient record was not parsed or ID not found which means that the according vaccinations were not read and not taken into account in summary count skipped vaccinations");
			}
			catch (Exception e) {
				logger.error("[UNEXPECTED ISSUE]", e);
				writer.addIssue("[ERROR] Unexpected error while processing record, view logs for more information. message : " + e.getMessage());
			}
		}

		logger.info("[END] Closing streams and deleting temp directory");

		try {
			this.detectionEngine.close();
		} catch (Exception e) {
			logger.error("[END][DETECTION PROVIDER CLOSING ERROR]", e);
		}

		try {
			iterator.close();
		} catch (Exception e) {
			logger.error("[END][ITERATOR CLOSING ERROR]", e);
		}
	}

	PreProcessRecord preProcessRecord(AggregatePatientRecord apr, DetectionContext detectionContext) {
		String patientAgeGroup = detectionContext.calculateAgeGroupAsOfEvaluationDate(apr.patient.date_of_birth.getValue());
		Map<String, String> providersByVaccinationId = apr.history.stream().collect(Collectors.toMap((vx) -> vx.vax_event_id.getValue(), (vx) -> vx.reporting_group.getValue()));
		Map<String, String> ageGroupAtVaccinationByVaccinationId = apr.history.stream().collect(Collectors.toMap((vx) -> vx.vax_event_id.getValue(), (vx) -> detectionContext.calculateAgeGroup(apr.patient.date_of_birth.getValue(), vx.administration_date.getValue())));
		return new PreProcessRecord(apr, patientAgeGroup, providersByVaccinationId, ageGroupAtVaccinationByVaccinationId);
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
