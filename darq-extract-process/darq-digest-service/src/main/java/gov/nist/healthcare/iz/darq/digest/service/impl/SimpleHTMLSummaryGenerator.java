package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import gov.nist.healthcare.iz.darq.digest.domain.*;
import j2html.tags.DomContent;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import gov.nist.healthcare.iz.darq.digest.service.HTMLSummaryGenerator;
import j2html.tags.ContainerTag;

import static j2html.TagCreator.*;

@Service
public class SimpleHTMLSummaryGenerator implements HTMLSummaryGenerator {

	@Override
	public void generateSummary(ADFile file, Summary summary, Map<String, String> providers, String path) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	    String html = html(
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
	    		    	    			"Issues",
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
								),
								card("", "Aggregate Detections File Content", pre(mapper.writeValueAsString(file)))
	    	    		)		
	    	    )
	    ).render();
	    
	    File summaryDir = new File(path+"css");
	    summaryDir.mkdirs();
	    
	    FileUtils.writeStringToFile(new File(path+"/index.html"), html);
	    FileUtils.copyInputStreamToFile(Exporter.class.getResourceAsStream("/bootstrap.min.css"), new File(path+"css/bootstrap.min.css"));
	    FileUtils.copyInputStreamToFile(Exporter.class.getResourceAsStream("/custom.css"), new File(path+"css/custom.css"));
	}

//	@Override
//	public void generateADFView(Map<String, Map<String, VaccinationPayload>> vaccinationSection, Map<String, PatientPayload> patientSection, String path) throws IOException {
//		String html = html(
//				head(
//						link().withRel("stylesheet").withHref("./css/bootstrap.min.css"),
//						link().withRel("stylesheet").withHref("./css/custom.css")
//				),
//				body(
//						adfTable(vaccinationSection, patientSection)
//				)
//		).render();
//		FileUtils.writeStringToFile(new File(path+"/adf.html"), html);
//	}

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

	private ContainerTag adfTable(Map<String, Map<String, VaccinationPayload>> vaccinationSection, Map<String, PatientPayload> patientSection) {
		return div(
				table(
						attrs(".table.table-sm.table-bordered"),
						tbody(
								tr(
										td("Patient Section").withClass("header-section").attr("colspan", 4)
								),
								tr(
										td("Age Group").withClass("header-normal"),
										td("Values").withClass("header-normal").attr("colspan", 3)
								),
								each(patientSection.entrySet(), entry -> patientTable(entry.getKey(), entry.getValue()))
						)
				),
				table(
						attrs(".table.table-sm.table-bordered"),
						tbody(
								tr(
										td("Vaccination Section").withClass("header-section").attr("colspan", 7)
								),
								tr(
										td("Provider").withClass("header-normal"),
										td("Age Groups").withClass("header-normal"),
										td("Values").withClass("header-normal").attr("colspan", 5)
								),
								each(vaccinationSection.entrySet(), entry -> vaccinationTableProvider(entry.getKey(), entry.getValue()))
						)
				)
		);
	}

	private ContainerTag vaccinationTableProvider(String provider, Map<String, VaccinationPayload> vaxAgeGroups) {
		int ageGroups = vaxAgeGroups.size();
		int providerSize = vaxAgeGroups.values().stream().mapToInt((entry) -> {
			int vxSize = entry.getVaccinations().values().stream().mapToInt((vx) -> {
				return vx.values().stream().mapToInt((year) -> {
					return year.values().stream().mapToInt((gender) -> {
						return gender.values().size() + 1;
					}).sum();
				}).sum();
			}).sum();
			return entry.getDetection().size() + (2 * ageGroups) + entry.getCodeTable().values().stream().mapToInt(x -> x.getCodes().size()).sum() + (2 * ageGroups) + vxSize + (2 * ageGroups);
		}).sum();

		return tbody(
				tr(
						td(provider).withClass("age-group").attr("rowspan", providerSize + 6)
				),
				each(vaxAgeGroups.entrySet(), entry ->
						each(vaccinationTableAgeGrp(entry.getKey(), entry.getValue()), (x) -> x)
				)
		);
	}

	private List<DomContent> vaccinationTableAgeGrp(String ageGroup, VaccinationPayload payload) {
		int ageGroupSize = payload.getVaccinations().values().stream().mapToInt((vx) -> {
			return vx.values().stream().mapToInt((year) -> {
				return year.values().stream().mapToInt((gender) -> {
					return gender.values().size() + 1;
				}).sum();
			}).sum();
		}).sum() + payload.getDetection().size() + 1 + payload.getCodeTable().values().stream().mapToInt(x -> x.getCodes().size() + 1).sum();

		return Arrays.asList(
				tr(
						td(ageGroup).withClass("age-group").attr("rowspan", ageGroupSize + 4)
				),
				tr(
						td("Detections").withClass("table-head-1").attr("colspan", 5)
				),
				tr(
						attrs(".table-head-2"),
						td("MQE Code").attr("colspan", 3),
						td("Positive"),
						td("Negative")
				),
				each(payload.getDetection().entrySet(), entry -> tr(
						td(entry.getKey()).attr("colspan", 3),
						td(entry.getValue().getPositive()+""),
						td(entry.getValue().getNegative()+"")
				)),
				tr(
						td("Codes").withClass("table-head-1").attr("colspan", 5)
				),
				tr(
						attrs(".table-head-2"),
						td("Table").attr("colspan", 3),
						td("Code"),
						td("Count")
				),
				each(payload.getCodeTable().entrySet(), entry -> {
					return each(codeTable(entry.getKey(), entry.getValue(), 3), elm -> elm);
				})
		);
	}

	private ContainerTag patientTable(String ageGroup, PatientPayload payload) {
		int ageGroupSize = payload.getDetection().size() + 1 + payload.getCodeTable().values().stream().mapToInt(x -> x.getCodes().size() + 1).sum();
		return tbody(
			tr(
					td(ageGroup).withClass("age-group").attr("rowspan", ageGroupSize + 4)
			),
			tr(
					td("Detections").withClass("table-head-1").attr("colspan", 3)
			),
			tr(
					attrs(".table-head-2"),
					td("MQE Code"),
					td("Positive"),
					td("Negative")
			),
			each(payload.getDetection().entrySet(), entry -> tr(
					td(entry.getKey()),
					td(entry.getValue().getPositive()+""),
					td(entry.getValue().getNegative()+"")
			)),
			tr(
					td("Codes").withClass("table-head-1").attr("colspan", 3)
			),
			tr(
					attrs(".table-head-2"),
					td("Table"),
					td("Code"),
					td("Count")
			),
			each(payload.getCodeTable().entrySet(), entry -> {
				return each(codeTable(entry.getKey(), entry.getValue(), 1), elm -> elm);
			})
		);
	}

	private List<DomContent> codeTable(String table, TablePayload payload, int col) {
		int tablePayloadSize = payload.getCodes().size() + 1;
		return Arrays.asList(
			tr(
					td(table).withClass("table-cell").attr("rowspan", tablePayloadSize).attr("colspan", col)
			),
			each(payload.getCodes().entrySet(), entry -> tr(
					td(entry.getKey()),
					td(entry.getValue()+"")
			))
		);
	}

}
