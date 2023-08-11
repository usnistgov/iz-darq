package gov.nist.healthcare.iz.darq.digest.app;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Strings;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.module.ADFManager;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFModule;
import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.detections.AvailableDetectionEngines;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.detections.DetectionEngineConfiguration;
import gov.nist.healthcare.iz.darq.digest.app.exception.*;
import gov.nist.healthcare.iz.darq.digest.service.impl.PublicOnlyCryptoKey;
import gov.nist.healthcare.iz.darq.digest.service.impl.SimpleDigestRunner;
import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import gov.nist.healthcare.iz.darq.digest.service.DigestRunner;
import gov.nist.healthcare.iz.darq.digest.service.impl.Exporter;

import javax.xml.bind.DatatypeConverter;

@Configuration
@ComponentScan("gov.nist.healthcare")
public class CLIApp {

	private static final DecimalFormat df = new DecimalFormat(".##");
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	private final static Logger logger = LoggerFactory.getLogger(CLIApp.class.getName());

	private static boolean running = false;
	private static Path temporaryDirectory = null;

	public static void run(String[] args) throws TerminalException {
		try {
			ApplicationContext context = new AnnotationConfigApplicationContext(CLIApp.class);
			Properties properties = new Properties();
			properties.load(CLIApp.class.getResourceAsStream("/application.properties"));
			String version = properties.getProperty("app.version");
			String build = properties.getProperty("app.date");
			String mqeVersion = properties.getProperty("mqe.version");
			CryptoKey cryptoKey = context.getBean(CryptoKey.class);
			String publicKeyHash = "";

			if(cryptoKey instanceof PublicOnlyCryptoKey) {
				try {
					((PublicOnlyCryptoKey) cryptoKey).setPublicKeyFromResource();
					publicKeyHash = DatatypeConverter.printHexBinary(cryptoKey.getPublicKeyHash());
				} catch (Exception e) {
					System.out.println("! No public key found in bundle");
					logger.warn("! No public found in bundle", e);
				}
			}

			String tag = String.format("v%s (%s) [MQE v%s] " + (!Strings.isNullOrEmpty(publicKeyHash) ? "[Key MD5 " + publicKeyHash + "]" : "") , version, build, mqeVersion);

			//--- OPTIONS
			Options options = new Options();
			options.addOption("help", false, "print help");
			options.addOption("p", "patients", true, "Patients Extract File");
			options.addOption("v", "vaccinations", true, "Vaccinations Extract File");
			options.addOption("c", "configuration", true, "Analysis Configuration");
			options.addOption("tmpDir", "temporaryDirectory", true, "Location where to create temporary directory");
			options.addOption("out", "output", true, "Location where to create result directory");
			options.addOption("pa", "printAdf", false, "print ADF content (deprecated)");
			options.addOption("d", "dateFormat", true, "Date Format");
			options.addOption("pub", "publicKey", true, "qDAR Public Key");
			options.addOption("pm", "patientMatching", false, "Activate patient matching");
			options.addOption("npm", "noPatientMatching", false, "Deactivate patient matching");


			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("help")){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("Data At Rest Quality Analysis Command Line Tool "+ tag, options);
				System.exit(0);
			}
			else {
				System.out.println("===================================================================================================");
				System.out.println(" [NIST] Welcome to Data At Rest Quality Analysis Command Line Tool " + tag + " ");
				System.out.println("===================================================================================================");

				boolean patientParamMissing = !cmd.hasOption("p");
				boolean vaxParamMissing = !cmd.hasOption("v");
				boolean confParamMissing = !cmd.hasOption("c");
				if(patientParamMissing || vaxParamMissing || confParamMissing){
					throw new RequiredParameterMissingException(new FileErrorCode(patientParamMissing, vaxParamMissing, confParamMissing));
				}
				else {
					String pFilePath = cmd.getOptionValue("p");
					String vFilePath = cmd.getOptionValue("v");
					String cFilePath = cmd.getOptionValue("c");
					String tmpDirLocation = cmd.getOptionValue("tmpDir");
					boolean printAdf = cmd.hasOption("pa");
					boolean activePatientMatching = cmd.hasOption("pm");
					boolean deActivatePatientMatching = cmd.hasOption("npm");
					String dateFormat = cmd.getOptionValue("d");

					File patients = new File(pFilePath);
					File vaccines = new File(vFilePath);
					File config = new File(cFilePath);
					boolean pFile = patients.exists() && !patients.isDirectory();
					boolean vFile = vaccines.exists() && !vaccines.isDirectory();
					boolean cFile = config.exists() && !config.isDirectory();


					System.out.println("Patients File @ "+ pFilePath + " " + (pFile ? "[FOUND]" : "[ERROR]"));
					System.out.println("Vaccinations File @ "+ vFilePath + " " + (vFile ? "[FOUND]" : "[ERROR]"));
					System.out.println("Configuration File @ "+ cFilePath + " " + (cFile ? "[FOUND]" : "[ERROR]"));
					System.out.println("===================================================================================================");

					if(!pFile || !vFile || !cFile) {
						throw new FileNotFoundException(new FileErrorCode(!pFile, !vFile, !cFile));
					} else {

						// --- Read Configuration file
						ConfigurationPayload configurationPayload;
						try {
							ObjectMapper mapper = new ObjectMapper();
							configurationPayload = mapper.readValue(config,ConfigurationPayload.class);
						}
						catch (Exception e) {
							throw new InvalidConfigurationFileFormatException(e);
						}

						// --- Read Date Format
						DqDateFormat simpleDateFormat = DqDateFormat.forPattern(DEFAULT_DATE_FORMAT);
						if(!Strings.isNullOrEmpty(dateFormat)) {
							try{
								simpleDateFormat = DqDateFormat.forPattern(dateFormat);
							} catch (Exception e) {
								throw new InvalidDateFormatException(e, "Date Format " + dateFormat + " is Invalid ");
							}
						}

						// --- Read Public Key
						if(cmd.hasOption("pub")) {
							String publicKeyLocation = cmd.getOptionValue("pub");
							if(cryptoKey instanceof PublicOnlyCryptoKey) {
								((PublicOnlyCryptoKey) cryptoKey).setPublicKeyFromLocation(publicKeyLocation);
								System.out.println("* Using provided public key " + DatatypeConverter.printHexBinary(cryptoKey.getPublicKeyHash()) + "(MD5)");
							} else {
								throw new PublicKeyException("Public Key parameter (pub) is not supported");
							}
						}

						if(cryptoKey.getPublicKey() == null) {
							throw new PublicKeyException("No public key provided or bundled");
						}

						// --- Create Outputs Folder
						String outputRoot = cmd.hasOption("out") ? cmd.getOptionValue("out") : ".";
						File output = Paths.get(outputRoot, "darq-analysis").toFile();
						output.mkdirs();

						// --- Create Temporary Directory
						temporaryDirectory = createTemporaryDirectory(Optional.ofNullable(tmpDirLocation));

						// --- Configure Detection Engine
						logger.info("Configuring the detection engine");
						DetectionEngine detectionEngine = context.getBean(DetectionEngine.class);
						DetectionEngineConfiguration detectionEngineConfiguration = new DetectionEngineConfiguration();
						detectionEngineConfiguration.setOutputDirectory(output.getAbsolutePath());
						detectionEngineConfiguration.setTemporaryDirectory(temporaryDirectory.toAbsolutePath().toString());
						detectionEngineConfiguration.setConfigurationPayload(configurationPayload);
						detectionEngineConfiguration.addActiveProvider(AvailableDetectionEngines.DP_ID_MQE);
						if(!deActivatePatientMatching && (configurationPayload.isActivatePatientMatching() || activePatientMatching)) {
							detectionEngineConfiguration.addActiveProvider(AvailableDetectionEngines.DP_ID_PM);
						}
						detectionEngine.configure(detectionEngineConfiguration);

						// --- Configure Services
						logger.info("Configuring Services");
						SimpleDigestRunner runner = context.getBean(SimpleDigestRunner.class);
						Exporter export = context.getBean(Exporter.class);
						ADFManager adfManager = context.getBean(ADFManager.class);
						adfManager.register(new SqliteADFModule(temporaryDirectory.toAbsolutePath().toString()), false, true);

						// --- Start Analysis
						System.out.println("Analysis Progress");
						running = true;
						Thread t = progress(runner);
						t.start();
						long start = System.currentTimeMillis();
						try(ADFWriter writer = adfManager.getWriter(cryptoKey)) {
							writer.open(temporaryDirectory.toAbsolutePath().toString());
							runner.digest(configurationPayload, pFilePath, vFilePath, simpleDateFormat, writer, output.toPath(), temporaryDirectory);
							t.join();
							System.out.println("Analysis Finished - Exporting Results");
							long elapsed = System.currentTimeMillis() - start;
							export.export(configurationPayload, output.toPath(), writer, version, build, mqeVersion, elapsed, printAdf);
							logger.info("* Closing ADF Writer");
						}
						System.out.println("Results Exported - END");
						cleanUp();
					}
				}
			}
		}
		catch (ParseException exp) {
			throw new InvalidCommandException(exp);
		}
		catch (InvalidConfigurationPayload exp) {
			throw new InvalidConfigurationFileContentException(exp);
		}
		catch (TerminalException terminalException) {
			throw terminalException;
		}
		catch (Exception exp) {
			throw new ExecutionException(exp, "Execution Failed due to exception");
		}
		catch (OutOfMemoryError outOfMemoryError) {
			throw new TerminalException(outOfMemoryError, 21, "[OUT_OF_MEMORY_ERROR]", "The JVM has run out of memory.", true);
		}
		catch (VirtualMachineError virtualMachineError) {
			throw new TerminalException(virtualMachineError, 22, "[VIRTUAL_MACHINE_ERROR]", "There has been an unexpected virtual machine error", true);
		}
		finally {
			running = false;
			cleanUp();
		}
	}

	public static void cleanUp() {
		try {
			if(temporaryDirectory != null && temporaryDirectory.toFile().exists()) {
				FileUtils.deleteDirectory(temporaryDirectory.toFile());
			}
		} catch (Exception e) {
			System.err.println("ALERT: Unable to delete temporary directory " + temporaryDirectory);
			System.err.println("Due to - " + e.getMessage());
		}
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			run(args);
		} catch (TerminalException exception) {
			if(!StringUtils.isBlank(exception.getPrint())) {
				System.err.println();
				System.err.println(exception.getPrint());
				System.err.println();
			}
			if(!StringUtils.isBlank(exception.getLogs())) {
				logger.error(exception.getLogs(), exception);
			}
			if(exception.isPrintStackTrace()) {
				exception.printStackTrace();
			}
			System.exit(exception.getExitCode());
		}
	}

	public static Path createTemporaryDirectory(Optional<String> directory) throws java.io.FileNotFoundException {
		logger.info("Creating Temporary directory");
		if(directory.isPresent()) {
			logger.info("Directory location provided");
			File location = new File(directory.get());
			if(!location.exists()) {
				logger.error("[TMP DIRECTORY] provided location'" + directory.get() + "' does not exist");
				throw new java.io.FileNotFoundException("provided location'" + directory.get() + "' does not exist");
			}

			if(!location.isDirectory()) {
				logger.error("[TMP DIRECTORY] provided location'" + directory.get() + "' is not directory");
				throw new java.io.FileNotFoundException("provided location'" + directory.get() + "' is not directory");
			}
		}

		Path tmpDir = createDirectory(directory);
		logger.info("Directory created at " + tmpDir);
		return tmpDir;
	}

	private static Path createDirectory(Optional<String> location) {
		String name = RandomStringUtils.random(10, true, true);
		Path path = location.map(s -> Paths.get(s, name)).orElseGet(() -> Paths.get(name));
		path.toFile().mkdir();
		return path.toAbsolutePath();
	}
	
	public static Thread progress(DigestRunner runner){
	    
	    	return new Thread(() -> {
				String[] animation = { "|", "/", "—", "\\" };
				int anime_step = 0;
				Fraction f = new Fraction(0,0);
				double per;
				long estimated;
				long velocity;
			    Timer timer = new Timer(runner.spy().getTotal());

				do{
					f = runner.spy();
					per = f.percent();
					timer.setProcessed(f, System.currentTimeMillis());
					if(f.getCount() == 0 && f.getTotal() == 0){
						System.out.print("\r-- Preparing " + animation[anime_step++ % animation.length]);
					}
					else {
						String progressBar = "\r[";
						int plain = (int) (70 * (f.percent() / 100));
						int empty = 70 - plain;
						for(int i = 0; i < plain; i++){
							progressBar += "=";
						}
						for(int i = 0; i < empty; i++){
							progressBar += " ";
						}
						estimated = (long) timer.getRemainingEstimate();
						velocity = (long) timer.getVelocityRecordsPerSecond();
						progressBar += "] " + animation[anime_step++ % animation.length] + " " + df.format(per) + "% ("+f.getCount()+"/"+f.getTotal()+")";
						progressBar += String.format("(Estimated Remaining Processing Time %2d min, %2d sec - %2d R/s)",
								TimeUnit.MILLISECONDS.toMinutes(estimated),
								TimeUnit.MILLISECONDS.toSeconds(estimated) -
								TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(estimated)),
								velocity
							);
						System.out.print(progressBar);
					}

					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} while(f.percent() != 100 && running);
				System.out.println();

			});
	}
	
	

}
