package gov.nist.healthcare.iz.darq.adf.module.sqlite.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.HashMap;
import java.util.Map;

public class Dictionaries {
	Map<Field, BiMap<String, Integer>> dictionary = new HashMap<>();
	Map<Field, Integer> index = new HashMap<>();

	public int getId(Field field, String value) {
		return dictionary.compute(field, (k, values) -> {
			if(values == null) values = HashBiMap.create();
			return values;
		}).compute(value, (k, v) -> {
			int idx = index.compute(field, (fk, i) -> {
				if(i == null) return 0;
				else return i;
			});
			if(v == null) {
				index.put(field, idx + 1);
				return idx + 1;
			} else return v;
		});
	}

	public Map<Field, BiMap<String, Integer>> get() {
		return dictionary;
	}

	public int findId(Field field, String value) {
		if(dictionary.containsKey(field)) {
			if(dictionary.get(field).containsKey(value)) {
				return dictionary.get(field).get(value);
			}
		}
		return -1;
	}

	public String findValue(Field field, int id) {
		if(dictionary.containsKey(field)) {
			if(dictionary.get(field).inverse().containsKey(id)) {
				return dictionary.get(field).inverse().get(id);
			}
		}
		return "";
	}

	public Map<String, Integer> getValues(Field field) {
		return dictionary.getOrDefault(field, HashBiMap.create());
	}

	public void put(Field field, Map<String, Integer> values) {
		if(dictionary.get(field) == null) dictionary.put(field, HashBiMap.create(values));
		else {
			dictionary.get(field).putAll(values);
		}
		index.put(field, dictionary.get(field).values().stream().mapToInt(i -> i).max().orElse(0));
	}
}
