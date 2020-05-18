package gov.nist.healthcare.iz.darq.analyzer.model.analysis;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.digest.domain.Analysis;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public class AnalysisQuery {
	public static enum Action {
		CONTINUE, TAKE, KILL 
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
			 return (queryField.isAll() || queryField.getValues().contains(value)) ? Action.TAKE : Action.KILL;
		}
		else {
			return Action.CONTINUE;
		}
	}
	
	public Action consider(TrayField tf){
		return this.consider(tf.getField(), tf.getV());
	}

	public QueryField get(Field f){
		return this.fields.stream().filter(x -> x.getF().equals(f)).findFirst().orElse(null);
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
				.map(this::consider)
				.anyMatch(y -> y.equals(Action.KILL));

		return kill ? Action.KILL : t.full() ? Action.TAKE : Action.CONTINUE;
	}
	
}
