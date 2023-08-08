package gov.nist.healthcare.iz.darq.adf.module.sqlite;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFModule;
import gov.nist.healthcare.iz.darq.adf.module.archive.ADFArchiveManager;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SqliteADFModule extends SqliteADFMerger implements ADFModule  {

	private final String TMP_WORK_DIR;

	public SqliteADFModule(String TMP_WORK_DIR) {
		this.TMP_WORK_DIR = TMP_WORK_DIR;
	}

	@Override
	public SqliteADFWriter getWriter(CryptoKey key) {
		return new SqliteADFWriter(key);
	}

	@Override
	public SqliteADFReader getReader(String location) {
		return new SqliteADFReader(location, TMP_WORK_DIR);
	}

	@Override
	public ADFVersion getVersion() {
		return ADFVersion.ADFSQLITE0001;
	}

	@Override
	public boolean isInstanceOfVersion(String file) throws Exception {
		byte[] version = ADFArchiveManager.getInstance().getFileVersion(file);
		return Arrays.equals((getVersion().name() + '\0').getBytes(StandardCharsets.UTF_8), version);
	}

	@Override
	public void merge(List<ADFReader> files, CryptoKey key, String targetLocation) throws Exception {
		this.merge(files.stream().map(f -> (SqliteADFReader) f).collect(Collectors.toList()), key, TMP_WORK_DIR, getVersion(), targetLocation);
	}

	public String getTmpDirLocation() {
		return TMP_WORK_DIR;
	}

}
