package gov.nist.healthcare.iz.darq.digest.app;

import java.io.File;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

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
import gov.nist.healthcare.iz.darq.digest.service.impl.ParallelDigestRunner;

@Configuration
@ComponentScan("gov.nist.healthcare")
public class CLIApp {
	
	private static DecimalFormat df = new DecimalFormat(".##");

	
	private static boolean exists(CommandLine cmd, String opt, String name){
		if(!cmd.hasOption(opt)){
			System.out.println(name+" is required");
			return false;
		}
		return true;
	}
	
    public static String success(String x){
    	return ConsoleColors.GREEN_BOLD + x + ConsoleColors.RESET;
    }
    
    public static String failure(String x){
    	return ConsoleColors.RED_BOLD + x + ConsoleColors.RESET;
    }
	
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(CLIApp.class);
		//--- OPTIONS
		Options options = new Options();
		options.addOption("help", false, "print help");
		options.addOption("p", "patients", true, "Patients Extract File");
		options.addOption("v", "vaccinations", true, "Vaccinations Extract File");
		options.addOption("c", "configuration", true, "Analysis Configuration");
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("help")){
				HelpFormatter formater = new HelpFormatter();
				formater.printHelp("Data At Rest Quality Analysis Command Line Tool", options);
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
					
					
					System.out.println(ConsoleColors.BLUE_BRIGHT + "========================================================================" + ConsoleColors.RESET);
			    	System.out.println(ConsoleColors.WHITE_BOLD + "** [NIST] Welcome to Data At Rest Quality Analysis Command Line Tool **" + ConsoleColors.RESET);
			    	System.out.println(ConsoleColors.YELLOW_BRIGHT + "========================================================================" + ConsoleColors.RESET);
		    		
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
		        	System.out.println("========================================================================");
		        	
		        	if(pFile && vFile && cFile && cFileValid){
		        		ParallelDigestRunner runner = (ParallelDigestRunner) context.getBean(ParallelDigestRunner.class);
		        		Exporter export = (Exporter) context.getBean(Exporter.class);
		        		Long start = System.currentTimeMillis();
		        		Thread t = progress(runner, start);
		        		t.start();
		            	ADChunk chunk = runner.digest(configurationPayload, pFilePath, vFilePath);
		            	t.join();
		            	System.out.println(ConsoleColors.GREEN_BRIGHT + "Analysis Finished" + ConsoleColors.RESET);
		            	export.export(configurationPayload, chunk);
		            	
		            	
		        	}
				}
			}
			
			
			
		}
		catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}
	}
	
	public static Thread progress(DigestRunner runner, long start){
	    
	    	return new Thread(new Runnable() {
				
				@Override
				public void run() {
					String[] animation = { "|", "/", "—", "\\" };
					int anime_step = 0;
					Fraction f = new Fraction(0,0);
					double per;
					int estimated = 0;
					
					do{
						int save = f.getCount();
						f = runner.spy();
						int diff = f.getCount() - save;
						estimated = (estimated + (f.getTotal() - f.getCount()) * (diff / 200)) / 2;
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
					    	progressBar += String.format("(ETA %2d min, %2d sec)", 
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
			
					} while(f.percent() != 100);
					System.out.println();
					
				}
			});
	}
	
	

}
