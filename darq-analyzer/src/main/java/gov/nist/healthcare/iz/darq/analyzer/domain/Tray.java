package gov.nist.healthcare.iz.darq.analyzer.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.spi.ThrowableInformation;

import gov.nist.healthcare.iz.darq.analyzer.domain.Field._CG;

public abstract class Tray implements Cloneable {
	public static class TrayField {
		Field field;
		String v;
		
		public TrayField(Field field, String v) {
			super();
			this.field = field;
			this.v = v;
		}
		public Field getField() {
			return field;
		}
		public void setField(Field field) {
			this.field = field;
		}
		public String getV() {
			return v;
		}
		public void setV(String v) {
			this.v = v;
		}
		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((field == null) ? 0 : field.hashCode());
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
			TrayField other = (TrayField) obj;
			if (field != other.field)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "TrayField [field=" + field + ", v=" + v + "]";
		}
		
	}
	
	public static class VaxTray extends Tray {
		
		public VaxTray() {
			super();
			// TODO Auto-generated constructor stub
		}

		public VaxTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean full() {
			return has(Arrays.asList(Field.PROVIDER, Field.AGE_GROUP, Field.VACCINE_CODE, Field.GENDER, Field.VACCINATION_YEAR, Field.EVENT));
		}

		@Override
		public _CG compatibilityGroup() {
			return _CG.V;
		}

		@Override
		public VaxTray cloneTray() {
			return new VaxTray(this.copyList(), count, weigth);
		}
	}
	
	public static class VaxDetectionTray extends Tray {
		
		public VaxDetectionTray() {
			super();
			// TODO Auto-generated constructor stub
		}

		public VaxDetectionTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean full() {
			return has(Arrays.asList(Field.PROVIDER, Field.AGE_GROUP, Field.DETECTION));
		}

		@Override
		public _CG compatibilityGroup() {
			return _CG.VD;
		}

		@Override
		public Tray cloneTray() {
			return new VaxDetectionTray(this.copyList(), count, weigth);
		}
	}
	
	public static class VaxCodeTray extends Tray {
		
		public VaxCodeTray() {
			super();
			// TODO Auto-generated constructor stub
		}

		public VaxCodeTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean full() {
			return has(Arrays.asList(Field.PROVIDER, Field.AGE_GROUP, Field.TABLE, Field.CODE));
		}

		@Override
		public _CG compatibilityGroup() {
			return _CG.VT;
		}

		@Override
		public Tray cloneTray() {
			return new VaxCodeTray(this.copyList(), count, weigth);
		}
	}
	
	public static class PatCodeTray extends Tray {
		
		public PatCodeTray() {
			super();
			// TODO Auto-generated constructor stub
		}

		public PatCodeTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean full() {
			return has(Arrays.asList(Field.AGE_GROUP, Field.TABLE, Field.CODE));
		}

		@Override
		public _CG compatibilityGroup() {
			return _CG.PT;
		}

		@Override
		public Tray cloneTray() {
			return new PatCodeTray(this.copyList(), count, weigth);
		}
	}
	
	public static class PatDetectionTray extends Tray {
		
		public PatDetectionTray() {
			super();
			// TODO Auto-generated constructor stub
		}

		public PatDetectionTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean full() {
			return has(Arrays.asList(Field.AGE_GROUP, Field.DETECTION));
		}

		@Override
		public _CG compatibilityGroup() {
			return _CG.PD;
		}

		@Override
		public Tray cloneTray() {
			return new PatDetectionTray(this.copyList(), count, weigth);
		}
	}
	
	Set<TrayField> fields;
	int count;
	int weigth;
	
	public Tray() {
		super();
		this.fields = new HashSet<>();
	}
	
	public Tray(Set<TrayField> fields, int count, int weigth) {
		super();
		this.fields = fields;
		this.count = count;
		this.weigth = weigth;
	}

	public abstract boolean full();
	public abstract _CG compatibilityGroup();
	public abstract Tray cloneTray();
	public boolean has(List<Field> fs){
		return fields.stream().map(x -> {
			System.out.print(x.field);
			return x.field;
		})
		.collect(Collectors.toSet()).containsAll(new HashSet<>(fs));
		
	}
	
	public void clean(){
		this.fields.clear();
	}
	
	public int getWeigth() {
		return weigth;
	}

	public void setWeigth(int weigth) {
		this.weigth = weigth;
	}

	Set<TrayField> copyList(){
		Set<TrayField> copy = new HashSet<>();
		for(TrayField tf : this.fields){
			copy.add(new TrayField(tf.getField(), tf.getV()));
		}
		return copy;
	}
	
	public void start(Field f, String v){
		this.clean();
		this.add(f, v);
	}
	
	public void add(Field f, String v){
		TrayField tf = new TrayField(f,v);
		if(this.fields.contains(tf))
			this.fields.remove(tf);
		this.fields.add(tf);
	}
	public Set<TrayField> getFields() {
		return fields;
	}
	public void setFields(Set<TrayField> fields) {
		this.fields = fields;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String get(Field f){
		return fields.stream().filter(tf -> tf.field.equals(f)).findFirst().map(x -> {
			return x.v;
		}).orElseGet(() -> "");
	}
	@Override
	public String toString() {
		return "Tray [fields=" + fields + ", count=" + count + ", weigth=" + weigth + "]";
	}
	
}
