package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import gov.nist.healthcare.iz.darq.digest.service.HTMLSummaryGenerator;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

@Service
public class SimpleHTMLSummaryGenerator implements HTMLSummaryGenerator {

	@Override
	public void generateSummary(ADFWriter writer, Metadata metadata, Summary summary, Map<String, String> providers, String path, boolean printAdf) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	    ContainerTag html = html(
	    		head(
	    				link().withRel("stylesheet").withHref("./css/bootstrap.min.css"),
	    				link().withRel("stylesheet").withHref("./css/custom.css")
	    		),
	    		body(
	    				attrs(".back"),
	    	    		navBar(summary.getAsOfDate() , new LocalDate()),
	    	    		div(
	    	    				attrs(".content-area"),
								card("",
										"Metadata",
										table(
												attrs(".table.table-sm.table-striped"),
												tbody(
														tr(th("CLI Version"), td(metadata.getVersion())),
														tr(th("CLI Build Date"), td(metadata.getBuild())),
														tr(th("MQE Version"), td(metadata.getMqeVersion())),
														tr(th("Total Analysis Time"), td(humanReadableTime(metadata.getTotalAnalysisTime())))
												)
										)
								),
	    	    				card("",
	    		    	    			"Summary Counts",
	    			    	    		table(
	    			    	    				attrs(".table.table-sm.table-striped"),
	    			    	    				thead(
	    			    	    						tr(
	    			    	    								th("Label"), th("Count")
	    			    	    						)
	    			    	    				),
	    			    	    				tbody(
	    			        						tr(td("Total Read Patient Records"), td(summary.getCounts().totalReadPatientRecords+"")),
	    			        						tr(td("Total Skipped Patient Records Due To Errors"), td(summary.getCounts().totalSkippedPatientRecords+"")),
													tr(td("Total Skipped Vaccination Records Due To Errors"), td(summary.getCounts().totalSkippedVaccinationRecords+"")),
	    			        						tr(td("Total Read Vaccination Records"), td(summary.getCounts().totalReadVaccinations+"")),
	    			        						tr(td("Minimum Vaccinations per Patient"), td(summary.getCounts().minVaccinationsPerRecord+"")),
	    			        						tr(td("Maximum Vaccinations per Patient"), td(summary.getCounts().maxVaccinationsPerRecord+"")),
	    			        						tr(td("Average Vaccinations per Patient"), td(summary.getCounts().avgVaccinationsPerRecord+"")),
													tr(td("Total Historical Vaccinations"), td(summary.getCounts().historical+"")),
													tr(td("Total Administered Vaccinations"), td(summary.getCounts().administered+"")),
	    			        						tr(td("Total Number of Providers"), td(summary.getCounts().numberOfProviders+""))
	    			    	    				)
	    			    	    		)
	    			    	    ),
    		    	    		card(
    		    	    				"",
	    		    	    			"Issues (maximum output " + ADChunk.MAX_ISSUES + ")",
	    		    	    			table(
	    		    	    					attrs(".table.table-sm.table-striped"),
	    			    	    				thead(
	    			    	    						tr(
	    			    	    								th("Description")
	    			    	    						)
	    			    	    				),
	    			    	    				tbody(
	    			        						each(summary.getIssues(), x -> tr(
	    			        	    						td(x)
	    			        	    				))
	    			    	    				)
	    			    	    		)
    		    	    		),
    		    	    		card(
    		    	    				"",
	    			    	    		"Age Groups",
	    			    	    		table(
	    			    	    				attrs(".table.table-sm.table-striped"),
	    			    	    				thead(
	    			    	    						tr(
	    			    	    								th("Age Group"), th("Count")
	    			    	    						)
	    			    	    				),
	    			    	    				tbody(
	    			        						each(summary.getCountByAgeGroup(), cag -> tr(
	    			        	    						td(cag.getRange().toString()), td(cag.getNb()+"")
	    			        	    				)),
	    			        						tr(
	    			        								td("+"), td(summary.getOutOfRange()+"")
	    			        						)
	    			    	    				)
	    			    	    		)
    			    	    	),
								card(
										"",
										"Extraction Completeness",
										table(
												attrs(".table.table-sm.table-striped"),
												thead(
														tr(
																th("Element"),
																th("Valued %"),
																th("Excluded %"),
																th("Not Collected %"),
																th("Not Extracted %"),
																th("Value Present %"),
																th("Value Not Present %"),
																th("Value Length %"),
																th("Empty %")
														)
												),
												tbody(
														each(summary.getExtract().entrySet(), entry -> tr(
																td(entry.getKey()),
																td(String.format("%.2f", entry.getValue().getValued())+" %").withClass(entry.getValue().getValued() > 0 ? "bold" : ""),
																td(String.format("%.2f",entry.getValue().getExcluded())+" %").withClass(entry.getValue().getExcluded() > 0 ? "bold" : ""),
																td(String.format("%.2f",entry.getValue().getNotCollected())+" %").withClass(entry.getValue().getNotCollected() > 0 ? "bold" : ""),
																td(String.format("%.2f",entry.getValue().getNotExtracted())+" %").withClass(entry.getValue().getNotExtracted() > 0 ? "bold" : ""),
																td(String.format("%.2f",entry.getValue().getValuePresent())+" %").withClass(entry.getValue().getValuePresent() > 0 ? "bold" : ""),
																td(String.format("%.2f",entry.getValue().getValueNotPresent())+" %").withClass(entry.getValue().getValueNotPresent() > 0 ? "bold" : ""),
																td(String.format("%.2f",entry.getValue().getValueLength())+" %").withClass(entry.getValue().getValueLength() > 0 ? "bold" : ""),
																td(String.format("%.2f",entry.getValue().getEmpty())+" %").withClass(entry.getValue().getEmpty() > 0 ? "bold" : "")
														))
												)
										)
								),
								card(
										"",
										"Reporting Groups",
										table(
												attrs(".table.table-sm.table-striped"),
												thead(
														tr(
																th("Value"), th("Hash")
														)
												),
												tbody(
														each(providers.entrySet(), entry -> tr(
																td(entry.getKey()), td(entry.getValue())
														))
												)
										)
								)
	    	    		)
	    	    )
	    );

