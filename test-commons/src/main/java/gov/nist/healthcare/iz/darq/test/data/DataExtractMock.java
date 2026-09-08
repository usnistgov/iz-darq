package gov.nist.healthcare.iz.darq.test.data;

import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.test.helper.AgeGroupHelper;
import gov.nist.healthcare.iz.darq.test.helper.Record;

import java.util.List;

public interface DataExtractMock {
	ConfigurationPayload getConfigurationPayload();
	List<Record> getDataExtract();
	AgeGroupHelper getAgeGroupHelper();
}
