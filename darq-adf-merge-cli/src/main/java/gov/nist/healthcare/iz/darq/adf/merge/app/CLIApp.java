package gov.nist.healthcare.iz.darq.adf.merge.app;

import com.google.common.base.Strings;
import gov.nist.healthcare.crypto.service.CryptoKey;
import gov.nist.healthcare.iz.darq.adf.merge.model.LoadableCryptoKey;
import gov.nist.healthcare.iz.darq.adf.service.ADFMergeService;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import org.apache.commons.cli.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

@Configuration
@ComponentScan("gov.nist.healthcare")
public class CLIApp {

    private static boolean exists(CommandLine cmd, String opt, String name){
        if(!cmd.hasOption(opt)){
            System.out.println(name+" is required");
            return false;
        }
        return true;
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(CLIApp.class);
        CryptoKey cryptoKey = context.getBean(CryptoKey.class);
        ADFMergeService mergeService = context.getBean(ADFMergeService.class);
        CryptoUtils utils = context.getBean(CryptoUtils.class);
        String jks = context.getEnvironment().getProperty("QDAR_ADF_JKS_PATH");
        String jksPassword = context.getEnvironment().getProperty("QDAR_ADF_JKS_PASSWORD");
        String keyAlias = context.getEnvironment().getProperty("QDAR_ADF_KEY_ALIAS");
        String keyPassword = context.getEnvironment().getProperty("QDAR_ADF_KEY_PASSWORD");

        String publicKeyHash = "";

        if(cryptoKey instanceof LoadableCryptoKey) {
            try {
                if(!Strings.isNullOrEmpty(jks)) {
                    ((LoadableCryptoKey) cryptoKey).load(jks, keyAlias, jksPassword, keyPassword);
                    publicKeyHash = DatatypeConverter.printHexBinary(cryptoKey.getPublicKeyHash());
                }
            } catch (Exception e) {
                System.out.println("! Error While Reading Key Pair :");
                e.printStackTrace();
                System.exit(0);
            }
        }

        //--- OPTIONS
        Options options = new Options();
        options.addOption("help", false, "print help");
        options.addOption("f", "files", true, "Column separated file paths");
        options.addOption("d", "destination", true, "File Destination");


        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if(cmd.hasOption("help")){
                HelpFormatter formater = new HelpFormatter();
                formater.printHelp("Data At Rest Quality ADF Merge Tool", options);
                System.exit(0);
            }
            else {
                if(!exists(cmd, "f", "files")){
                    System.out.println("Use -help for details");
                    System.exit(0);
                }
                else {
                    String filePaths = cmd.getOptionValue("f");
                    String destination = cmd.hasOption("d") ? cmd.getOptionValue("d") : "./MERGED_ADF.data";
                    System.out.println("===================================================================================================");
                    System.out.println(" [NIST] Welcome to Data At Rest Quality ADF Merge Tool [PK: "+ publicKeyHash + "]");
                    System.out.println("===================================================================================================");

                    String[] filePathArray = filePaths.split(":");
                    List<File> files = new ArrayList<>();
                    boolean overallStatus = true;
                    for(String path: filePathArray) {
                        File fs = new File(path);
                        boolean status = fs.exists() && !fs.isDirectory();
                        System.out.println("File : " + path + " " + (status ? "[FOUND]" : "[NOT_FOUND]"));
                        files.add(fs);
                        overallStatus = status && overallStatus;
                    }

                    if(!overallStatus) {
                        System.out.println("One ore more files not found");
                        System.exit(0);
                    } else if(files.size() < 2) {
                        System.out.println("Please select multiple files PATH_FILE_1:PATH_FILE_2:...");
                        System.exit(0);
                    }
                    System.out.println("===================================================================================================");
                    System.out.println(" - Merging Files, this may take a while ");
                    File first = files.get(0);
                    ADFile A = utils.decryptFile(new FileInputStream(first));
                    for(int i = 1; i < files.size(); i++) {
                        A = merge(A, files.get(i), mergeService, utils);
                    }
                    System.out.println(" - Finished Merging Files, Writing result to destination : " + destination);
                    utils.encryptContentToFile(A, new FileOutputStream(new File(destination)));
                    System.out.println(" - ADF Files Merged!");
                    System.out.println("===================================================================================================");
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
    }

    private static ADFile merge(ADFile a, File b, ADFMergeService mergeService, CryptoUtils utils) throws Exception {
        return mergeService.mergeADFiles(Arrays.asList(a, utils.decryptFile(new FileInputStream(b))));
    }
}
