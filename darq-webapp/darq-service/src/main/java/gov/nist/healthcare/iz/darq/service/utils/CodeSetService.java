package gov.nist.healthcare.iz.darq.service.utils;

import java.util.List;

public interface CodeSetService {

	List<String> patientCodes() throws IllegalArgumentException, IllegalAccessException;
	List<String> vaccinationCodes() throws IllegalArgumentException, IllegalAccessException;
	
}
