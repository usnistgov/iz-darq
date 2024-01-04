package gov.nist.healthcare.iz.darq.adf.module.json;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.exception.UnsupportedADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFModule;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;

import java.io.BufferedInputStream;
import java.nio.file.Paths;
import java.util.List;

public class BsonADFModule implements ADFModule {

	@Override
	public ADFWriter getWriter(CryptoKey key) throws UnsupportedADFVersion {
		throw new UnsupportedADFVersion(getVersion());
	}

	@Override
	public ADFReader getReader(String location) {
		return new BsonADFReader(Paths.get(location));
	}

	@Override
	public ADFVersion getVersion() {
		return ADFVersion.ADFBSON000001;
	}

	@Override
	public boolean isInstanceOfVersion(String file) throws UnsupportedADFVersion {
		throw new UnsupportedADFVersion(getVersion());
	}

	@Override
	public void merge(List<ADFReader> files, CryptoKey key, String target) throws Exception {
		throw new UnsupportedADFVersion(getVersion());
	}


}
