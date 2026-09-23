package gov.nist.healthcare.iz.darq.digest.mock;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.data.DataExtractMock;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.Record;
import org.immregistries.mqe.validator.detection.Detection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SME-supplied fixture for the mismerged-patient-candidate signals. Rather than rebuilding the
 * records with ExtractBuilder, this mock replays the two extract files exactly as delivered
 * (src/test/resources/mismerged), so the fixture stays byte-identical to what the SME validated
 * and can be refreshed by dropping in new files.
 *
 * <pre>
 * 101, 102, 103  5+ invalid / mismatched doses  - 6 doses of one vaccine, days apart, in adults
 * 201, 202, 203  4+ COVID doses in 2021         - CVX 207/208, all administered during 2021
 * 301, 302, 303  2+ flu doses in one season     - CVX 140/141/150 inside the Sep 2021-Mar 2022 season
 * 401, 402, 403  high volume immunizations      - 21 to 31 doses administered to infants
 * 501, 502, 503  controls                       - 2 well-spaced doses, below every threshold
 * </pre>
 */
public class PossibleMismergedRecordsReportMock implements DataExtractMock {
	static private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final String PATIENTS_RESOURCE = "/mismerged/patients.txt";
	private static final String VACCINATIONS_RESOURCE = "/mismerged/vaccinations.txt";
	private static final String SEPARATOR = "\t";

	/** Column 0 of both extract files is the patient ID. */
	private static final int PATIENT_ID_COLUMN = 0;

	// 5+ invalid / mismatched doses
	public final String invalidDoses1 = "101";
	public final String invalidDoses2 = "102";
	public final String invalidDoses3 = "103";
	// 4+ COVID doses in 2021
	public final String covidDoses1 = "201";
	public final String covidDoses2 = "202";
	public final String covidDoses3 = "203";
	// 2+ flu doses in a single flu season
	public final String fluDoses1 = "301";
	public final String fluDoses2 = "302";
	public final String fluDoses3 = "303";
	// High volume immunizations
	public final String highVolume1 = "401";
	public final String highVolume2 = "402";
	public final String highVolume3 = "403";
	// Controls
	public final String control1 = "501";
	public final String control2 = "502";
	public final String control3 = "503";

	public final List<String> invalidDosesPatients = Arrays.asList(invalidDoses1, invalidDoses2, invalidDoses3);
	public final List<String> covidDosesPatients = Arrays.asList(covidDoses1, covidDoses2, covidDoses3);
	public final List<String> fluDosesPatients = Arrays.asList(fluDoses1, fluDoses2, fluDoses3);
	public final List<String> highVolumePatients = Arrays.asList(highVolume1, highVolume2, highVolume3);
	public final List<String> controlPatients = Arrays.asList(control1, control2, control3);

	public final AgeGroupHelper ageGroupHelper;
	public final ConfigurationPayload configurationPayload;

	protected PossibleMismergedRecordsReportMock() {
		/*
		 * Evaluated after the last administered dose in the fixture (2025-04-01, patient 401) so
		 * that no vaccination is future-dated relative to the evaluation date. The age groups are
		 * irrelevant to this report; patients older than the last bracket fall into
		 * AgeGroupCalculator's overflow group rather than being dropped.
		 */
		ageGroupHelper = new AgeGroupHelper(LocalDate.parse("2025-06-01", DATE_FORMATTER), 3);
		configurationPayload = new ConfigurationPayload();
		configurationPayload.setAsOf("06/01/2025");
		// Must cover MismergedPatientCandidatesReportService.DETECTIONS, otherwise the report is
		// skipped entirely (see LocalReportService.dependenciesAreMet). The high-volume detection
		// is configured too, for the 401-403 cohort.
		configurationPayload.setDetections(Stream.of(
				Detection.VaccineEvaluationHasInvalidDoses5orMore,
				Detection.PatientFluSeasonDoseCountIs2OrMore,
				Detection.PatientCovid2021DoseCountIs4OrMore,
				Detection.PatientCovid2021DoseCountIs5OrMore,
				Detection.PatientCovid2021DoseCountIs6OrMore,
				Detection.AdministeredVaccinationsCountIsLargerThanExpected
		).map(Detection::getMqeMqeCode).collect(Collectors.toList()));
		configurationPayload.setAgeGroups(ageGroupHelper.getAgeGroups());
		configurationPayload.setActivatePatientMatching(false);
		configurationPayload.setVaxCodeAbstraction(null);
	}

	@Override
	public ConfigurationPayload getConfigurationPayload() {
		return configurationPayload;
	}

	@Override
	public List<Record> getDataExtract() {
		List<String> patientLines = readLines(PATIENTS_RESOURCE);
		Map<String, List<String>> vaccinationsByPatientId = new LinkedHashMap<>();
		for (String vaccinationLine : readLines(VACCINATIONS_RESOURCE)) {
			vaccinationsByPatientId
					.computeIfAbsent(columnsOf(vaccinationLine).get(PATIENT_ID_COLUMN), (id) -> new ArrayList<>())
					.add(vaccinationLine);
		}

		List<Record> records = new ArrayList<>();
		for (String patientLine : patientLines) {
			List<String> patientColumns = columnsOf(patientLine);
			List<String> vaccinationLines = vaccinationsByPatientId
					.getOrDefault(patientColumns.get(PATIENT_ID_COLUMN), new ArrayList<>());
			records.add(new Record(
					patientLine,
					patientColumns,
					vaccinationLines,
					vaccinationLines.stream().map(this::columnsOf).collect(Collectors.toList())
			));
		}
		return records;
	}

	private List<String> columnsOf(String line) {
		// -1 keeps trailing empty columns, which matter because the extract is positional.
		return Arrays.asList(line.split(SEPARATOR, -1));
	}

	/** Reads an extract file, dropping the trailing CR of its CRLF line endings and blank lines. */
	private List<String> readLines(String resource) {
		InputStream in = PossibleMismergedRecordsReportMock.class.getResourceAsStream(resource);
		if (in == null) {
			throw new IllegalStateException("Missing test resource " + resource);
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
			return reader.lines()
					.map((line) -> line.endsWith("\r") ? line.substring(0, line.length() - 1) : line)
					.filter((line) -> !line.trim().isEmpty())
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new UncheckedIOException("Could not read " + resource, new java.io.IOException(e));
		}
	}

	@Override
	public AgeGroupHelper getAgeGroupHelper() {
		return ageGroupHelper;
	}

	public static PossibleMismergedRecordsReportMock get() {
		return new PossibleMismergedRecordsReportMock();
	}
}
