package gov.nist.healthcare.iz.darq.digest.service;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import gov.nist.healthcare.iz.darq.digest.domain.Range;


public interface AgeGroupService {

	List<String> getGroups();
	String getGroup(LocalDate from, LocalDate to);
	boolean inside(Period period, Range range);

}