package gov.nist.healthcare.iz.darq.analyzer.service;

import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

public interface TrayProcessorFactory {
	
	public TrayProcessor create(_CG group, Function<Tray, Action> guard);
	
}
