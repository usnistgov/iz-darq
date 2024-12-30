package gov.nist.healthcare.iz.darq.test.helper;

import java.util.Arrays;
import java.util.List;

public class LineWriter {
	String[] fields;

	public LineWriter(int size) {
		this.fields = new String[size];
	}

	public void put(String value, int i) {
		if(i < fields.length) {
			fields[i] = value;
		}
	}

	public List<String> getColumns() {
		return Arrays.asList(fields);
	}

	public String getLine() {
		return String.join("\t", fields);
	}

	public void fill(String value) {
		for(int i = 0; i < fields.length; i++) {
			if(fields[i] == null || fields[i].isEmpty()) {
				fields[i] = value;
			}
		}
	}
}
