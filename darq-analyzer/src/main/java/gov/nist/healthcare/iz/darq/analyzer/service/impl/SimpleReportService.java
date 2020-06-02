package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.*;
import gov.nist.healthcare.iz.darq.analyzer.model.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.service.ReportService;
import gov.nist.healthcare.iz.darq.analyzer.service.DataTableService;
import gov.nist.healthcare.iz.darq.analyzer.service.TrayProcessor;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

@Service
public class SimpleReportService implements ReportService {

	@Autowired
	TrayProcessorFactory factory;
	@Autowired
	private DataTableService tableService;

	@Override
	public DataTable singleQuery(ADFile file, DataViewQuery payload) {
		AnalysisQuery query = queryFromPayload(payload);
		QueryIssues issues = sanitizeQuery(file, query);
		TrayProcessor processor = factory.create(query.getCompatibilityGroup(), query::take);
		DataTable table = tableService.createTable(processor.process(file), payload);
		table.setIssues(issues);
		return table;
	}

	QueryIssues sanitizeQuery(ADFile file, AnalysisQuery query) {
		Set<String> inactive = null;

		// Check for inative detections
		if(file.getInactiveDetections() != null && file.getInactiveDetections().size() > 0) {
			QueryField f = query.get(Field.DETECTION);
			if(f != null) {
				inactive = f.getValues().stream().filter((d) -> file.getInactiveDetections().contains(d)).collect(Collectors.toSet());
				f.getValues().removeAll(inactive);
			}
		}

		if(inactive != null && inactive.size() > 0) {
			QueryIssues issues = new QueryIssues();
			issues.setInactiveDetections(inactive);
			return issues;
		}

		return null;
	}
	
	@Override
	public AnalysisReport analyse(ADFile file, ReportTemplate template) {
		AnalysisReport result = new AnalysisReport();
		result.setName(template.getName());
		result.setDescription(template.getDescription());
		result.setSections(this.analyse(file, template.getSections()));
		result.setConfiguration(template.getConfiguration());
		return result;
	}

	public List<ReportSectionResult> analyse(ADFile file, List<? extends ReportSection> sections) {
		List<ReportSectionResult> result = new ArrayList<>();

		for(ReportSection sectionTemplate : sections) {
			ReportSectionResult section = new ReportSectionResult();
			section.fromSection(sectionTemplate);

			for (DataViewQuery payload : sectionTemplate.getData()) {
				AnalysisQuery query = queryFromPayload(payload);
				QueryIssues issues = sanitizeQuery(file, query);
				TrayProcessor processor = factory.create(query.getCompatibilityGroup(), query::take);
				DataTable table = tableService.createTable(processor.process(file), payload);
				table.setIssues(issues);
				if(table.isThresholdViolation()) {
					section.setThresholdViolation(true);
				}
				section.getData().add(table);
			}

			section.setChildren(this.analyse(file, sectionTemplate.getChildren()));
			result.add(section);
		}

		return result;
	}
	
	AnalysisQuery queryFromPayload(DataViewQuery payload){
		Set<QueryField> fields = new HashSet<>();
		for(DataSelector selector : payload.getSelectors()){
			fields.add(
					new QueryField(
							selector.getField(),
							selector.getValues()
									.stream()
									.map(ValueContainer::getValue)
									.collect(Collectors.toList()
									)
					)
			);
		}
		for(Field grp : payload.getGroupBy()){
			if(fields.stream().noneMatch((field) -> field.getF().equals(grp))) {
				fields.add(new QueryField(grp));
			}
		}
		return new AnalysisQuery(fields, payload.getType());
	}

}
