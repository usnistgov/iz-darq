package gov.nist.healthcare.iz.darq.analyzer.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTableRow;
import gov.nist.healthcare.iz.darq.analyzer.model.template.*;
import org.springframework.stereotype.Service;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.service.DataTableService;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Field._CG;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;


@Service
public class SimpleDataTableService implements DataTableService {

	@Override
	public DataTable createTable(List<Tray> trays, DataViewQuery payload) {
		DataTable table = new DataTable();
		table.fromQuery(payload);
		Map<Map<Field, String>, Fraction> rowFields = new HashMap<>();

		// Calculate total weight of trays
		int weigth = trays.stream().map(Tray::getWeigth)
		.reduce(0, Integer::sum);
		
		for(Tray t : trays){

			// Create group values
			Map<Field, String> groupBy = new HashMap<>();
			for(Field group : payload.getGroupBy()){
				String value = t.get(group);
				groupBy.put(group, value);
				table.putFieldValue(group, value);
			}

			// If group exists
			Fraction v;
			if(rowFields.containsKey(groupBy)){
				v = rowFields.get(groupBy);

				// Update count
				v.setCount(v.getCount() + t.getCount());

				// If type is not detection
				if(!useTotal(payload.getType())){
					v.setTotal(v.getTotal() + t.getWeigth());
				}

			}
			else {
				v = new Fraction(t.getCount(), useTotal(payload.getType()) ? weigth : t.getWeigth());
			}
			rowFields.put(groupBy, v);
		}

		rowFields.forEach((key, value) -> {
			DataTableRow row = new DataTableRow();
			row.setValues(key);
			row.setResult(value);
			table.getValues().add(row);
		});

		this.applyThreshold(table, payload.getThreshold());
		this.applyFilters(table, payload.getFilter());
		return table;
	}

	public boolean useTotal(_CG cg){
		switch (cg) {
		case PD:
			case VD:
				return false;
		case PT:
			case V:
			case VT:
				return true;
		}
		return false;
	}


	@Override
	public DataTable applyThreshold(DataTable table, QueryThreshold threshold) {
		for(DataTableRow row : table.getValues()) {
			if(threshold.getCustom().isActive()) {
				ComplexThreshold complexThreshold = this.matchCustomThreshold(row, threshold.getCustom());
				if(complexThreshold != null) {
					boolean value = this.applyThreshold(row, complexThreshold.getGoal());
					table.addThreshold(value);
					continue;
				}
			}

			if(threshold.getGlobal().isActive()) {
				boolean value = this.applyThreshold(row, threshold.getGlobal().getGoal());
				table.addThreshold(value);
			} else {
				row.setPass(true);
			}
		}
		return table;
	}

	public boolean applyThreshold(DataTableRow row, Threshold threshold) {
		boolean eval = this.eval(threshold.getComparator(), threshold.getValue(), row.getResult().percent());
		row.setThreshold(threshold);
		row.setPass(eval);
		return eval;
	}

	private boolean match(Map<Field, ValueContainer> selector, Map<Field, String> row) {
		return selector.entrySet().stream().allMatch((entry) ->
				row.containsKey(entry.getKey()) &&
						row.getOrDefault(entry.getKey(), "").equals(entry.getValue().getValue()
						)
		);
	}

	public boolean eval(Comparator comparator, double target, double value) {
		switch (comparator) {
			case GT:
				return value > target;
			case LT:
				return value < target;
		}
		return false;
	}

	public ComplexThreshold matchCustomThreshold(DataTableRow row, CustomThreshold threshold) {
		for(ComplexThreshold complexThreshold: threshold.getThresholds()) {
			if(match(complexThreshold.getValues(), row.getValues())) {
				return complexThreshold;
			}
		}
		return null;
	}

	@Override
	public DataTable applyFilters(DataTable table, QueryResultFilter filter) {
		List<DataTableRow> rows = table.getValues().stream().filter((row) ->
				this.compareFilter(filter.getDenominator(), row.getResult().getTotal()) &&
				this.compareFilter(filter.getPercentage(), row.getResult().percent()) &&
				this.thresholdFilter(filter.getThreshold(), row) &&
				this.valueFilter(filter.getGroups(), row)
		).collect(Collectors.toList());

		table.setValues(rows);
		return table;
	}

	public boolean compareFilter(CompareFilter filter, double value) {
		return !filter.isActive() || this.eval(filter.getComparator(), filter.getValue(), value);
	}

	public boolean thresholdFilter(ThresholdFilter filter, DataTableRow row) {
		return !filter.isActive() || row.isPass() == filter.isPass();
	}

	public boolean valueFilter(GroupFilter filter, DataTableRow row) {
		if(!filter.isActive() || filter.getValues() == null ||  filter.getValues().size() == 0) {
			return true;
		}

		for(Map<Field, ValueContainer> selector: filter.getValues()) {
			if(match(selector, row.getValues())) {
				return filter.isKeep();
			}
		}

		return !filter.isKeep();
	}
}
