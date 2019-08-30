package gov.nist.healthcare.iz.darq.parser.type;


import gov.nist.healthcare.iz.darq.parser.exception.InvalidValueException;

/** Class representing a data unit from a data extract
 * It either holds a value or an extraction descriptor describing the value
 *
 * {@link DqDate}
 * {@link DqString}
 * {@link DqNumeric}
 * {@link ExtractionData}
 *
 */
public abstract class DataUnit<T> {

	// The actual value from the extract file as a string
	protected String payload;
	// THe value in its expected type
	protected T value;
	// Whether the DataUnit contains a value
	private boolean extracted;
	// Extraction descriptor about the value when no value is provided
	private ExtractionData placeholder;

	public DataUnit(String payload) throws InvalidValueException {
		int length = -1;
		DescriptorType code;
		extracted = false;

		// If value is coded extraction descriptor
		if(payload != null && payload.startsWith("[[") && payload.endsWith("]]")){
			

			String extCode = payload.replace("[[", "").replace("]]", "");

			// If value length is present
			if(extCode.contains("VALUE_LENGTH_IS")){	
				
				try {
					code = DescriptorType.VALUE_LENGTH;
					length = Integer.parseInt(extCode.replace("VALUE_LENGTH_IS {","").replace("}", ""));
					if(length < 0)
						throw new InvalidValueException("Invalid length : "+payload);
					placeholder = new ExtractionData(code, length);
				}
				catch(InvalidValueException e){
					throw e;
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

			this.validatePlaceHolder(placeholder.getCode());
		}
		else {
			
			if(payload == null){
				throw new InvalidValueException("Field value can't be null");
			}
			else if(payload.isEmpty()){
				placeholder = new ExtractionData(DescriptorType.EMPTY, -1);
			}
			else {
				// If field is valued validate the field
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
		if(this.placeholder != null){
			if(this.placeholder.getCode().equals(DescriptorType.VALUE_PRESENT)) {
				return dummy(-1);
			}
			else if(this.placeholder.getCode().equals(DescriptorType.VALUE_LENGTH)) {
				return dummy(this.placeholder.getLength());
			}
			else {
				return this.empty();
			}
		}
		else {
			return value;
		}
	}
	
	protected abstract T dummy(int n);
	protected abstract T empty();
	protected abstract void validatePlaceHolder(DescriptorType placeholder) throws InvalidValueException;
	protected abstract T validate(String payload) throws InvalidValueException;
	
}
