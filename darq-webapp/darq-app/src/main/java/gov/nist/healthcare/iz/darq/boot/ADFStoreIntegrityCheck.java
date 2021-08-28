package gov.nist.healthcare.iz.darq.boot;

import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.service.impl.ADFStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ADFStoreIntegrityCheck {

    @Autowired
    @Qualifier("ADF_KEYS")
    CryptoKey adfKeys;
    @Autowired
    CryptoUtils cryptoUtils;
    @Value("#{environment.QDAR_STORE}")
    private String QDAR_STORE;

    @PostConstruct
    void check() throws Exception {
    	//-- Verify ADF Folder
    	File f = new File(QDAR_STORE);
    	if(!f.exists()){
    		if(!f.mkdirs() || !f.exists()){
    			throw new Exception("Could not create directory : "+ QDAR_STORE);
    		}
    	} else {
			Path storePath = Paths.get(QDAR_STORE);
			try (Stream<Path> walk = Files.walk(storePath)) {
				List<String> errors = walk.filter(Files::isDirectory)
						.filter((path) -> path.compareTo(storePath) != 0)
						.map((path) -> {
					File adf = Paths.get(path.toAbsolutePath().toString(), ADFStorage.ADF_FILENAME).toFile();
					if(!adf.exists()) {
						return "No ADF file ( "+ ADFStorage.ADF_FILENAME +" ) found at " + path.toAbsolutePath().toString();
					}
					try {
						if(this.cryptoUtils.checkAdfStore(new FileInputStream(adf))) {
							return null;
						} else {
							return "Current ADF KeyPair is not valid from ADF at " + adf.getAbsolutePath();
						}
					} catch (Exception e) {
						e.printStackTrace();
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
