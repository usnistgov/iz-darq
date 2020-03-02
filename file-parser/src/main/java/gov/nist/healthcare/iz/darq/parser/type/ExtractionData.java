package gov.nist.healthcare.iz.darq.parser.type;

import gov.nist.healthcare.iz.darq.parser.exception.InvalidValueException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractionData {
	
	private DescriptorType code;
	private int length;
	private static Pattern regex = Pattern.compile("\\[{2}([A-Z_-]+)(?: \\{(\\d+)\\})?\\]{2}");
	
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

	@Override
	public String toString() {
		return "ExtractionData{" +
				"code=" + code +
				", length=" + length +
				'}';
	}

	public static ExtractionData parse(String payload) throws InvalidValueException {
		Matcher matcher = regex.matcher(payload);
		if (matcher.find()) {

			String token = matcher.group(1);
			String value = matcher.group(2);

			if(token != null) {
				DescriptorType type =  DescriptorType.token(token);
				if(type.equals(DescriptorType.VALUE_LENGTH)) {
					if(value != null) {
						int length = Integer.parseInt(value);
						if(length < 0) {
							throw new InvalidValueException("Invalid length : "+payload);
						} else {
							return new ExtractionData(type, length);
						}
					} else {
						throw new InvalidValueException("Invalid length : "+payload);
					}
				} else {
					return new ExtractionData(type);
				}
			} else {
				throw new InvalidValueException("payload " + payload + " is not a valid descriptor");
			}
		} else {
			throw new InvalidValueException("payload " + payload + " is not a valid descriptor");
		}
	}

}
