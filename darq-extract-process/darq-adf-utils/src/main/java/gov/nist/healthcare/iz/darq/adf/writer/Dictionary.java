package gov.nist.healthcare.iz.darq.adf.writer;

import java.util.HashMap;
import java.util.Map;

public class Dictionary {
	Map<String, Integer> values = new HashMap<>();
	int idx = 0;

	public int getId(String value) {
		return values.compute(value, (k, v) -> v != null ? v : ++idx);
	}
}
