package gov.nist.healthcare.iz.darq.controller.domain;

public class EntityDescriptor {
	private String id;
	private String name;
	
	
	public EntityDescriptor(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public EntityDescriptor() {
		super();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	

}
