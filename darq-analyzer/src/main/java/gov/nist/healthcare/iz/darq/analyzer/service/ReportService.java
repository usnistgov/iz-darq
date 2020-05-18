package gov.nist.healthcare.iz.darq.analyzer.service;

import gov.nist.healthcare.iz.darq.analyzer.exception.IncompatibleFields;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.template.DataViewQuery;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ReportTemplate;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

public interface ReportService {

	DataTable singleQuery(ADFile file, DataViewQuery payload);

	AnalysisReport analyse(ADFile file, ReportTemplate template) throws IncompatibleFields;
	
}
