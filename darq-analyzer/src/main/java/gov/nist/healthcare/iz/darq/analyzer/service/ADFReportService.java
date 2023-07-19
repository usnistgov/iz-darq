package gov.nist.healthcare.iz.darq.analyzer.service;

import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.ReportSectionResult;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportSection;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;

import java.util.ArrayList;
import java.util.List;

public abstract class ADFReportService<T extends ADFReader> {

	public abstract DataTable singleQuery(T file, QueryPayload payload, String facilityId) throws Exception;

	public abstract ADFVersion getVersion();

	public AnalysisReport analyse(T file, ReportTemplate template, String facilityId) throws Exception {
		try {
			AnalysisReport result = new AnalysisReport();
			result.setName(template.getName());
			result.setDescription(template.getDescription());
			result.setSections(this.analyse(file, template.getSections(), facilityId));
			result.setConfiguration(template.getConfiguration());
			result.setCustomDetectionLabels(template.getCustomDetectionLabels());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private List<ReportSectionResult> analyse(T file, List<? extends ReportSection> sections, String facilityId) throws Exception {
		if(sections != null) {
			List<ReportSectionResult> result = new ArrayList<>();
			for(ReportSection sectionTemplate : sections) {
				ReportSectionResult section = new ReportSectionResult();
				section.fromSection(sectionTemplate);

				for (QueryPayload payload : sectionTemplate.getData()) {
					DataTable table = this.singleQuery(file, payload, facilityId);
					if(table.isThresholdViolation()) {
						section.setThresholdViolation(true);
					}
					section.getData().add(table);
				}

				section.setChildren(this.analyse(file, sectionTemplate.getChildren(), facilityId));
				result.add(section);
			}
			return result;
		}
		return new ArrayList<>();
	}
	
}
