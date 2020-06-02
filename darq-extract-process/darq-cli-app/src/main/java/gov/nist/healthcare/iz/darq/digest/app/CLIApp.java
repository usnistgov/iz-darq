package gov.nist.healthcare.iz.darq.digest.app;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import gov.nist.healthcare.iz.darq.digest.service.impl.SimpleDigestRunner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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

@Configuration
@ComponentScan("gov.nist.healthcare")
public class CLIApp {

	private static final DecimalFormat df = new DecimalFormat(".##");
	
	private static boolean exists(CommandLine cmd, String opt, String name){
		if(!cmd.hasOption(opt)){
			System.out.println(name+" is required");
			return false;
		}
		return true;
	}

	private static boolean running = false;
	
    public static String success(String x){
    	return ConsoleColors.GREEN_BOLD + x + ConsoleColors.RESET;
    }
    
    public static String failure(String x){
    	return ConsoleColors.RED_BOLD + x + ConsoleColors.RESET;
    }
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ApplicationContext context = new AnnotationConfigApplicationContext(CLIApp.class);
		Properties properties = new Properties();
		properties.load(CLIApp.class.getResourceAsStream("/application.properties"));
		String version = properties.getProperty("app.version");
		String build = properties.getProperty("app.date");
		String mqeVersion = properties.getProperty("mqe.version");
		String tag = String.format("v%s (%s) [MQE v%s]", version, build, mqeVersion);

		//--- OPTIONS
		Options options = new Options();
		options.addOption("help", false, "print help");
		options.addOption("p", "patients", true, "Patients Extract File");
		options.addOption("v", "vaccinations", true, "Vaccinations Extract File");
		options.addOption("c", "configuration", true, "Analysis Configuration");
		options.addOption("tmpDir", "temporaryDirectory", true, "Location where to create temporary directory");
		options.addOption("pa", "printAdf", false, "print ADF content");

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("help")){
				HelpFormatter formater = new HelpFormatter();
				formater.printHelp("Data At Rest Quality Analysis Command Line Tool "+ tag, options);
				System.exit(0);
			}
			else {
				if(!exists(cmd, "p", "patients file") | !exists(cmd, "v", "vaccinations file") | !exists(cmd, "c", "configuration file")){
					System.out.println("Use -help for details");
					System.exit(0);
				}
				else {
					String pFilePath = cmd.getOptionValue("p");
					String vFilePath = cmd.getOptionValue("v");
					String cFilePath = cmd.getOptionValue("c");
					String tmpDirLocation = cmd.getOptionValue("tmpDir");
					boolean printAdf = cmd.hasOption("pa");

					System.out.println(ConsoleColors.BLUE_BRIGHT + "===================================================================================================" + ConsoleColors.RESET);
			    	System.out.println(ConsoleColors.WHITE_BOLD + " [NIST] Welcome to Data At Rest Quality Analysis Command Line Tool " + tag + " " + ConsoleColors.RESET);
			    	System.out.println(ConsoleColors.YELLOW_BRIGHT + "===================================================================================================" + ConsoleColors.RESET);
		    		
			    	File patients = new File(pFilePath);
			    	File vaccines = new File(vFilePath);
			    	File config = new File(cFilePath);
			    	boolean pFile = patients.exists() && !patients.isDirectory();
			    	boolean vFile = vaccines.exists() && !vaccines.isDirectory();
			    	boolean cFile = config.exists() && !config.isDirectory();
			    	boolean cFileValid = true;
			    	ConfigurationPayload configurationPayload = null;
			    	if(cFile){
			    		try {
				    		ObjectMapper mapper = new ObjectMapper();
				    		configurationPayload = mapper.readValue(config,ConfigurationPayload.class);
				    	}
			    		catch (Exception e) {
							cFileValid = false;
						}
			    	}
			    	
			    	System.out.println("Patients File @ "+ pFilePath + " " + (pFile ? success("[FOUND]") : failure("[ERROR]")));
		        	System.out.println("Vaccinations File @ "+ vFilePath + " " + (vFile ? success("[FOUND]") : failure("[ERROR]")));
		        	System.out.println("Configuration File @ "+ cFilePath + " " + (cFile ? cFileValid ? success("[FOUND]") : failure("[INVALID]") : failure("[ERROR]")));
		        	System.out.println("===================================================================================================");
					System.out.println(ConsoleColors.YELLOW_BRIGHT + "Analysis Progress => " + ConsoleColors.RESET);

					if(pFile && vFile && cFile && cFileValid){
		        		SimpleDigestRunner runner = context.getBean(SimpleDigestRunner.class);
		        		Exporter export = context.getBean(Exporter.class);
		        		running = true;
		        		Thread t = progress(runner);
		        		t.start();
		            	ADChunk chunk = runner.digest(configurationPayload, pFilePath, vFilePath, Optional.ofNullable(tmpDirLocation));
		            	t.join();
		            	System.out.println(ConsoleColors.GREEN_BRIGHT + "Analysis Finished" + ConsoleColors.RESET);
		            	export.export(configurationPayload, chunk, version, build, mqeVersion, printAdf);
		            
		        	}
				}
			}
			
			
			
		}
		catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}
		catch (Exception exp) {
			System.err.println("Execution Failed due to exception ");
			exp.printStackTrace();
		}
		finally {
			running = false;
		}
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
