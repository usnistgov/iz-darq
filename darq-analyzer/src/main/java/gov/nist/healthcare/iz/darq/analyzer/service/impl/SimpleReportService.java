package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.*;
import gov.nist.healthcare.iz.darq.analyzer.model.template.*;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.analyzer.service.QueryValueResolverService;
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
	@Autowired
	QueryValueResolverService queryValueResolverService;

	@Override
	public DataTable singleQuery(ADFile file, QueryPayload payload, String facilityId) throws Exception {
		AnalysisQuery query = queryFromPayload(payload);
		QueryIssues issues = sanitizeQuery(file, query);
		QueryVariableRefInstance denominatorVariableValue = payload.getDenominatorVariable() != null ?
				this.queryValueResolverService.resolveInstanceValue(payload.getDenominatorVariable(), file, facilityId) : null;
		QueryVariableRefInstance numeratorVariableValue = payload.getNumeratorVariable() != null ?
				this.queryValueResolverService.resolveInstanceValue(payload.getNumeratorVariable(), file, facilityId) : null;
		QueryVariableInstanceHolder holder = new QueryVariableInstanceHolder();
		holder.setDenominator(denominatorVariableValue);
		holder.setNumerator(numeratorVariableValue);

		List<Tray> trays = payload.getPayloadType().equals(QueryPayloadType.VARIABLE) ?
				new ArrayList<>() :
				getQueryTrays(file, query);

		DataTable table = tableService.createTable(trays, payload, holder);
		table.setIssues(issues);
		return table;
	}

	List<Tray> getQueryTrays(ADFile file, AnalysisQuery query) {
		TrayProcessor processor = factory.create(query.getCompatibilityGroup(), query::take);
		return processor.process(file);
	}

	QueryIssues sanitizeQuery(ADFile file, AnalysisQuery query) {
		Set<String> inactive = null;

		// Check for inative detections
		if(file.getInactiveDetections() != null && file.getInactiveDetections().size() > 0) {
			QueryField f = query.get(Field.DETECTION);
			if(f != null && f.getValues() != null) {
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
	public AnalysisReport analyse(ADFile file, ReportTemplate template, String facilityId) throws Exception {
		try {
			AnalysisReport result = new AnalysisReport();
			result.setName(template.getName());
			result.setDescription(template.getDescription());
			result.setSections(this.analyse(file, template.getSections(), facilityId));
			result.setConfiguration(template.getConfiguration());
			result.setCustomDetectionLabels(template.getCustomDetectionLabels());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private List<ReportSectionResult> analyse(ADFile file, List<? extends ReportSection> sections, String facilityId) throws Exception {
		if(sections != null) {
			List<ReportSectionResult> result = new ArrayList<>();
			for(ReportSection sectionTemplate : sections) {
				ReportSectionResult section = new ReportSectionResult();
				section.fromSection(sectionTemplate);

				for (QueryPayload payload : sectionTemplate.getData()) {
					DataTable table = this.singleQuery(file, payload, facilityId);
					if(table.isThresholdViolation()) {
						section.setThresholdViolation(true);
					}
					section.getData().add(table);
				}

				section.setChildren(this.analyse(file, sectionTemplate.getChildren(), facilityId));
				result.add(section);
			}
			return result;
		}
		return new ArrayList<>();
	}
	
	AnalysisQuery queryFromPayload(QueryPayload payload){
		Set<QueryField> fields = new HashSet<>();
		for(DataSelector selector : payload.getFilterFields()){
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
		for(Field grp : payload.getDenominatorFields()){
			if(fields.stream().noneMatch((field) -> field.getF().equals(grp))) {
				fields.add(new QueryField(grp));
			}
		}
		return new AnalysisQuery(fields, payload.getType());
	}

}
