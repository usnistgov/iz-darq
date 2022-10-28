package gov.nist.healthcare.iz.darq.digest.service.impl;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.engine.rules.ValidationRuleEntityLists;
import org.immregistries.mqe.vxu.TargetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.ConfigurationPayload;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import gov.nist.healthcare.iz.darq.digest.service.ExportADChunk;
import gov.nist.healthcare.iz.darq.digest.service.HTMLSummaryGenerator;


@Service
public class Exporter implements ExportADChunk {

	@Autowired
	private CryptoUtils cryptoUtils;
	@Autowired
	private HTMLSummaryGenerator summaryGenerator;

	Set<String> getInactiveDetections(List<String> fromConfig) {
		Set<String> active = ValidationRuleEntityLists.activeDetectionsForTargets(new HashSet<>(Arrays.asList(
				TargetType.Patient,
				TargetType.NextOfKin,
				TargetType.Vaccination
		))).stream().map(Detection::getMqeMqeCode).collect(Collectors.toSet());
		return fromConfig.stream().filter((code) -> !active.contains(code)).collect(Collectors.toSet());
	}
	
	
	@Override
	public void export(ConfigurationPayload payload, ADChunk chunk, String version, String build, String mqeVersion, long elapsed, boolean printAdf) throws Exception {
		Summary summary = new Summary(chunk, payload);
		ADFile file = new ADFile(
				chunk.getGeneralPatientPayload(),
				chunk.getReportingGroupPayload(),
				payload,
				summary,
				version,
				build,
				mqeVersion,
				this.getInactiveDetections(payload.getDetections()),
				elapsed,
				chunk.getHistorical(),
				chunk.getAdministered()
		);
		
		File output = new File("./darq-analysis/");
	    output.mkdirs();
	    
	    //---- ENCRYPT and write ADF
	    this.cryptoUtils.encryptContentToFileWithoutTemporaryFile(file, Files.newOutputStream(new File("./darq-analysis/ADF.data").toPath()));

	    //---- HTML
	    summaryGenerator.generateSummary(file, summary, chunk.getProviders(), "./darq-analysis/summary/", printAdf);

	    //--- Reporting Group Spreadsheet
		writeProvidersToCSV(chunk.getProviders(), new FileWriter(new File("./darq-analysis/reporting-groups.csv")));
	}

	void writeProvidersToCSV(Map<String, String> providers, FileWriter writer) throws IOException {
		writer.write("Reporting Group, Coded Value (MD5 HASH)\n");
		providers.forEach((key, value) -> {
			try {
				writer.append(key);
				writer.append(", ");
				writer.append(value);
				writer.append('\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		writer.flush();
		writer.close();
	}

}
