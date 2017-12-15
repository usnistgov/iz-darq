package gov.nist.healthcare.iz.darq.generator;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;


public class GeneratedData implements Closeable{
	
	private PrintWriter patient;
	private PrintWriter vaccination;
	
	public GeneratedData(OutputStream patient, OutputStream vaccination) throws IOException {
		super();
		
		this.patient = new PrintWriter(new OutputStreamWriter(patient, Charset.forName("UTF-8").newEncoder()));
		this.vaccination = new PrintWriter(new OutputStreamWriter(vaccination, Charset.forName("UTF-8").newEncoder()));
	}

	public PrintWriter get(EntityType type){
		if(type.name.equals("patient")) return patient;
		if(type.name.equals("vaccination")) return vaccination;
		return null;
	}

	@Override
	public void close() throws IOException {
		patient.close();
		vaccination.close();
	}
	
}
