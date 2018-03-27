package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;
import gov.nist.healthcare.iz.darq.analyzer.exception.IncompatibleFields;
import gov.nist.healthcare.iz.darq.analyzer.service.Analyzer;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

@Service
public class DefaultAnalyzer implements Analyzer {

	@Autowired
	TrayProcessorFactory factory;
	
	@Override
	public AnalysisResult analyze(ADFile db, AnalysisQuery query) throws IncompatibleFields {
		TrayProcessor processor = factory.create(query.getCompatibilityGroup(), query::take);
		List<Tray> trays = processor.process(db);
		AnalysisResult ar = new AnalysisResult(query.getFields());
		ar.addTrays(trays);
		return ar;
	}

}
