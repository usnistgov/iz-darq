package gov.nist.healthcare.iz.darq.controller.domain;

public class DetectionDescriptor {
	private String description;
	private String target;
	private boolean active;

	public DetectionDescriptor(String description, String target, boolean active) {
		super();
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
