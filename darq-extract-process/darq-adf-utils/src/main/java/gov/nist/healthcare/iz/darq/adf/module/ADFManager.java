package gov.nist.healthcare.iz.darq.adf.module;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.exception.UnsupportedADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFModule;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import java.util.*;

public class ADFManager {

	Map<ADFVersion, ADFModule> modules = new HashMap<>();
	ADFVersion fallback;
	ADFVersion _default;
	private final HashMap<String, HashSet<String>> compatibilityVersions = new HashMap<String, HashSet<String>>() {{
		put("COMPATIBILITY_1", new HashSet<>(Arrays.asList(
				"2.0.0-SNAPSHOT", "2.0.0", "2.0.1", "2.0.2", "2.0.3"
		)));
		put("COMPATIBILITY_2", new HashSet<>(Arrays.asList(
				"2.1.0", "2.1.1", "2.1.2"
		)));
		put("COMPATIBILITY_3", new HashSet<>(Arrays.asList(
				"3.0.0-SNAPSHOT", "3.0.0-SNAPSHOT.rev1"
		)));
		put("COMPATIBILITY_4", new HashSet<>(Arrays.asList(
				"3.0.0", "3.0.1"
		)));
	}};


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
		for(ADFModule module : modules.values()) {
			try {
				if(module.isInstanceOfVersion(location)) {
					return module.getReader(location);
				}
			} catch (Exception ignored) {}
		}

		if(fallback != null && modules.containsKey(fallback)) {
			return modules.get(fallback).getReader(location);
		} else {
			throw new Exception("Could not find a module to handle ADF at " + location + " no fallback module is defined");
		}
	}

	public void merge(List<ADFReader> files, String target, CryptoKey key) throws Exception {
		assertFilesAreMergeable(files);
		ADFVersion version = files.get(0).getVersion();

		ADFModule module = modules.get(version);
		if(module != null) {
			module.merge(files, key, target);
		} else {
			throw new Exception("No ADF module found for version " + version);
		}

	}

	public void assertFilesAreMergeable(List<ADFReader> files) throws Exception {
		// Check ADF Versions
		ADFVersion adfVersion = files.get(0).getVersion();
		if(files.stream().anyMatch(file -> !file.getVersion().equals(adfVersion))) {
			throw new Exception("Files don't have the same adf version");
		}

		// Check CLI/MQE version
		String version = this.compatibilityVersion(files.get(0));
		if(files.stream().anyMatch(file -> !this.compatibilityVersion(file).equals(version))) {
			throw new Exception("Files don't have the same compatibility version");
		}

		// Check Configuration
		ConfigurationPayload configuration = files.get(0).getConfigurationPayload();
		if(files.stream().anyMatch(file -> !file.getConfigurationPayload().equals(configuration))) {
			throw new Exception("Files don't have the same configuration");
		}

		// Check Inactive
		Set<String> inactive = files.get(0).getMetadata().getInactiveDetections();
		if(files.stream().anyMatch(file -> !file.getMetadata().getInactiveDetections().equals(inactive))) {
			throw new Exception("Files don't have the same active/inactive detections");
		}
	}

	public String compatibilityVersion(ADFReader file) {
		String version = Optional.ofNullable(file.getMetadata().getVersion()).orElse(this.generateRandomString(5));
		String compatibilityVersion = this.compatibilityVersions.entrySet().stream().filter(
				(entry) -> entry.getValue().contains(version)
		).findFirst().map(Map.Entry::getKey).orElse(version);

		String mqe = Optional.ofNullable(file.getMetadata().getMqeVersion()).orElse(this.generateRandomString(5));
		return compatibilityVersion + " - " + mqe;
	}

	public String generateRandomString(int size) {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

}
