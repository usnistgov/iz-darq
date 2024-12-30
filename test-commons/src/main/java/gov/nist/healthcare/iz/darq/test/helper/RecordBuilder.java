package gov.nist.healthcare.iz.darq.test.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecordBuilder extends LineBuilder<RecordBuilder> {
	AgeGroupHelper ageGroupHelper;
	private static final int PATIENT_DOB = 8;
	private final List<VaccinationBuilder> vaccinations = new ArrayList<>();
	protected String DOB;
	private final Map<String, Integer> tables;
	private final ExtractBuilder extractBuilder;

	public RecordBuilder(ExtractBuilder extractBuilder, AgeGroupHelper ageGroupHelper) {
		super(0, 34);
		this.ageGroupHelper = ageGroupHelper;
		this.extractBuilder = extractBuilder;
		tables = new HashMap<>();
		tables.put("GENDER_0001", 9);
		tables.put("RACE_0005", 15);
		tables.put("ETH_0189", 16);
		tables.put("LANG_0296", 19);
		tables.put("BIRTH_US_POSTAL_STATE", 29);
		tables.put("MULTI_BIRTH", 27);
		tables.put("FACILITY_PSTAT_0441", 30);
		tables.put("IIS_PSTAT_0441", 31);
	}

	public RecordBuilder(AgeGroupHelper ageGroupHelper) {
		this(null, ageGroupHelper);
	}

	protected Map<String, Integer> getTables() {
		return tables;
	}

	public RecordBuilder withAgeGroup(int i) {
		DOB = ageGroupHelper.getDateInAgeGroup(i);
		writer.put(DOB, PATIENT_DOB);
		return this;
	}

	public RecordBuilder withDOB(String DOB) {
		writer.put(DOB, PATIENT_DOB);
		return this;
	}

	public VaccinationBuilder withVaccination() {
		VaccinationBuilder vaccine = new VaccinationBuilder(this);
		this.vaccinations.add(vaccine);
		return vaccine;
	}

	public Record get() {
		this.fill();
		String patient = this.getLine();
		List<String> patientColumns = this.getLineColumns();
		List<String> vaccinations = this.vaccinations.stream()
		                                             .peek(LineBuilder::fill)
				                                     .map(LineBuilder::getLine)
				                                     .collect(Collectors.toList());
		List<List<String>> vaccinationColumns =  this.vaccinations.stream().map(LineBuilder::getLineColumns).collect(Collectors.toList());
		return new Record(
				patient,
				patientColumns,
				vaccinations,
				vaccinationColumns
		);
	}

	public ExtractBuilder and() {
		return extractBuilder;
	}

	@Override
	protected void close() {}
}
