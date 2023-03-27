package gov.nist.healthcare.iz.darq.digest.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.configuration.exception.InvalidConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.app.exception.*;
import gov.nist.healthcare.iz.darq.digest.service.impl.PublicOnlyCryptoKey;
import gov.nist.healthcare.iz.darq.digest.service.impl.SimpleDigestRunner;
import gov.nist.healthcare.iz.darq.digest.service.patient.matching.PatientMatchingService;
import gov.nist.healthcare.iz.darq.parser.type.DqDateFormat;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.lang3.StringUtils;
import org.immregistries.mqe.validator.detection.MqeCode;
import org.immregistries.mqe.validator.engine.MessageValidator;
import org.immregistries.mqe.validator.engine.RulePairBuilder;
import org.immregistries.mqe.validator.engine.ValidationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
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
			options.addOption("pa", "printAdf", false, "print ADF content");
			options.addOption("d", "dateFormat", true, "Date Format");
			options.addOption("pub", "publicKey", true, "qDAR Public Key");
			options.addOption("pm", "patientMatching", false, "Activate patient matching");


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

						// Read Configuration file
						ConfigurationPayload configurationPayload;
						try {
							ObjectMapper mapper = new ObjectMapper();
							configurationPayload = mapper.readValue(config,ConfigurationPayload.class);
						}
						catch (Exception e) {
							throw new InvalidConfigurationFileFormatException(e);
						}

						// Read Date Format
						DqDateFormat simpleDateFormat = DqDateFormat.forPattern(DEFAULT_DATE_FORMAT);
						if(!Strings.isNullOrEmpty(dateFormat)) {
							try{
								simpleDateFormat = DqDateFormat.forPattern(dateFormat);
							} catch (Exception e) {
								throw new InvalidDateFormatException(e, "Date Format " + dateFormat + " is Invalid ");
							}
						}

						// Read Public Key
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

						// Create Outputs Folder
						File output = new File("./darq-analysis/");
						output.mkdirs();

						System.out.println("Analysis Progress");

						SimpleDigestRunner runner = context.getBean(SimpleDigestRunner.class);
					  	PatientMatchingService matchingService = options.hasOption("pm") ? context.getBean(PatientMatchingService.class) : null;
						Exporter export = context.getBean(Exporter.class);
						configureMqeValidator(configurationPayload.getDetections());
						running = true;
						Thread t = progress(runner);
						t.start();
						long start = System.currentTimeMillis();
						ADChunk chunk = runner.digest(configurationPayload, pFilePath, vFilePath, simpleDateFormat, matchingService, output.toPath(), Optional.ofNullable(tmpDirLocation));
						t.join();
						System.out.println("Analysis Finished - Exporting Results");
						long elapsed = System.currentTimeMillis() - start;
						export.export(configurationPayload, output.toPath(),chunk, version, build, mqeVersion, elapsed, printAdf);
						System.out.println("Results Exported - END");
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
		finally {
			running = false;
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

	public static void configureMqeValidator(List<String> activeMqeCodes) {
		logger.info("Configuring MQE Validator");
		MessageValidator.INSTANCE.configure(
				activeMqeCodes.stream()
						.map((code) -> {
							try {
								return MqeCode.valueOf(code);
							} catch (Exception e) {
								return null;
							}
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toSet())
		);
		logger.info("MQE Validator Configured");
		Set<ValidationRule> rules = RulePairBuilder.INSTANCE.getActiveValidationRules().getRules();
		logger.info("MQE Active Rules (" + rules.size() + ") :");
		rules.forEach(
				(r) -> logger.info("* Active Rules : " + r.getClass())
		);
	}
	
	public static Thread progress(DigestRunner runner){
	    
	    	return new Thread(() -> {
				String[] animation = { "|", "/", "—", "\\" };
				int anime_step = 0;
				Fraction f = new Fraction(0,0);
				double per;
				long estimated;
				long stamp = System.currentTimeMillis();

				do{
					long elapsed = System.currentTimeMillis() - stamp;
					int save = f.getCount();
					f = runner.spy();
					stamp = System.currentTimeMillis();
					int diff = f.getCount() - save;
					int remaining = f.getTotal() - f.getCount();
					estimated = diff == 0 ? 0 : (elapsed / diff) * remaining;
					per = f.percent();
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
						progressBar += "] " + animation[anime_step++ % animation.length] + " " + df.format(per) + "% ("+f.getCount()+"/"+f.getTotal()+")";
						progressBar += String.format("(Estimated Remaining Processing Time %2d min, %2d sec)",
								TimeUnit.MILLISECONDS.toMinutes(estimated),
								TimeUnit.MILLISECONDS.toSeconds(estimated) -
								TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(estimated))
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
