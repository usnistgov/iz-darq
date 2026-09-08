package gov.nist.healthcare.iz.darq.digest.service;

import gov.nist.healthcare.iz.darq.digest.domain.Range;
import org.joda.time.LocalDate;
import org.joda.time.Period;

import java.util.List;


public interface AgeGroupService {

	List<String> getGroups();
	String getGroup(LocalDate from, LocalDate to);
	boolean inside(Period period, Range range);

}