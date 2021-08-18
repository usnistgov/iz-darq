package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.function.Function;

import gov.nist.healthcare.iz.darq.analyzer.service.tray.*;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;

@Service
public class TrayProcessorFactory implements gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessorFactory {

	@Override
	public TrayProcessor create(_CG group, Function<Tray, Action> guard) {
		switch(group){
			case V : return new VaxTrayProcessor(guard);
			case VT : return new VaxCodeTrayProcessor(guard);
			case VD : return new VaxDetectionTrayProcessor(guard);
			case PT : return new PatCodeTrayProcessor(guard);
			case PD : return new PatDetectionTrayProcessor(guard);
			case PD_RG: return new PatRgDetectionTrayProcessor(guard);
			case PT_RG: return new PatRgCodeTrayProcessor(guard);
		}
		return null;
	}

}
