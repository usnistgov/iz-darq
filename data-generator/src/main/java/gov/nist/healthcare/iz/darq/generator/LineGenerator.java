package gov.nist.healthcare.iz.darq.generator;

import java.util.List;

import gov.nist.healthcare.iz.darq.model.FieldModel;

public interface LineGenerator {
	
	public String generate(List<FieldModel> fields, String separator);

}