	    // Create CSS Directory
		File summaryDir = Paths.get(path, "css").toFile();
		summaryDir.mkdirs();

		// HTML File Writer
		FileWriter htmlFileWriter = new FileWriter(path+"/index.html");
		html.render(htmlFileWriter);
		htmlFileWriter.flush();
		htmlFileWriter.close();

		if(printAdf && writer.supportsPrint()) {
			// ADF File Writer
			FileWriter adfFileWriter = new FileWriter(new File(path+"/plain_adf_content.json"));
			adfFileWriter.write(writer.getAsString());
			adfFileWriter.close();
		}


		// Write CSS
	    FileUtils.copyInputStreamToFile(Exporter.class.getResourceAsStream("/bootstrap.min.css"), Paths.get(path, "css", "bootstrap.min.css").toFile());
	    FileUtils.copyInputStreamToFile(Exporter.class.getResourceAsStream("/custom.css"), Paths.get(path, "css", "custom.css").toFile());
	}


	private ContainerTag navBar(String date, LocalDate d){
		String today = d.toString("MM/dd/yyyy");
		return nav(
				attrs(".navbar.fixed-top.navbar-dark.nav-back"),
				a(
						attrs(".navbar-brand"),
						span(
								attrs(".nist"),
								"NIST"
						),
						span(
								attrs(".sub"),
								strong("   Data Quality Detection Summary")
						)
				),
				div(
						span(
								attrs(".pull-right.date"),
								"Run on "+today
						),
						span(
								attrs(".pull-right.date"),
								" | As "+(date != null ? date : today)
						)
				)
		);
	}
	
	private ContainerTag card(String att, String title, ContainerTag content){
		return div(
				attrs(".card.space"+ att),
				div(
						attrs(".card-block.card-content-custom"),
						h4(
								attrs(".card-title"),
								title
						),
						content
				)
		);
	}

	private String humanReadableTime(long time) {
		if(time == 0) {
			return "N/A";
		}
		try {
			long hours = TimeUnit.MILLISECONDS.toHours(time);
			long minutes = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.HOURS.toMillis(hours));
			long seconds = TimeUnit.MILLISECONDS.toSeconds(time - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
			long milliseconds = time - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds);

			return String.format("%02d hours %02d minutes %02d seconds %d milliseconds", hours, minutes, seconds, milliseconds);
		} catch (Exception exception) {
			return "N/A";
		}
	}


}
