package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.FieldValue;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery.QueryField;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisSectionResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisSectionResult.AnalysisPayloadResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.ReportSection;
import gov.nist.healthcare.iz.darq.analyzer.domain.ReportTemplate;
import gov.nist.healthcare.iz.darq.analyzer.exception.IncompatibleFields;
import gov.nist.healthcare.iz.darq.analyzer.service.ReportService;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayAggregator;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

@Service
public class SimpleReportService implements ReportService {

	@Autowired
	TrayProcessorFactory factory;
	@Autowired
	private TrayAggregator aggregator;
	
	@Override
	public AnalysisReport analyse(ADFile file, ReportTemplate template) throws IncompatibleFields {
		ObjectMapper mapper = new ObjectMapper();
		AnalysisReport result = new AnalysisReport();
		result.setName(template.getName());
		result.setDescription(template.getDescription());
		
		for(ReportSection sectionTemplate : template.getSections()){
			AnalysisSectionResult section = new AnalysisSectionResult();
			section.setTitle(sectionTemplate.getTitle());
			section.setDescription(sectionTemplate.getDescription());
			
			for(AnalysisPayload payload : sectionTemplate.getPayloads()){
				AnalysisQuery query = queryFromPayload(payload);
				TrayProcessor processor = factory.create(query.getCompatibilityGroup(), query::take);
				AnalysisPayloadResult pR = aggregator.aggregate(processor.process(file), payload);
				section.getResults().add(pR);
			}
			result.getSections().add(section);
		}
		
		result.setConfiguration(template.getConfiguration());
		return result;
	}
	
	AnalysisQuery queryFromPayload(AnalysisPayload payload){
		Set<QueryField> fields = new HashSet<>();
		for(FieldValue fv : payload.getFilters()){
			fields.add(new QueryField(fv.getField(), fv.getValue()));
		}
		for(Field grp : payload.getGroupBy()){
			fields.add(new QueryField(grp));
		}
		return new AnalysisQuery(fields, payload.getType());
	}

}
