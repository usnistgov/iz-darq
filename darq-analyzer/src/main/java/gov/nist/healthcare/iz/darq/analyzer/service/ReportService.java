package gov.nist.healthcare.iz.darq.analyzer.service;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.domain.ReportTemplate;
import gov.nist.healthcare.iz.darq.analyzer.exception.IncompatibleFields;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

public interface ReportService {

	AnalysisReport analyse(ADFile file, ReportTemplate template) throws IncompatibleFields;
	
}
