package gov.nist.healthcare.iz.darq.controller.domain;

public class DetectionDescriptor {
	private String description;
	private String target;
	public DetectionDescriptor(String description, String target) {
		super();
		this.description = description;
		this.target = target;
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
}
