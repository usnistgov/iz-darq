package gov.nist.healthcare.iz.darq.digest.service.report.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AggregateRow {
	List<String> indexedValues;
	List<String> nonIndexedValues;

	public AggregateRow(List<String> indexedValues, List<String> nonIndexedValues) {
		this.indexedValues = indexedValues;
		this.nonIndexedValues = nonIndexedValues;
	}

	public List<String> getIndexedValues() {
		return indexedValues;
	}

	public List<String> getNonIndexedValues() {
		return nonIndexedValues;
	}

	public static AggregateRow withIndexed(String ...indexedValues) {
		return new AggregateRow(Arrays.asList(indexedValues), new ArrayList<>());
	}
}
