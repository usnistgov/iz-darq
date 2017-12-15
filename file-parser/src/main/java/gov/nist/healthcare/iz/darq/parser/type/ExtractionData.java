package gov.nist.healthcare.iz.darq.parser.type;

public class ExtractionData {
	
	private DescriptorType code;
	private int length;
	
	public ExtractionData(DescriptorType code, int length) {
		super();
		this.code = code;
		this.length = length;
	}
		
	public ExtractionData(DescriptorType code) {
		super();
		this.code = code;
	}

	public DescriptorType getCode() {
		return code;
	}
	public void setCode(DescriptorType code) {
		this.code = code;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	} 

}
