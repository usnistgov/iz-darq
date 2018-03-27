package gov.nist.healthcare.iz.darq.analyzer.domain;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.iz.darq.analyzer.domain.Field._CG;

public class AnalysisPayload {
	
	public static class FieldValue {
		Field field;
		String value;
		
		public FieldValue(Field field, String value) {
			super();
			this.field = field;
			this.value = value;
		}
		
		public FieldValue() {
			super();
		}

		public Field getField() {
			return field;
		}
		public void setField(Field field) {
			this.field = field;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((field == null) ? 0 : field.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FieldValue other = (FieldValue) obj;
			if (field != other.field)
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		
	}
	
	private _CG type;
	private List<FieldValue> filters;
	private List<Field> groupBy;
	public _CG getType() {
		return type;
	}
	
	
	public AnalysisPayload() {
		super();
		this.filters = new ArrayList<>();
		this.groupBy = new ArrayList<>();
	}


	public void setType(_CG type) {
		this.type = type;
	}
	public List<FieldValue> getFilters() {
		return filters;
	}
	public void setFilters(List<FieldValue> filters) {
		this.filters = filters;
	}
	public List<Field> getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(List<Field> groupBy) {
		this.groupBy = groupBy;
	}
}
