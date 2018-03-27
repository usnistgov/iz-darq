package gov.nist.healthcare.iz.darq.analyzer.service;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisResult;
import gov.nist.healthcare.iz.darq.analyzer.exception.IncompatibleFields;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

public interface Analyzer {

	AnalysisResult analyze(ADFile db, AnalysisQuery query) throws IncompatibleFields ;
	
}
