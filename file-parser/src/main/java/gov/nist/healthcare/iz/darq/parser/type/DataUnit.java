package gov.nist.healthcare.iz.darq.parser.type;

import gov.nist.lightdb.exception.InvalidValueException;

public abstract class DataUnit<T> {
	
	protected String payload;
	protected T value;
	private boolean extracted;
	private ExtractionData placeholder;

	public DataUnit(String payload) throws InvalidValueException {
		int length = -1;
		DescriptorType code;
		extracted = false;
		if(payload != null && payload.startsWith("[[") && payload.endsWith("]]")){
			
			
			String extCode = payload.replace("[[", "").replace("]]", "");
			
			if(extCode.contains("VALUE_LENGTH_IS")){	
				
				try {
					code = DescriptorType.VALUE_LENGTH;
					length = Integer.parseInt(extCode.replace("VALUE_LENGTH_IS {","").replace("}", ""));
					placeholder = new ExtractionData(code, length);
				}
				catch(Exception e){
					throw new InvalidValueException("Unrecognized : "+payload);
				}
				
			}
			else {
				
				code = DescriptorType.valueOf(extCode);
				if(code != null){
					placeholder = new ExtractionData(code);
				}
				else {
					throw new InvalidValueException("Unrecognized : "+payload);
				}
				
			}
		}
		else {
			
			if(payload == null){
				throw new InvalidValueException("Field value can't be null");
			}
			else if(payload.isEmpty()){
				placeholder = new ExtractionData(DescriptorType.EMPTY, -1);
			}
			else {
				this.value = validate(payload);
				this.extracted = true;
			}
		}
		this.payload = payload;
	}
	
	public ExtractionData placeholder(){
		return placeholder;
	}
	
	public boolean hasValue(){
		return extracted;
	}
	
	public String toString(){
		return payload;
	}
	
	public T getValue(){
		return value;
	}
	
	protected abstract T validate(String payload) throws InvalidValueException;

}
