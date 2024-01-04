package gov.nist.healthcare.iz.darq.service.impl;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFModule;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFModule;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

@Service
public class ConfigurableSqliteADFModule implements ADFModule, Observer {
	private SqliteADFModule module;

	public ConfigurableSqliteADFModule(ADFTemporaryDirectoryProviderService provider) {
		if(provider.getADFTemporaryDirectoryPath() != null) {
			refresh(provider.getADFTemporaryDirectoryPath());
		}
		provider.addObserver(this);
	}

	@Override
	public ADFWriter getWriter(CryptoKey key) {
		return module.getWriter(key);
	}

	@Override
	public ADFReader getReader(String location) {
		return module.getReader(location);
	}

	@Override
	public ADFVersion getVersion() {
		return module.getVersion();
	}

	@Override
	public boolean isInstanceOfVersion(String file) throws Exception {
		return module.isInstanceOfVersion(file);
	}

	@Override
	public void merge(List<ADFReader> files, CryptoKey key, String targetLocation) throws Exception {
		module.merge(files, key, targetLocation);
	}

	@Override
	public void update(Observable o, Object temporaryDirectory) {
		refresh((Path) temporaryDirectory);
	}

	public void refresh(Path temporaryDirectory) {
		module = new SqliteADFModule(temporaryDirectory.toAbsolutePath().toString());
	}
}
