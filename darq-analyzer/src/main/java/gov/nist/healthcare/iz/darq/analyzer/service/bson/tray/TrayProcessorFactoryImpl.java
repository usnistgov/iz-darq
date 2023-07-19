package gov.nist.healthcare.iz.darq.analyzer.service.bson.tray;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.AnalysisQuery.Action;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;

@Service
public class TrayProcessorFactoryImpl implements TrayProcessorFactory {

	@Override
	public TrayProcessor create(AnalysisType group, Function<Tray, Action> guard) {
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
