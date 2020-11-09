package gov.nist.healthcare.domain;

public class OpAck<T> {
	
	public enum AckStatus {
		SUCCESS, FAILED, WARNING, INFO
	}
	
	private AckStatus status;
	private String text;
	private T data;
	private String op;

	public OpAck() {
	}

	public OpAck(AckStatus status, String text, T data, String op) {
		super();
		this.status = status;
		this.text = text;
		this.op = op;
		this.data = data;
	}

	public AckStatus getStatus() {
		return status;
	}

	public void setStatus(AckStatus status) {
		this.status = status;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
