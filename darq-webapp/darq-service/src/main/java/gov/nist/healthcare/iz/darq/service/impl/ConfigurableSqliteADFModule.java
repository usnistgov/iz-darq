package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.adf.exception.UnsupportedADFVersion;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFModule;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFModule;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKey;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationProperty;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ConfigurableSqliteADFModule implements ADFModule, ConfigurableService {
	private final ToolConfigurationKey temporary_deflate_dir = new ToolConfigurationKey("sqlite.adf.module.deflate.directory", true);
	private SqliteADFModule module;

	@Override
	public boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet) {
		return keyValueSet.stream().anyMatch((kv) -> kv.getKey().equals(temporary_deflate_dir.getKey()));
	}

	@Override
	public void configure(Properties properties) throws Exception {
		if(properties.containsKey(temporary_deflate_dir.getKey()) && !Strings.isNullOrEmpty(properties.getProperty(temporary_deflate_dir.getKey()))) {
			String tmp_dir = properties.getProperty(temporary_deflate_dir.getKey());
			Path path = Paths.get(tmp_dir);
			Files.createDirectories(path);
			module = new SqliteADFModule(path.toAbsolutePath().toString());
		}
	}

	@Override
	public Set<ToolConfigurationProperty> initialize() {
		String tmpdir = System.getProperty("java.io.tmpdir");

		return new HashSet<>(Collections.singletonList(
				new ToolConfigurationProperty(temporary_deflate_dir.getKey(), Paths.get(tmpdir, "QDAR_ADF_SQLITE_DEFLATE").toAbsolutePath().toString(), true)
		));
	}

	@Override
	public Set<ToolConfigurationKey> getConfigurationKeys() {
		return new HashSet<>(Collections.singletonList(temporary_deflate_dir));
	}

	@Override
	public OpAck<Void> checkServiceStatus() {
		if(module != null) {
			String status = "Deflate Location At ";
			File dir = Paths.get(module.getDeflateDirLocation()).toFile();
			status += dir.getAbsolutePath();
			boolean exists = dir.exists();
			boolean isDir = dir.isDirectory();
			status += " ";
			status += exists ? "(exists)" : "(does not exist)";
			status += isDir ? "(directory)" : "(not directory)";
			try {
				long size = Files.size(dir.toPath());
				status += "(size "+ humanReadableByteCount(size, true) + ")";
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			boolean successful = exists && isDir;
			return new OpAck<>(
					successful ? OpAck.AckStatus.SUCCESS : OpAck.AckStatus.FAILED,
					status,
					null,
					getServiceDisplayName()
			);
		} else {
			return new OpAck<>(
					OpAck.AckStatus.FAILED,
					"Service Uninitialized",
					null,
					getServiceDisplayName()
			);
		}
	}

	public String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	@Override
	public String getServiceDisplayName() {
		return "SQLITE_ADF_MODULE";
	}

	@Override
	public ADFWriter getWriter(CryptoKey key) throws UnsupportedADFVersion {
		return module.getWriter(key);
	}

	@Override
	public ADFReader getReader(String location) throws UnsupportedADFVersion {
		return module.getReader(location);
	}

	@Override
	public ADFVersion getVersion() {
		return ADFVersion.ADFSQLITE0001;
	}

	@Override
	public boolean isInstanceOfVersion(BufferedInputStream content) throws IOException, UnsupportedADFVersion {
		return module.isInstanceOfVersion(content);
	}

	@Override
	public OutputStream merge(InputStream a, InputStream b) throws UnsupportedADFVersion {
		return module.merge(a, b);
	}
}
