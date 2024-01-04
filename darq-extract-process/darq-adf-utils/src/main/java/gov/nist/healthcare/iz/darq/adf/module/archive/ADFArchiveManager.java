package gov.nist.healthcare.iz.darq.adf.module.archive;

import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;

public interface ADFArchiveManager {
	void create(String sourceFile, String targetFile, ADFVersion version) throws Exception;
	void extract(String sourceFile, String targetFile) throws Exception;
	byte[] getFileVersion(String file) throws Exception;

	static ADFArchiveManager getInstance() {
		return new TarADFArchiveManager();
	}
}
