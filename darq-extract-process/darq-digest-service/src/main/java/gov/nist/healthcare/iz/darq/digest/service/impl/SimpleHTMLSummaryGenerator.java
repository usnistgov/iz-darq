package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
	public void generateSummary(ADFile file, Summary summary, Map<String, String> providers, String path, boolean printAdf) throws IOException {
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
										"Providers",
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
		File summaryDir = new File(path+"css");
		summaryDir.mkdirs();

		// HTML File Writer
		FileWriter htmlFileWriter = new FileWriter(new File(path+"/index.html"));
		html.render(htmlFileWriter);
		htmlFileWriter.flush();
		htmlFileWriter.close();

		if(printAdf) {
			// ADF File Writer
			FileWriter adfFileWriter = new FileWriter(new File(path+"/plain_adf_content.json"));
			mapper.writeValue(adfFileWriter, file);
			adfFileWriter.close();
		}


		// Write CSS
	    FileUtils.copyInputStreamToFile(Exporter.class.getResourceAsStream("/bootstrap.min.css"), new File(path+"css/bootstrap.min.css"));
	    FileUtils.copyInputStreamToFile(Exporter.class.getResourceAsStream("/custom.css"), new File(path+"css/custom.css"));
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


}
