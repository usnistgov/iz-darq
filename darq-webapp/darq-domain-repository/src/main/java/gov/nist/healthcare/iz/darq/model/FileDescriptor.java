package gov.nist.healthcare.iz.darq.model;


public class FileDescriptor {

	private String id;
	private String name;
	private String path;
	private String type;
	private String description;

	public FileDescriptor(String id, String name, String path, String type, String description) {
		this.id = id;
		this.name = name;
		this.path = path;
		this.type = type;
		this.description = description;
	}

	public FileDescriptor() {
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


}
