package gov.nist.healthcare.iz.darq.adf.module.sqlite;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.exception.UnsupportedADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFModule;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SqliteADFModule implements ADFModule {

	private final String DEFLATE_DIR;

	public SqliteADFModule(String DEFLATE_DIR) {
		this.DEFLATE_DIR = DEFLATE_DIR;
	}

	@Override
	public ADFWriter getWriter(CryptoKey key) {
		return new SqliteADFWriter(key);
	}

	@Override
	public ADFReader getReader(String location) {
		return new SqliteADFReader(location, DEFLATE_DIR);
	}

	@Override
	public ADFVersion getVersion() {
		return ADFVersion.ADFSQLITE0001;
	}

	@Override
	public boolean isInstanceOfVersion(BufferedInputStream content) throws IOException {
		int magicLength = getVersion().name().length();
		content.mark(magicLength + 1);
		byte[] magic = new byte[magicLength];
		int nb = content.read(magic, 0, magicLength);
		content.reset();
		if(nb == magicLength) {
			return Arrays.equals(getVersion().name().getBytes(StandardCharsets.UTF_8), magic);
		}
		return false;
	}

	public String getDeflateDirLocation() {
		return DEFLATE_DIR;
	}

	@Override
	public OutputStream merge(InputStream a, InputStream b) throws UnsupportedADFVersion {
		throw new UnsupportedADFVersion(getVersion());
	}
}
