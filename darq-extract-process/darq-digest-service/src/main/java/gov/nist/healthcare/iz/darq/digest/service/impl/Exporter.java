package gov.nist.healthcare.iz.darq.digest.service.impl;


import java.io.File;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile.Vocabulary;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import gov.nist.healthcare.iz.darq.digest.service.ExportADChunk;
import gov.nist.healthcare.iz.darq.digest.service.HTMLSummaryGenerator;


@Service
public class Exporter implements ExportADChunk {

	@Autowired
	private CryptoUtils cryptoUtils;
	@Autowired
	private HTMLSummaryGenerator summaryGenerator;
	
	
	@Override
	public void export(ConfigurationPayload payload, ADChunk chunk) throws Exception {
		
		Summary summary = new Summary(chunk, payload);
		ADFile file = new ADFile(chunk.getExtraction(), chunk.getPatientSection(), chunk.getVaccinationSection(), payload, summary, new Vocabulary(chunk.getValues(), chunk.getCodes()));
		
		File output = new File("./darq-analysis/");
	    output.mkdirs();
	    
	    //---- ENCRYPT
	    byte[] fileBytes = this.cryptoUtils.encrypt(file);
	    FileUtils.writeByteArrayToFile(new File("./darq-analysis/ADF.data"), fileBytes);
	    
	    //---- HTML
	    summaryGenerator.generateSummary(summary, chunk.getProviders(), "./darq-analysis/summary/");		
	}

}
