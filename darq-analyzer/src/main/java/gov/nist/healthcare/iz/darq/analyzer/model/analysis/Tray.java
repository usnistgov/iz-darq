package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public abstract class Tray implements Cloneable {

	public static class VaxTray extends Tray {
		
		public VaxTray() {
			super();
		}

		public VaxTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
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
		}

		public VaxDetectionTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
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
		}

		public VaxCodeTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
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
		}

		public PatCodeTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
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
		}

		public PatDetectionTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
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

	public static class PatRgDetectionTray extends Tray {

		public PatRgDetectionTray() {
			super();
		}

		public PatRgDetectionTray(Set<TrayField> fields, int count, int weigth) {
			super(fields, count, weigth);
		}

		@Override
		public boolean full() {
			return has(Arrays.asList(Field.PROVIDER, Field.AGE_GROUP, Field.DETECTION));
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
		return fields.stream().map(TrayField::getField)
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
		this.fields.remove(tf);
		this.fields.add(tf);
	}
	
	public void remove(Field f){
		TrayField tf = new TrayField(f,"");
		this.fields.remove(tf);
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
		return fields.stream().filter(tf -> tf.getField().equals(f)).findFirst().map(x -> {
			return x.getV();
		}).orElseGet(() -> "");
	}
	@Override
	public String toString() {
		return "Tray [fields=" + fields + ", count=" + count + ", weigth=" + weigth + "]";
	}
	
}
