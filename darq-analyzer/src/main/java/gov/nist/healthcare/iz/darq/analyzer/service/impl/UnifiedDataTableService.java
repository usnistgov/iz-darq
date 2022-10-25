package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.*;
import gov.nist.healthcare.iz.darq.analyzer.model.template.*;
import gov.nist.healthcare.iz.darq.analyzer.service.DataTableService;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UnifiedDataTableService implements DataTableService {

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

    @Override
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

        this.applyThreshold(table, payload.getQueryThreshold());
        this.applyFilters(table, payload.getFilter());
        return table;
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
        boolean eval = this.eval(threshold.getComparator(), threshold.getValue(), row.getEffectiveResult().percent());
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
                this.compareFilter(filter.getDenominator(), row.getEffectiveResult().getTotal()) &&
                        this.compareFilter(filter.getPercentage(), row.getEffectiveResult().percent()) &&
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
