package gov.nist.healthcare.iz.darq.adf.module;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.exception.UnsupportedADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFModule;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ADFManager {

	Map<ADFVersion, ADFModule> modules = new HashMap<>();
	ADFVersion fallback;
	ADFVersion _default;

	public void register(ADFModule module, boolean fallback, boolean _default) {
		modules.put(module.getVersion(), module);
		if(fallback) {
			this.fallback = module.getVersion();
		}
		if(_default) {
			this._default = module.getVersion();
		}
	}

	public Set<ADFVersion> getSupportedVersions() {
		return modules.keySet();
	}

	public ADFWriter getWriter(ADFVersion version, CryptoKey key) throws UnsupportedADFVersion {
		if(modules.containsKey(version)) {
			return modules.get(version).getWriter(key);
		} else {
			throw new UnsupportedADFVersion(version);
		}
	}

	public ADFWriter getWriter(CryptoKey key) throws Exception {
		if(_default != null && modules.containsKey(_default)) {
			return modules.get(_default).getWriter(key);
		} else {
			throw new Exception("No default ADF Module defined");
		}
	}

	public ADFReader getADFReader(String location) throws Exception {
		BufferedInputStream fileInputStream = new BufferedInputStream(Files.newInputStream(Paths.get(location)));
		for(ADFModule module : modules.values()) {
			try {
				if(module.isInstanceOfVersion(fileInputStream)) {
					fileInputStream.close();
					return module.getReader(location);
				}
			} catch (Exception ignored) {}
		}

		if(fallback != null && modules.containsKey(fallback)) {
			fileInputStream.close();
			return modules.get(fallback).getReader(location);
		} else {
			fileInputStream.close();
			throw new Exception("Could not find a module to handle ADF at " + location + " no fallback module is defined");
		}
	}
}
