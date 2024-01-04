package gov.nist.healthcare.iz.darq.service.impl;

import com.google.common.base.Strings;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKey;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationKeyValue;
import gov.nist.healthcare.iz.darq.model.ToolConfigurationProperty;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ADFTemporaryDirectoryProviderService extends Observable implements ConfigurableService {

	private final ToolConfigurationKey adf_temporary_dir = new ToolConfigurationKey("adf.module.temporary.directory", true);
	private Path temporaryDirectory;

	@Override
	public boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet) {
		return keyValueSet.stream().anyMatch((kv) -> kv.getKey().equals(adf_temporary_dir.getKey()));
	}

	@Override
	public void configure(Properties properties) throws Exception {
		if(properties.containsKey(adf_temporary_dir.getKey()) && !Strings.isNullOrEmpty(properties.getProperty(adf_temporary_dir.getKey()))) {
			String tmp_dir = properties.getProperty(adf_temporary_dir.getKey());
			Path path = Paths.get(tmp_dir);
			Files.createDirectories(path);
			temporaryDirectory = path.toAbsolutePath();
			notifyObservers(temporaryDirectory);
		}
	}

	@Override
	public Set<ToolConfigurationProperty> initialize() {
		String tmpdir = System.getProperty("java.io.tmpdir");

		return new HashSet<>(Collections.singletonList(
				new ToolConfigurationProperty(adf_temporary_dir.getKey(), Paths.get(tmpdir, "QDAR_ADF_TEMPORARY_DIR").toAbsolutePath().toString(), true)
		));
	}

	@Override
	public Set<ToolConfigurationKey> getConfigurationKeys() {
		return new HashSet<>(Collections.singletonList(adf_temporary_dir));
	}

	@Override
	public OpAck<Void> checkServiceStatus() {
		if(temporaryDirectory != null) {
			String status = "Temporary Location At ";
			File dir = temporaryDirectory.toFile();
			status += dir.getAbsolutePath();
			boolean exists = dir.exists();
			boolean isDir = dir.isDirectory();
			status += " ";
			status += exists ? "(exists)" : "(does not exist)";
			status += isDir ? "(directory)" : "(not directory)";
			long size = FileUtils.sizeOfDirectory(dir);
			status += "(size "+ humanReadableByteCount(size, true) + ")";
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

	@Override
	public String getServiceDisplayName() {
		return "ADF_TEMP_DIRECTORY";
	}

	public String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public Path getADFTemporaryDirectoryPath() {
		return temporaryDirectory;
	}

}
