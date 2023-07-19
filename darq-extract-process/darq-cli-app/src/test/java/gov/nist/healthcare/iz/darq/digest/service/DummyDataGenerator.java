package gov.nist.healthcare.iz.darq.digest.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DummyDataGenerator {
	private final List<String> REPORTING_GROUPS = new ArrayList<>();
	private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private final LocalDate TOP_DATE = LocalDate.parse("2020-12-14", DATE_FORMATTER);
	private final int NB_AGE_GROUPS = 24;
	private final int NB_REPORTING_GROUPS = 4000;
	private final Random random = new Random();
	private final List<String> VALUES = Arrays.asList("[[VP]]", "[[NP]]");
	private final int PATIENT_SIZE = 34;
	private final int PATIENT_ID = 0;
	private final int PATIENT_DOB = 8;
	private final List<Integer> PATIENT_DATES = Arrays.asList(32, 33, 28);
	private final int VAX_SIZE = 26;
	private final int VAX_PAT_ID = 0;
	private final int VAX_ID = 1;
	private final int VAX_REPORTING_GROUP = 2;
	private final int VAX_ADMIN = 9;
	private final List<Integer> VAX_DATES = Arrays.asList(15, 24, 25, 22, 23);
	private final List<Integer> PATIENT_CODES = Arrays.asList(9, 19, 16, 15, 29, 27, 30, 31);
	private final List<Integer> VACCINE_CODES = Arrays.asList(7, 8, 10, 12, 13, 14, 19, 18, 20, 21);

	private final Map<Integer, List<String>> PCODES = new HashMap<>();
	private final Map<Integer, List<String>> VCODES = new HashMap<>();
	private final int MAX_CODES = 50;
	private final int MIN_CODES = 20;


	String getRandomString(int length) {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'


		return random.ints(leftLimit, rightLimit + 1)
				.limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();

	}

	int getRandomNumber(int min, int max) {
		return random.nextInt((max + 1) - min) + min;
	}

	String getRandomValue() {
		int idx = getRandomNumber(0,1);
		return VALUES.get(idx);
	}

	String getRandomReportingGroup() {
		int idx = getRandomNumber(0,REPORTING_GROUPS.size() - 1);
		return REPORTING_GROUPS.get(idx);
	}

	String getRandomVCODE(int i) {
		int idx = getRandomNumber(0,VCODES.get(i).size() - 1);
		return VCODES.get(i).get(idx);
	}

	String getRandomPCODE(int i) {
		int idx = getRandomNumber(0,PCODES.get(i).size() - 1);
		return PCODES.get(i).get(idx);
	}


	void generateRandomReportingGroups() {
		REPORTING_GROUPS.clear();
		for(int i = 0; i < NB_REPORTING_GROUPS; i++) {
			REPORTING_GROUPS.add(getRandomString(10));
		}
	}

	void generateRandomCodeSets() {
		PCODES.clear();
		this.PATIENT_CODES.stream().forEach((i) -> {
			PCODES.put(i, IntStream.range(0, getRandomNumber(MIN_CODES, MAX_CODES)).mapToObj((j) -> getRandomString(10)).collect(Collectors.toList()));
		});
		VCODES.clear();
		this.VACCINE_CODES.stream().forEach((i) -> {
			VCODES.put(i, IntStream.range(0, getRandomNumber(MIN_CODES, MAX_CODES)).mapToObj((j) -> getRandomString(10)).collect(Collectors.toList()));
		});
	}

	String getRandomDateOfBirth() {
		int group = getRandomNumber(1, NB_AGE_GROUPS);
		return DATE_FORMATTER.format(TOP_DATE.minusMonths(group));
	}

	String getRandomAdminDate(String dob) {
		LocalDate DOB = LocalDate.parse(dob, DATE_FORMATTER);
		int days = getRandomNumber(0, 20);
		return DATE_FORMATTER.format(DOB.plusDays(days));
	}

	void generate(String patients, String vaccinations, int nb_patients, int max_vax_per_patient) throws IOException {
		FileWriter patientsFile = new FileWriter(patients);
		FileWriter vaccinationsFile = new FileWriter(vaccinations);
		generateRandomReportingGroups();
		generateRandomCodeSets();
		for(int i = 0; i < nb_patients; i++) {
			generateRecord(max_vax_per_patient, patientsFile, vaccinationsFile);
		}
		patientsFile.close();
		vaccinationsFile.close();
	}

	void generateRecord(int maxVaxNb, FileWriter patientsFile, FileWriter vaccinationsFile) throws IOException {
		String ID = getRandomString(8);
		String DOB = getRandomDateOfBirth();
		int nbVaxRecords = getRandomNumber(1, maxVaxNb);
		String patient = generatePatient(ID, DOB);
		String[] vaccinations = generateVaccinations(ID, DOB, nbVaxRecords);
		patientsFile.write(patient);
		patientsFile.write("\n");
		vaccinationsFile.write(String.join("\n", vaccinations));
		vaccinationsFile.write("\n");
	}


	String generatePatient(String ID, String DOB) {
		StringBuilder patientBuilder = new StringBuilder();
		for(int i = 0; i < PATIENT_SIZE; i++) {
			switch (i) {
				case PATIENT_ID:
					patientBuilder.append(ID);
					break;
				case PATIENT_DOB:
					patientBuilder.append(DOB);
					break;
				default:
					if(PATIENT_DATES.contains(i)) {
						patientBuilder.append("[[NP]]");
					} else if (PATIENT_CODES.contains(i)) {
						patientBuilder.append(getRandomPCODE(i));
					} else {
						patientBuilder.append(getRandomString(10));
					}
					break;
			}
			patientBuilder.append("\t");
		}
		return patientBuilder.toString();
	}

	String[] generateVaccinations(String ID, String DOB, int nbVax) {
		String[] vaccines = new String[nbVax];
		for(int v = 0; v < nbVax; v++) {
			StringBuilder vaccineBuilder = new StringBuilder();
			String VID = getRandomString(8);
			String REPORTING_GROUP = getRandomReportingGroup();
			String ADMIN = getRandomAdminDate(DOB);
			for(int i = 0; i < VAX_SIZE; i++) {
				switch (i) {
					case VAX_PAT_ID:
						vaccineBuilder.append(ID);
						break;
					case VAX_ID:
						vaccineBuilder.append(VID);
						break;
					case VAX_REPORTING_GROUP:
						vaccineBuilder.append(REPORTING_GROUP);
						break;
					case VAX_ADMIN:
						vaccineBuilder.append(ADMIN);
						break;
					default:
						if(VAX_DATES.contains(i)) {
							vaccineBuilder.append("[[NP]]");
						} else if (VACCINE_CODES.contains(i)) {
							vaccineBuilder.append(getRandomVCODE(i));
						} else {
							vaccineBuilder.append(getRandomString(10));
						}
						break;
				}
				vaccineBuilder.append("\t");
			}
			vaccines[v] = vaccineBuilder.toString();
		}
		return vaccines;
	}

}
