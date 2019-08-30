package gov.nist.healthcare.iz.darq.model;

public class TableConstraint extends ValueConstraint {
	
	public TableConstraint() {
		super(ValueConstraintType.TABLE);
	}
	
	private String table;
	private Distribution codes;
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public Distribution getCodes() {
		return codes;
	}
	public void setCodes(Distribution codes) {
		this.codes = codes;
	} 
	
}
