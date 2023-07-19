package gov.nist.healthcare.iz.darq.analyzer.service;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

	@Autowired
	List<ADFReportService> handlers;

	public <T extends ADFReader> DataTable singleQuery(T file, QueryPayload payload, String facilityId) throws Exception {
		ADFReportService<T> reportService = getHandlerFor(file);
		return reportService.singleQuery(file, payload, facilityId);
	}

	public <T extends ADFReader> AnalysisReport analyse(T file, ReportTemplate template, String facilityId) throws Exception {
		ADFReportService<T> reportService = getHandlerFor(file);
		return reportService.analyse(file, template, facilityId);
	}

	private <T extends ADFReader> ADFReportService<T> getHandlerFor(ADFReader file) throws Exception {
		return handlers.stream().filter((handler) -> handler.getVersion().equals(file.getVersion())).findFirst()
				.orElseThrow(() -> new Exception("No report handler for ADF version " + file.getVersion()));
	}

}
