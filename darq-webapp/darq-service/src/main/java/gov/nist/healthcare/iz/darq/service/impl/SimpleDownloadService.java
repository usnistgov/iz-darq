package gov.nist.healthcare.iz.darq.service.impl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.impl.CryptoUtilsImpl;
import gov.nist.healthcare.iz.darq.model.*;
import gov.nist.healthcare.iz.darq.model.FileDescriptor;
import gov.nist.healthcare.iz.darq.service.utils.DownloadService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.xml.bind.DatatypeConverter;

public class SimpleDownloadService implements DownloadService {
	public static final String RESOURCES_JAR_FILE = "qdar-cli.jar";
	private final CryptoKey cryptoKey;

	private final String JARFILE_LOCATION = "qdar.cli.jar.base.location";
	private final String TARGET_LOCATION = "qdar.cli.jar.generated.location";
	private final String FILES_MANIFEST_LOCATION = "qdar.download.files.manifest.location";

	private String jarFileLocation = "";
	private String targetLocation  = "";
	private String filesManifestLocation  = "";

	private qDARJarFile jarFile;
	private String jarFileCreationIssue;
	private Map<String, FileDescriptor> fileDescriptorMap;
	private String fileDescriptorMapCreationIssue;

	public SimpleDownloadService(CryptoKey cryptoKey) {
		super();
		this.cryptoKey = cryptoKey;
	}

	@Override
	public qDARJarFile getJarFileInfo() {
		return this.jarFile;
	}

