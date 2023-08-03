package gov.nist.healthcare.iz.darq.test.helper;

import java.util.HashMap;
import java.util.Map;

public class VaccinationBuilder extends LineBuilder<VaccinationBuilder> {
	RecordBuilder recordBuilder;
	private final int VAX_PAT_ID = 0;
	private final int VAX_REPORTING_GROUP = 2;
	private final int VAX_ADMIN = 9;
	private final Map<String, Integer> tables;

	public VaccinationBuilder(RecordBuilder recordBuilder) {
		super(1, 26);
		this.recordBuilder = recordBuilder;
		tables = new HashMap<>();
		tables.put("CVX", 7);
		tables.put("NDC", 8);
		tables.put("MVX", 10);
		tables.put("EVENT_NIP001", 12);
		tables.put("BODY_ROUTE_0162_NCIT", 13);
		tables.put("BODY_SITE_0163", 14);
		tables.put("ELIG_0064", 19);
		tables.put("COMP_STATUS_0322", 18);
		tables.put("FUNDING_PHVS_ImmunizationFundingSource_IIS", 20);
		tables.put("VIS_GDTI", 21);
	}

	public VaccinationBuilder withAdminAtAgeGroup(int i) {
		this.writer.put(recordBuilder.ageGroupHelper.getDateInAgeGroupAt(i, recordBuilder.DOB), VAX_ADMIN);
		return this;
	}

	public VaccinationBuilder withReportingGroup(String value) {
		this.writer.put(value, VAX_REPORTING_GROUP);
		return this;
	}

	public RecordBuilder and() {
		return recordBuilder;
	}

	@Override
	protected Map<String, Integer> getTables() {
		return tables;
	}

	public void close() {
		this.writer.put(recordBuilder.ID, VAX_PAT_ID);
	}
}
