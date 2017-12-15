package gov.nist.lightdb.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;

public interface Searcher {

	Map<EntityType, List<String>> get(Path index, Path data, String ID, EntityType... types) throws IOException;
	
}
