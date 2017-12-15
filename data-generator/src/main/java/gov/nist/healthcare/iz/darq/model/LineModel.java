package gov.nist.healthcare.iz.darq.model;

import java.util.ArrayList;
import java.util.List;

public class LineModel {
	
	public class Step {
		public DataType type;
		public int times;
		
		public Step(DataType type, int times) {
			super();
			this.type = type;
			this.times = times;
		}
	}
	
	public class Builder {
	
		private List<Step> steps = new ArrayList<>();
		public Builder step(DataType t, int i){
			steps.add(new Step(t,i));
			return this;
		}
		public Builder step(DataType t){
			steps.add(new Step(t,1));
			return this;
		}
		public Builder assertLineSize(int i){
			assert((steps.size() + 1) == i);
			return this;
		}
		public LineModel build(){
			return new LineModel(steps);
		}
	}
	
	private Step[] steps;

	public LineModel(List<Step> steps) {
		super();
		this.steps = steps.toArray(new Step[0]);
	}
	
	public Step[] getSteps() {
		return steps;
	}

	private LineModel(){
		
	}
	
	public static Builder builder(){
		return new LineModel().new Builder();
	}
	
}
