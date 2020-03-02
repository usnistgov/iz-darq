package gov.nist.healthcare.iz.darq.parser.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DescriptorType {
	EXCLUDED("EXCLUDED", "EX"),
	NOT_COLLECTED("NOT_COLLECTED", "NC"),
	NOT_EXTRACTED("NOT_EXTRACTED", "NE"),
	VALUE_PRESENT("VALUE_PRESENT", "VP"),
	VALUE_NOT_PRESENT("VALUE_NOT_PRESENT", "NP"),
	VALUE_LENGTH("VALUE_LENGTH_IS", "LEN"),
	EMPTY("");

	private List<String> values;

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	DescriptorType(String ...values) {
		this.values = Arrays.asList(values);
	}

	public static DescriptorType token(String token) {
		for(DescriptorType type: DescriptorType.values()) {
			if(type.values.contains(token)) return type;
		}
		throw new IllegalArgumentException(token);
	}
}
