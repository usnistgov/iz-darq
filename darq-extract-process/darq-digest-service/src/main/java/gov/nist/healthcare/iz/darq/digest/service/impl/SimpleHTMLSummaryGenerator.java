package gov.nist.healthcare.iz.darq.digest.service.impl;

import static j2html.TagCreator.a;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h4;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.nav;
import static j2html.TagCreator.span;
import static j2html.TagCreator.strong;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.tr;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import gov.nist.healthcare.iz.darq.digest.domain.Summary;
import gov.nist.healthcare.iz.darq.digest.service.HTMLSummaryGenerator;
import j2html.tags.ContainerTag;

@Service
public class SimpleHTMLSummaryGenerator implements HTMLSummaryGenerator {

	@Override
	public void generateSummary(Summary summary, Map<String, String> providers, String path) throws IOException {
	    String html = html(
	    		head(
	    				link().withRel("stylesheet").withHref("./css/bootstrap.min.css"),
	    				link().withRel("stylesheet").withHref("./css/custom.css")
	    		),
	    		body(
	    				attrs(".back"),
	    	    		navBar(new LocalDate()),
	    	    		div(
	    	    				attrs(".content-area"),
	    	    				card(
	    	    						".half-width.space-right",
	    		    	    			"Summary Counts",
	    			    	    		table(
	    			    	    				attrs(".table.table-condensed.table-striped"),
	    			    	    				thead(
	    			    	    						tr(
	    			    	    								th("Label"), th("Count")
	    			    	    						)
	    			    	    				),
	    			    	    				tbody(
	    			        						tr(td("Total Read Patient Records"), td(summary.getCounts().totalReadPatientRecords+"")),
	    			        						tr(td("Total Skipped Patient Records Due To Errors"), td(summary.getCounts().totalSkippedPatientRecords+"")),
	    			        						tr(td("Total Read Vaccination Records"), td(summary.getCounts().totalReadVaccinations+"")),
	    			        						tr(td("Minimum Vaccinations per Patient"), td(summary.getCounts().minVaccinationsPerRecord+"")),
	    			        						tr(td("Maximum Vaccinations per Patient"), td(summary.getCounts().maxVaccinationsPerRecord+"")),
	    			        						tr(td("Average Vaccinations per Patient"), td(summary.getCounts().avgVaccinationsPerRecord+"")),
	    			        						tr(td("Total Number of Providers"), td(summary.getCounts().numberOfProviders+""))
	    			    	    				)
	    			    	    		)
	    			    	    ),
	    	    				card(
	    	    						".half-width.space-left",
	    		    	    			"Extraction Completeness",
	    			    	    		table(
	    			    	    				attrs(".table.table-condensed.table-striped"),
	    			    	    				thead(
	    			    	    						tr(
	    			    	    								th("Element"), th("Actual Value %")
	    			    	    						)
	    			    	    				),
	    			    	    				tbody(
	    			    	    						each(summary.getExtract().entrySet(), entry -> tr(
		    			        	    						td(entry.getKey()), td(entry.getValue()+" %")
		    			        	    				))
	    			    	    				)
	    			    	    		)
	    			    	    ),
	    	    				card(
	    	    						".half-width.space-right",
	    		    	    			"Providers",
	    			    	    		table(
	    			    	    				attrs(".table.table-condensed.table-striped"),
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
	    			    	    	),
    		    	    		card(
    		    	    				".half-width.space-left",
	    		    	    			"Issues",
	    		    	    			table(
	    		    	    					attrs(".table.table-condensed.table-striped"),
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
    		    	    				".full-width",
	    			    	    		"Age Groups",
	    			    	    		table(
	    			    	    				attrs(".table.table-condensed.table-striped"),
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
    			    	    	)
	    	    		)		
	    	    )
	    ).render();
	    
	    File summaryDir = new File(path+"css");
	    summaryDir.mkdirs();
	    
	    FileUtils.writeStringToFile(new File(path+"/index.html"), html);
	    FileUtils.copyInputStreamToFile(Exporter.class.getResourceAsStream("/bootstrap.min.css"), new File(path+"css/bootstrap.min.css"));
	    FileUtils.copyInputStreamToFile(Exporter.class.getResourceAsStream("/custom.css"), new File(path+"css/custom.css"));
	}
	
	ContainerTag navBar(LocalDate d){
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
								strong("   DARQ Command Line Tool Summary")
						)
				),
				span(
						attrs(".pull-right.date"),
						d.toString("MM/dd/yyyy")
				)
		);
	}
	
	ContainerTag card(String att, String title, ContainerTag content){
		return div(
				attrs(".card.space"+att),
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
	ContainerTag card(ContainerTag top, ContainerTag content){
		return div(
				attrs(".card.space"),
				div(
						attrs(".card-block.card-content-custom"),
						top,
						content
				)
		);
	}

}