	@Override
	public InputStream getFile(String id) {
		if(this.fileDescriptorMap != null) {
			try {
				return new FileInputStream(new File(this.fileDescriptorMap.get(id).getPath()));
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	@Override
	public List<FileDescriptorWrapper> catalog() {
		if(this.fileDescriptorMap != null) {
			List<FileDescriptorWrapper> result = new ArrayList<>();
			for (Entry<String, FileDescriptor> e : this.fileDescriptorMap.entrySet()) {
				FileDescriptorWrapper wrapper = new FileDescriptorWrapper(
						e.getKey(),
						e.getValue().getName(),
						e.getValue().getDescription()
				);
				result.add(wrapper);
			}
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public FileDescriptor getInfo(String id) {
		if(this.fileDescriptorMap != null) {
			return this.fileDescriptorMap.get(id);
		}
		return null;
	}

	public InputStream getPublicKeyDER() throws Exception {
		return new ByteArrayInputStream(this.cryptoKey.getPublicKey().getEncoded());
	}

	public void setJarFileInfo(Path jarPath) throws IOException, NoSuchAlgorithmException {
		File jarFileDescriptor = jarPath.toFile();
		qDARJarFile jarFile = new qDARJarFile();
		jarFile.setLocation(jarPath);
		try (JarFile jar = new JarFile(jarFileDescriptor)) {
			for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().equals("application.properties")) {
					Properties application = new Properties();
					application.load(jar.getInputStream(entry));
					jarFile.setVersion(application.getProperty("app.version"));
					jarFile.setBuildAt(application.getProperty("app.date"));
					jarFile.setMqeVersion(application.getProperty("mqe.version"));
				}
				if (entry.getName().equals(CryptoUtilsImpl.PUB_KEY_RESOURCE_NAME)) {
					byte[] key = IOUtils.toByteArray(jar.getInputStream(entry));
					byte[] hash = MessageDigest.getInstance("MD5").digest(key);
					jarFile.setKeyHash(DatatypeConverter.printHexBinary(hash));
				}
			}
		}
		this.jarFile = jarFile;
	}

	public void makeCLIWithCurrentPublicKey(Path baseJar, Path target) throws Exception {
		File jarFileDescriptor = baseJar.toFile();
		Path targetJar = target.resolve(RESOURCES_JAR_FILE);
		try (ZipInputStream jar = new ZipInputStream(new FileInputStream(jarFileDescriptor))) {
			FileOutputStream output = new FileOutputStream(targetJar.toFile());
			try (ZipOutputStream updateJar = new ZipOutputStream(output)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				InputStream publicKeyInputStream = getPublicKeyDER();
				ZipEntry pubKeyEntry = new ZipEntry(CryptoUtilsImpl.PUB_KEY_RESOURCE_NAME);
				updateJar.putNextEntry(pubKeyEntry);

				while ((bytesRead = publicKeyInputStream.read(buffer)) != -1) {
					updateJar.write(buffer, 0, bytesRead);
				}


				ZipEntry entry = jar.getNextEntry();
				while (entry != null){
					if (!entry.getName().equals(CryptoUtilsImpl.PUB_KEY_RESOURCE_NAME)) {
						updateJar.putNextEntry(entry);
						while ((bytesRead = jar.read(buffer)) != -1) {
							updateJar.write(buffer, 0, bytesRead);
						}
					}
					entry = jar.getNextEntry();
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
			} finally {
				this.setJarFileInfo(targetJar);
			}
		}
	}

	@Override
	public void configure(Properties properties) throws Exception {
		try {
			makeFilesManifest(properties);
		} catch (Exception e) {
			this.fileDescriptorMapCreationIssue = e.getMessage();
		}
		try {
			makeJarFile(properties);
		} catch (Exception e) {
			this.jarFileCreationIssue = e.getMessage();
		}
	}

	public void makeJarFile(Properties properties) throws Exception {
		this.jarFileCreationIssue = null;
		this.jarFileLocation = properties.getProperty(JARFILE_LOCATION);
		this.targetLocation = properties.getProperty(TARGET_LOCATION);
		boolean createTargetDirectory = Strings.isNullOrEmpty(targetLocation) ||
				!Paths.get(targetLocation).toFile().exists() ||
				!Paths.get(targetLocation).toFile().isDirectory();

		boolean useDefaultJar = Strings.isNullOrEmpty(jarFileLocation) ||
				!Paths.get(jarFileLocation).toFile().exists() ||
				!isJarFile(Paths.get(jarFileLocation).toFile());

		// Initialize Target Location
		File target = createTargetDirectory ? Paths.get(System.getProperty("user.home"),"qdar_cli_generated_location-" + RandomStringUtils.randomAlphabetic(5)).toFile()
											: Paths.get(targetLocation).toFile();
		if(!target.exists() && !target.mkdirs()) {
			throw new Exception("Failed to create qDAR CLI Generated directory at " + target.getAbsolutePath());
		}

		// Initialize Jar Location
		File jar = useDefaultJar ? target.toPath().resolve("base.jar").toFile() : Paths.get(jarFileLocation).toFile();

		if(!jar.exists()){
			Files.copy(SimpleDownloadService.class.getResourceAsStream("/" + RESOURCES_JAR_FILE), jar.toPath());
		}

		this.makeCLIWithCurrentPublicKey(jar.toPath(), target.toPath());
	}

	public void makeFilesManifest(Properties properties) throws Exception {
		this.fileDescriptorMapCreationIssue = null;
		this.filesManifestLocation = properties.getProperty(FILES_MANIFEST_LOCATION);
		if(!Strings.isNullOrEmpty(filesManifestLocation)) {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<HashMap<String, FileDescriptor>> typeRef = new TypeReference<HashMap<String, FileDescriptor>>() {};
			Map<String, FileDescriptor> map = mapper.readValue(new FileInputStream(new File(filesManifestLocation)), typeRef);
			Set<Entry<String, FileDescriptor>> valid =  map.entrySet().stream().filter((entry) -> {
				String path = entry.getValue().getPath();
				if(Strings.isNullOrEmpty(path)) {
					return false;
				}
				else {
					File file =  new File(entry.getValue().getPath());
					return file.exists() && file.canRead() && !file.isDirectory();
				}
			}).collect(Collectors.toSet());
			if(valid.size() < map.size()) {
				this.fileDescriptorMapCreationIssue = "Some files in manifest have invalid locations";
			}
			this.fileDescriptorMap = valid.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		} else {
			this.fileDescriptorMap = new HashMap<>();
		}
	}

	@Override
	public boolean shouldReloadOnUpdate(Set<ToolConfigurationKeyValue> keyValueSet) {
		return this.getConfigurationKeys().stream().anyMatch((a) -> keyValueSet.stream().anyMatch((b) -> a.getKey().equals(b.getKey())));
	}

	@Override
	public Set<ToolConfigurationProperty> initialize() {
		return new HashSet<>(
				Arrays.asList(
						new ToolConfigurationProperty(JARFILE_LOCATION, "", false),
						new ToolConfigurationProperty(TARGET_LOCATION, "", false),
						new ToolConfigurationProperty(FILES_MANIFEST_LOCATION, "", false)
				)
		);
	}

	@Override
	public Set<ToolConfigurationKey> getConfigurationKeys() {
		return new HashSet<>(
				Arrays.asList(
						new ToolConfigurationKey(JARFILE_LOCATION, false),
						new ToolConfigurationKey(TARGET_LOCATION, false),
						new ToolConfigurationKey(FILES_MANIFEST_LOCATION, false)
				)
		);
	}

	@Override
	public OpAck<Void> checkServiceStatus() {
		List<String> warning = new ArrayList<>();

		// JARFILE
		if(!Strings.isNullOrEmpty(jarFileLocation)) {
			File jar = new File(jarFileLocation);
			if(!jar.exists()) {
				warning.add("Base qDAR Jar File Location does not exist (Using Default From Resources)");
			}
			if(jar.isDirectory()) {
				warning.add("Base qDAR Jar File Location is directory (Using Default From Resources)");
			}
		}


		// TARGET
		if(!Strings.isNullOrEmpty(targetLocation)) {
			File target = new File(targetLocation);
			if(!target.exists()) {
				warning.add("Target Location to store generated qDAR Jar File does not exist (Generating one in user.home)");
			}
			if(!target.isDirectory()) {
				warning.add("Target Location to store generated qDAR Jar File is not directory (Generating one in user.home)");
			}
		}

		if(jarFile == null) {
			return new OpAck<>(OpAck.AckStatus.FAILED, "Jar File Could not be created : " + jarFileCreationIssue, null, "JAR CREATION");
		}

		if(!Strings.isNullOrEmpty(filesManifestLocation)) {
			File manifest = new File(filesManifestLocation);
			if(!manifest.exists()) {
				warning.add("Files Manifest Location does not exist");
			}
			if(manifest.isDirectory()) {
				warning.add("Download Files Manifest Location is directory");
			}
			if(fileDescriptorMap == null) {
				warning.add("Files Manifest could not be read : " + fileDescriptorMapCreationIssue);
			}
			if(!Strings.isNullOrEmpty(fileDescriptorMapCreationIssue)) {
				warning.add("Files Manifest Issue Encountered : " + fileDescriptorMapCreationIssue);
			}
		}

		if(warning.size() > 0) {
			return new OpAck<>(OpAck.AckStatus.WARNING, String.join(", ", warning), null, "");
		} else {
			return new OpAck<>(OpAck.AckStatus.SUCCESS, "Configuration is valid", null, "JAR CREATION");
		}
	}

	@Override
	public String getServiceDisplayName() {
		return "DOWNLOAD_SERVICE";
	}

	/**
	 * Determine whether a file is a JAR File.
	 */
	public static boolean isJarFile(File file) throws IOException {
		if (!isZipFile(file)) {
			return false;
		}
		ZipFile zip = new ZipFile(file);
		boolean manifest = zip.getEntry("META-INF/MANIFEST.MF") != null;
		zip.close();
		return manifest;
	}
	/**
	 * Determine whether a file is a ZIP File.
	 */
	public static boolean isZipFile(File file) throws IOException {
		if(file.isDirectory()) {
			return false;
		}
		if(!file.canRead()) {
			throw new IOException("Cannot read file "+file.getAbsolutePath());
		}
		if(file.length() < 4) {
			return false;
		}
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		int test = in.readInt();
		in.close();
		return test == 0x504b0304;
	}
}
