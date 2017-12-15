package gov.nist.lightdb.domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;

public class Source {
	
	private EntityType type;
	private InputStream stream;
	
	private Source(EntityType type) {
		super();
		this.type = type;
	}
	
	public Source(EntityType type, InputStream i){
		this(type);
		this.stream = i;
	}
	
	public Source(EntityType type, Path p) throws FileNotFoundException {
		this(type);
		this.stream = new FileInputStream(p.toFile());
	}
	
	public InputStream getPayload(){
		return stream;
	};
	
	public EntityType getType() {
		return type;
	}

}
