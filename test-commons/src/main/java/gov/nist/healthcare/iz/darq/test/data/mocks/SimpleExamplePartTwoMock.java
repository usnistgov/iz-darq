package gov.nist.healthcare.iz.darq.test.data.mocks;

import gov.nist.healthcare.iz.darq.test.helper.ExtractBuilder;
import gov.nist.healthcare.iz.darq.test.helper.Record;

import java.util.Set;

public class SimpleExamplePartTwoMock  extends SimpleExampleMock {

	@Override
	public Set<Record> getDataExtract() {
		return new ExtractBuilder(ageGroupHelper)
				.withRecord()
				// Record 3
				.withAgeGroup(1)
				.withValue(PNF, "[[VP]]").withValue(PNL, "[[NP]]")
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[VP]]").withReportingGroup("GRP_1")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "UNRECOGNIZED")
				.and()
				.withVaccination()
				.withAdminAtAgeGroup(0)
				.withValue(VE, "[[VP]]").withValue(VF, "[[NP]]").withReportingGroup("GRP_3")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.and()
				.withRecord()
				// Record 4
				.withAgeGroup(3)
				.withValue(PNF, "[[NP]]").withValue(PNL, "[[NP]]").withVocabulary("GENDER_0001", "M")
				.withVaccination()
				.withAdminAtAgeGroup(2)
				.withValue(VE, "[[VP]]").withValue(VF, "[[NP]]").withReportingGroup("GRP_3")
				.withVocabulary("EVENT_NIP001", "00").withVocabulary("CVX", "8").withVocabulary("NDC", "58160-0820-01")
				.and()
				.and()
				.get();
	}

	public static SimpleExamplePartTwoMock get() {
		return new SimpleExamplePartTwoMock();
	}

}