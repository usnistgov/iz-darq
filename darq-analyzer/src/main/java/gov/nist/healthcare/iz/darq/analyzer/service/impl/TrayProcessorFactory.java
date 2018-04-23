package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.domain.Tray;
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
		}
		return null;
	}

}
