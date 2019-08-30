package gov.nist.healthcare.iz.darq.analyzer.domain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.analyzer.domain.Tray.TrayField;
import gov.nist.healthcare.iz.darq.digest.domain.Analysis;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public class AnalysisQuery {
	public static enum Action {
		CONTINUE, TAKE, KILL 
	}
	public static class QueryField implements Comparable<QueryField>{
		private Field f;
		private String value;
		private boolean all;
		
		public QueryField(Field f, String value) {
			super();
			this.f = f;
			this.value = value;
		}
		public QueryField(Field f) {
			super();
			this.f = f;
			this.all = true;
		}

		public Field getF() {
			return f;
		}
		public void setF(Field f) {
			this.f = f;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public boolean isAll() {
			return all;
		}
		public void setAll(boolean all) {
			this.all = all;
		}
		
		
		
		@Override
		public String toString() {
			return "QueryField [f=" + f + ", value=" + value + ", all=" + all + "]";
		}
		@Override
		public int compareTo(QueryField o) {
			return all && o.all ? 0 : all && !o.all ? 1 : !all && o.all ? -1 : 0;
		}
	}
	
	Set<QueryField> fields;
	_CG compatibilityGroup;
	

	public AnalysisQuery(Set<QueryField> fields, _CG compatibilityGroup) {
		super();
		this.fields = fields;
		this.compatibilityGroup = compatibilityGroup;
	}

	public Action consider(Field f, String value){
		QueryField queryField = this.get(f);
		if(queryField != null){
			 return (queryField.isAll() || queryField.getValue().equals(value)) ? Action.TAKE : Action.KILL;
		}
		else {
			return Action.CONTINUE;
		}
	}
	
	public Action consider(TrayField tf){
		return this.consider(tf.getField(), tf.getV());
	}

	public QueryField get(Field f){
		return this.fields.stream().filter(x -> x.getF().equals(f)).findFirst().orElseGet(() -> null);
	}
	
	public static boolean validateQuery(Set<QueryField> fields,Analysis type){
		Field[] fieldTypes = new Field[fields.size()];
		int i = 0;
		for(QueryField qf : fields){
			fieldTypes[i++] = qf.getF();
		}
		return Field.areCompatible(type, fieldTypes);
	}

	public List<Field> getFields() {
		List<QueryField> f = new ArrayList<>(fields);
		Collections.sort(f);
		return f.stream().map(x -> {
			return x.getF();
		}).collect(Collectors.toList());
	}

	public void setFields(Set<QueryField> fields) {
		this.fields = fields;
	}
	
	public _CG getCompatibilityGroup() {
		return compatibilityGroup;
	}

	public void setCompatibilityGroup(_CG compatibilityGroup) {
		this.compatibilityGroup = compatibilityGroup;
	}
	
	@Override
	public String toString() {
		return "AnalysisQuery [fields=" + fields + ", compatibilityGroup=" + compatibilityGroup + "]";
	}

	public Action take(Tray t){
		boolean kill = t.getFields()
				.stream()
				.map(x -> consider(x))
				.filter(y -> y.equals(Action.KILL))
				.findAny().isPresent();

		Action act = kill ? Action.KILL : t.full() ? Action.TAKE : Action.CONTINUE;
		return act;
	}
	
}
