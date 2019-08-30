package gov.nist.healthcare.iz.darq.parser.service.model;

import gov.nist.healthcare.iz.darq.parser.type.DataUnit;

public class DataElement {
	String key;
	String name;
	boolean isCoded;
	String table;
	String dummyValue;
	DataUnit<?> value;
	
	
	public DataElement(String key, String name, boolean isCoded, String table, String dummy, DataUnit<?> value) {
		super();
		this.key = key;
		this.name = name;
		this.isCoded = isCoded;
		this.table = table;
		this.value = value;
		this.dummyValue = dummy;
	}
	
	public DataElement() {
		super();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isCoded() {
		return isCoded;
	}
	public void setCoded(boolean isCoded) {
		this.isCoded = isCoded;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public DataUnit<?> getValue() {
		return value;
	}
	public void setValue(DataUnit<?> value) {
		this.value = value;
	}
	public String getDummyValue() {
		return dummyValue;
	}
	public void setDummyValue(String dummyValue) {
		this.dummyValue = dummyValue;
	}
	
}
