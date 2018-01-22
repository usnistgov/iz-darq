package gov.nist.healthcare.iz.darq.analysis;

public class Detection {
	
	private String recordId;
	private String type;
	private String message;
	
	
	public Detection(String recordId, String type, String message) {
		super();
		this.recordId = recordId;
		this.type = type;
		this.message = message;
	}
	
	public Detection() {
		super();
	}

	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
