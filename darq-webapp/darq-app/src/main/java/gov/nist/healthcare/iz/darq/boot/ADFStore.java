package gov.nist.healthcare.iz.darq.boot;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.ADFManager;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.adf.module.transformer.TransformerService;
import gov.nist.healthcare.iz.darq.service.impl.ADFStorage;
import gov.nist.healthcare.iz.darq.service.impl.ToolConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ADFStore {
	@Autowired
	@Qualifier("ADF_KEYS")
	CryptoKey adfKeys;
	@Value("#{environment.QDAR_STORE}")
	private String QDAR_STORE;
	@Autowired
	TransformerService transformerService;
	@Autowired
	ADFManager manager;
	@Autowired
	ADFVersion supportedVersion;
	@Autowired
	ToolConfigurationService configurationService;

	@PostConstruct
	void checkFilesInADFStore() throws Exception {
		Class.forName("org.sqlite.JDBC");
		System.out.println("===== CHECKING ADF STORE FOLDER @ " + QDAR_STORE + "=====");
		//-- Verify ADF Folder
		File f = new File(QDAR_STORE);
		if(!f.exists()){
			if(!f.mkdirs() || !f.exists()){
				System.out.println(" ! Could not create directory : "+ QDAR_STORE);
				throw new Exception("Could not create directory : "+ QDAR_STORE);
			}
		} else {
			int nb = Objects.requireNonNull(f.list()).length;
			System.out.println(" * Store Directory Exists");
			System.out.println(" * Contains " + nb + " ADFs");
			AtomicInteger i = new AtomicInteger();
			Path storePath = Paths.get(QDAR_STORE);
			try (Stream<Path> walk = Files.walk(storePath)) {
				List<String> errors = walk.filter(Files::isDirectory)
						.filter((path) -> path.compareTo(storePath) != 0)
						.map((path) -> {
							System.out.println(" + Checking ADF " + (i.incrementAndGet()) + "/" + nb + " @ " + path.toAbsolutePath());
							File adf = Paths.get(path.toAbsolutePath().toString(), ADFStorage.ADF_FILENAME).toFile();
							if(!adf.exists()) {
								System.out.println(" ! No ADF file ( "+ ADFStorage.ADF_FILENAME +" ) found at " + path.toAbsolutePath());
								return "No ADF file ( "+ ADFStorage.ADF_FILENAME +" ) found at " + path.toAbsolutePath();
							}
							try(ADFReader reader = manager.getADFReader(adf.getAbsolutePath())) {
								System.out.println(" * Checking ADF encryption key");
								if(reader.checkADFKey(adfKeys)) {
									System.out.println(" * Checking ADF Version ("+reader.getVersion()+")");
									if(!reader.getVersion().equals(supportedVersion)) {
										System.out.println(" * Converting ADF to " + supportedVersion);
										reader.read(adfKeys);
										transformerService.transform(
												reader.getVersion(),
												supportedVersion,
												adfKeys,
												reader,
												adf.toPath(),
												false
										);
									}
								} else {
									System.out.println(" ! Current ADF KeyPair is not valid from ADF at " + adf.getAbsolutePath());
									return "Current ADF KeyPair is not valid from ADF at " + adf.getAbsolutePath();
								}
								System.out.println(" * All Checks Pass ");
								return null;
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println(" ! Exception encountered while checking ADF at " + adf.getAbsolutePath() + " " + e.getMessage());
								return "Exception encountered while checking ADF at " + adf.getAbsolutePath() + " " + e.getMessage();
							}
						}).filter(Objects::nonNull).collect(Collectors.toList());
				if(errors.size() > 0) {
					throw new Exception("[QDAR_ADF_STORE_CHECK] " + String.join(", ", errors));
				}
			}
		}
	}
}
