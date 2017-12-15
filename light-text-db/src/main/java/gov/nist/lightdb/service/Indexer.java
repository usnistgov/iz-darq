package gov.nist.lightdb.service;

import java.nio.file.Path;

import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;

public interface Indexer {
	
	public void master(Path dataDir, Path indexDir, EntityType type) throws Exception;
	public void slave(Path dataDir, Path indexDir, EntityType type);

}
