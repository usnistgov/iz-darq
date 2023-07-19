package gov.nist.healthcare.iz.darq.adf.exception;

import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;

public class UnsupportedADFVersion extends Exception {
	public UnsupportedADFVersion(ADFVersion version) {
		super("Unsupported ADF Version :"+ version);
	}
}
