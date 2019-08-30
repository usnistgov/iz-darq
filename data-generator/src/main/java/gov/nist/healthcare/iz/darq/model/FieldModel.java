package gov.nist.healthcare.iz.darq.model;

import java.util.List;

public class FieldModel {
	
	private String ID;
	private int position;
	private DataType type;
	private List<String> dependencies;
	private ValueConstraint value;
	
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	public ValueConstraint getValue() {
		return value;
	}
	public void setValue(ValueConstraint value) {
		this.value = value;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public List<String> getDependencies() {
		return dependencies;
	}
	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}
	
	
}
