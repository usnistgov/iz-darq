package gov.nist.healthcare.iz.darq.detections;


public class DetectionDescriptor {
	private String code;
	private String description;
	private String target;
	private boolean active;
	private boolean complex;
	private String lifecycle;

	public DetectionDescriptor(String code, String description, String target, boolean active) {
		super();
		this.code = code;
		this.description = description;
		this.target = target;
		this.active = active;
		this.complex = false;
		this.lifecycle = "";
	}

	public DetectionDescriptor(String code, String description, String target, boolean active, String lifecycle) {
		super();
		this.code = code;
		this.description = description;
		this.target = target;
		this.active = active;
		this.complex = false;
		this.lifecycle = lifecycle;
	}

	public DetectionDescriptor(String code, String description, String target, boolean active, boolean complex) {
		super();
		this.code = code;
		this.description = description;
		this.target = target;
		this.active = active;
		this.complex = complex;
		this.lifecycle = "";
	}

	public DetectionDescriptor(String code, String description, String target, boolean active, boolean complex, String lifecycle) {
		super();
		this.code = code;
		this.description = description;
		this.target = target;
		this.active = active;
		this.complex = complex;
		this.lifecycle = lifecycle;
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

	public boolean isComplex() {
		return complex;
	}

	public void setComplex(boolean complex) {
		this.complex = complex;
	}

    public String getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }
}
