package gov.nist.healthcare.iz.darq.test.data.mocks;

import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;

import java.util.List;
import java.util.Set;

public class SimpleExamplePartOneMock extends SimpleExampleMock {

	@Override
	public List<Record> getDataExtract() {
		return new ExtractBuilder(ageGroupHelper)
				.withRecord()
				// Record 1
				.withAgeGroup(0)
				.withValue(PNF, "[[VP]]").withValue(PNL, "[[VP]]").withVocabulary("GENDER_0001", "F")
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_1")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[NP]]").withReportingGroup("GRP_2")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[NP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_2")
				.withVocabulary("EVENT_NIP001", "01").withVocabulary("CVX", "115").withVocabulary("NDC", "49281-0400-15")
				.and()
				.and()
				.withRecord()
				// Record 2
				.withAgeGroup(0)
				.withValue(PNF, "[[NP]]").withValue(PNL, "[[VP]]").withVocabulary("GENDER_0001", "F")
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_1")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[NP]]").withValue(VF, "[[NP]]").withReportingGroup("GRP_2")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "UNRECOGNIZED")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[NP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_2")
				.withVocabulary("EVENT_NIP001", "01").withVocabulary("CVX", "115").withVocabulary("NDC", "UNRECOGNIZED")
				.and()
				.and()
				.get();
	}

	public static SimpleExamplePartOneMock get() {
		return new SimpleExamplePartOneMock();
	}

}
