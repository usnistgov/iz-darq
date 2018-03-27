package gov.nist.healthcare.iz.darq.digest.service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Range;

public interface ExportADChunk {
	
	void export(ConfigurationPayload payload, ADChunk chunk) throws JsonGenerationException, JsonMappingException, IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, Exception;

}
