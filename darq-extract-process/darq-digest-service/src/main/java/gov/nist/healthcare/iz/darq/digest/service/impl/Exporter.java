package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.detections.DetectionEngine;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gov.nist.healthcare.iz.darq.digest.service.ExportADChunk;
import gov.nist.healthcare.iz.darq.digest.service.HTMLSummaryGenerator;


@Service
public class Exporter implements ExportADChunk {

	@Autowired
	private HTMLSummaryGenerator summaryGenerator;
	@Autowired
	private DetectionEngine detectionEngine;

	Set<String> getInactiveDetections(List<String> fromConfig) {
		Set<String> active = detectionEngine.getActiveDetectionCodes();
		return fromConfig.stream().filter((code) -> !active.contains(code)).collect(Collectors.toSet());
	}
	
	
	@Override
	public void export(ConfigurationPayload payload, Path folder, ADFWriter writer, String version, String build, String mqeVersion, long elapsed, boolean printAdf) throws Exception {
		Metadata metadata = new Metadata(
				version,
				build,
				mqeVersion,
				elapsed,
				new Date(),
				getInactiveDetections(payload.getDetections())
		);

		Summary summary = new Summary(
				writer.getAgeGroupCount(),
				writer.getSummaryCounts(),
				writer.getExtractPercent(),
				payload
		);

		writer.write_metadata(metadata, payload);
		writer.setSummary(summary);

	    //---- HTML
	    summaryGenerator.generateSummary(writer, metadata, summary, writer.getIssues(), writer.getProviders(), Paths.get(folder.toAbsolutePath().toString(), "summary").toAbsolutePath().toString(), printAdf);

	    //--- Reporting Group Spreadsheet
		writeProvidersToCSV(writer.getProviders(), new FileWriter(Paths.get(folder.toAbsolutePath().toString(), "reporting-groups.csv").toFile()));

		//---- Write ADF
		writer.exportAndClose(Paths.get(folder.toAbsolutePath().toString(), "ADF.data").toAbsolutePath().toString());
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
