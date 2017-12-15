package gov.nist.healthcare.iz.darq.data.service;

import java.util.List;

import gov.nist.healthcare.iz.darq.data.exceptions.NoIndexFound;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;

public interface DataSearch {
	
	List<String> find(EntityType type, String id, JobDataToken token) throws NoIndexFound;

}
