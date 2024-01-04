package gov.nist.healthcare.iz.darq.test.data;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.Record;

import java.util.Set;

public interface DataExtractMock {
	ConfigurationPayload getConfigurationPayload();
	Set<Record> getDataExtract();
	AgeGroupHelper getAgeGroupHelper();
}
