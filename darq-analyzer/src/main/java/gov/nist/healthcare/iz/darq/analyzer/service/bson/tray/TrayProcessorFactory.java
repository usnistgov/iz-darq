package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray;

import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;

public interface TrayProcessorFactory {
	
	public TrayProcessor create(AnalysisType group, Function<Tray, Action> guard);
	
}
