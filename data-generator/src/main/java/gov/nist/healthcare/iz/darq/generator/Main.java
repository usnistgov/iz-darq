package gov.nist.healthcare.iz.darq.generator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import gov.nist.healthcare.iz.darq.model.DataType;
import gov.nist.healthcare.iz.darq.model.LineModel;
import gov.nist.lightdb.domain.EntityTypeRegistry;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		String path = "/Users/hnt5/generated_data";
		SimpleDataGenerator generator = new SimpleDataGenerator();
		EntityTypeRegistry.register("patient", "patients.data", "p", null);
		EntityTypeRegistry.register("vaccination", "vaccines.data", "v", null);
		
		// 7 - STR / 1 - DT / 6 - STR / 2 - NUM / 12 - STR / 1 - NUM / 2 - STR
		LineModel patient = LineModel.builder()
							.step(DataType.STRING, 3)
							.step(DataType.NO_EXTRACT, 1)
							.step(DataType.STRING, 3)
							.step(DataType.DATE)
							.step(DataType.STRING, 6)
							.step(DataType.NUMERIC, 2)
							.step(DataType.STRING, 12)
							.step(DataType.NUMERIC, 1)
							.step(DataType.STRING, 2)
							.assertLineSize(32)
							.build();
		
		// 2 - STR / 1 - DATE / 7 - STR / 1 - DATE / 5 - STR / 2 - DATE / 2 - STR
		LineModel vaccination = LineModel.builder()
							.step(DataType.STRING, 2)
							.step(DataType.NO_EXTRACT)
							.step(DataType.STRING, 7)
							.step(DataType.DATE, 1)
							.step(DataType.STRING, 5)
							.step(DataType.DATE, 2)
							.step(DataType.STRING, 2)
							.assertLineSize(21)
							.build();
		
		Time.INSTANCE.init();
		
		try (
		FileOutputStream patientsFile = new FileOutputStream(Paths.get(path,EntityTypeRegistry.get("patient").i.file).toFile()); 
		FileOutputStream vaccinesFile = new FileOutputStream(Paths.get(path,EntityTypeRegistry.get("vaccination").i.file).toFile()); 
		GeneratedData destination = new GeneratedData(patientsFile, vaccinesFile)
		) {
			
			generator.generateData(10000, 1, 10, patient, vaccination, destination);
			Time.INSTANCE.checkPoint("FINISH GENERATION");
			
		}

	}
}
