package gov.nist.healthcare.iz.darq.generator;

import java.io.IOException;

import gov.nist.healthcare.iz.darq.model.LineModel;

public interface Generator {

		public void generateData(int n, int v_min, int v_max, LineModel patient, LineModel vaccination, GeneratedData destination) throws IOException;
		
}
