package gov.nist.healthcare.iz.darq.detections;

public class DetectionDescriptor {
	private String code;
	private String description;
	private String target;
	private boolean active;

	public DetectionDescriptor(String code, String description, String target, boolean active) {
		super();
		this.code = code;
		this.description = description;
		this.target = target;
		this.active = active;
	}
	public DetectionDescriptor() {
		super();
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
