package gov.nist.healthcare.iz.darq.generator;

import java.io.IOException;
import java.util.Random;
import java.util.stream.Stream;
import gov.nist.healthcare.iz.darq.model.DataType;
import gov.nist.healthcare.iz.darq.model.LineModel;
import gov.nist.lightdb.domain.EntityTypeRegistry;

public class SimpleDataGenerator implements Generator {


	@Override
	public void generateData(int n, int v_min, int v_max, LineModel patient, LineModel vaccination, GeneratedData destination) throws IOException {

		String patientLine = makeLine(patient);
		String vaccinationLine = makeLine(vaccination);
		
		Stream.iterate(0, i -> i).limit(n).forEach(i -> {
			String id = genId();
			destination.get(EntityTypeRegistry.get("patient")).println(line(id, makeLine(patient)));
			int v_n = (new Random()).nextInt(v_max) + v_min;
			Stream.iterate(0, j -> i).limit(v_n).forEach(j -> {
				destination.get(EntityTypeRegistry.get("vaccination")).println(line(id, makeLine(vaccination)));
			});
			
		});
		
	}
	
	private String genId(){
		int  n = (new Random()).nextInt(25) + 65;
		int  id = (new Random()).nextInt(99999999);
		return (char) n + String.format("%08d", id);
	}
	
	private String line(String id, String line){
		return id + line;
	}
	
	private String makeLine(LineModel model){
		StringBuilder builder = new StringBuilder();
		for(LineModel.Step step : model.getSteps()){
			add(step, builder);
		}
		return builder.toString();
	}
	
	private void add(LineModel.Step step, StringBuilder builder){
		String unit = getUnit(step.type);
		Stream.iterate(0, i -> i).limit(step.times).forEach(i -> {
			builder.append("\t").append(unit);
		});
	}
	
	private String getUnit(DataType type){
		
		switch(type){
		case NUMERIC : return "1233432";
		case DATE : return "20170410";
		case NO_EXTRACT : return "[[NOT_EXTRACTED]]";
		default : {
//			int  n = (new Random()).nextInt(20);
//			if(n == 1) return "[[VALUE_NOT_PRESENT]]";
			return "STRINGSTRINGSTRING";
		}
		}
	}

	
}
