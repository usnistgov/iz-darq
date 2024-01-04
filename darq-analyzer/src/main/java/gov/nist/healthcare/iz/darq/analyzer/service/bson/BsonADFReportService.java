package gov.nist.healthcare.iz.darq.analyzer.service.bson;

import java.util.*;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.json.BsonADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.*;
import gov.nist.healthcare.iz.darq.analyzer.model.template.*;
import gov.nist.healthcare.iz.darq.analyzer.service.ADFReportService;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.TrayProcessor;
import gov.nist.healthcare.iz.darq.analyzer.service.bson.tray.TrayProcessorFactoryImpl;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gov.nist.healthcare.iz.darq.analyzer.service.common.DataTableService;
import gov.nist.healthcare.iz.darq.adf.module.json.model.ADFile;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

@Service
public class BsonADFReportService extends ADFReportService<BsonADFReader> {

	@Autowired
	TrayProcessorFactoryImpl factory;
	@Autowired
	private DataTableService tableService;


	@Override
	public DataTable singleQuery(BsonADFReader file, QueryPayload payload, String facilityId) throws Exception {
		AnalysisQuery query = queryFromPayload(payload);
		QueryField detectionQueryField = query.get(Field.DETECTION);
		Set<String> detectionsSelection = detectionQueryField != null ? detectionQueryField.getValues() != null ? new HashSet<>(detectionQueryField.getValues()) : new HashSet<>() : new HashSet<>();
		QueryIssues issues = tableService.getQueryIssues(file, detectionsSelection);
		QueryVariableInstanceHolder holder = this.tableService.getVariableInstanceHolder(payload, file, facilityId);

		List<Tray> trays = payload.getPayloadType().equals(QueryPayloadType.VARIABLE) ?
				new ArrayList<>() :
				getQueryTrays(file.getADF(), query);

		DataTable table = createTable(trays, payload, holder);
		table.setIssues(issues);
		return table;
	}

	@Override
	public ADFVersion getVersion() {
		return ADFVersion.ADFBSON000001;
	}

	List<Tray> getQueryTrays(ADFile file, AnalysisQuery query) {
		TrayProcessor processor = factory.create(query.getCompatibilityGroup(), query::take);
		return processor.process(file);
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
			if(fields.stream().noneMatch((field) -> field.getField().equals(grp))) {
				fields.add(new QueryField(grp));
			}
		}
		return new AnalysisQuery(fields, payload.getType());
	}

	public DataTable createTable(List<Tray> trays, QueryPayload payload, QueryVariableInstanceHolder variables) {
		DataTable table = new DataTable();
		table.fromQuery(payload);
		Counter groupId = new Counter();
		groupTraysBy(payload.getDenominatorFields(), trays)
				.forEach((group, groupTrays) -> {
					int denominator = groupTrays.stream().map(Tray::getWeigth).reduce(0, Integer::sum);
					groupId.i++;
					groupTraysBy(payload.getNominatorFields(), groupTrays)
							.forEach((values, rowTrays) -> {
								int nominator = rowTrays.stream().map(Tray::getCount).reduce(0, Integer::sum);

								DataTableRow row = new DataTableRow();
								Fraction rowFraction = new Fraction(nominator, denominator);
								row.setGroupId(groupId.i);
								row.setValues(makeRowValues(group, values));
								row.setResult(rowFraction);
								row.setAdjustedFraction(variables.getAdjustedFraction(rowFraction));
								table.getValues().add(row);
							});
				});

		if(trays.size() == 0 && variables != null && variables.hasNumerator() && variables.hasDenominator()) {
			DataTableRow row = new DataTableRow();
			Fraction rowFraction = new Fraction((int) variables.getNumerator().getValue(), (int) variables.getDenominator().getValue());
			row.setGroupId(groupId.i);
			row.setValues(new HashMap<>());
			row.setResult(rowFraction);
			row.setAdjustedFraction(variables.getAdjustedFraction(rowFraction));
			table.getValues().add(row);
		}

		tableService.applyThreshold(table, payload.getQueryThreshold());
		tableService.applyFilters(table, payload.getFilter());
		return table;
	}

	Map<Map<Field, String>, List<Tray>> groupTraysBy(Collection<Field> fields, List<Tray> trays) {
		return trays.stream().collect(
				Collectors.groupingBy(
						(tray) -> {
							Map<Field, String> groupBy = new HashMap<>();
							for(Field group : fields){
								groupBy.put(group, tray.get(group));
							}
							return groupBy;
						},
						Collectors.toList()
				));
	}

	Map<Field, String> makeRowValues(Map<Field, String> group, Map<Field, String> values) {
		Map<Field, String> row = new HashMap<>(group);
		values.forEach((k, v) -> row.merge(k, v, (v1, v2) -> v2));
		return row;
	}

}
