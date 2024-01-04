package gov.nist.healthcare.iz.darq.analyzer.service.common.impl;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.*;
import gov.nist.healthcare.iz.darq.analyzer.model.template.*;
import gov.nist.healthcare.iz.darq.analyzer.model.variable.QueryVariableRefInstance;
import gov.nist.healthcare.iz.darq.analyzer.service.common.DataTableService;
import gov.nist.healthcare.iz.darq.analyzer.service.common.QueryValueResolverService;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SimpleDataTableService implements DataTableService {

    QueryValueResolverService queryValueResolverService;

    public SimpleDataTableService(QueryValueResolverService queryValueResolverService) {
        this.queryValueResolverService = queryValueResolverService;
    }

    @Override
    public DataTable applyThreshold(DataTable table, QueryThreshold threshold) {
        if(threshold != null) {
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
        if(filter != null) {
            List<DataTableRow> rows = table.getValues().stream().filter((row) ->
                    this.compareFilter(filter.getDenominator(), row.getEffectiveResult().getTotal()) &&
                            this.compareFilter(filter.getPercentage(), row.getEffectiveResult().percent()) &&
                            this.thresholdFilter(filter.getThreshold(), row) &&
                            this.valueFilter(filter.getGroups(), row)
            ).collect(Collectors.toList());

            table.setValues(rows);
        }
        return table;
    }

    @Override
    public QueryVariableInstanceHolder getVariableInstanceHolder(QueryPayload payload, ADFReader file, String facilityId) throws Exception {
        QueryVariableRefInstance denominatorVariableValue = payload.getDenominatorVariable() != null ?
                this.queryValueResolverService.resolveInstanceValue(payload.getDenominatorVariable(), file, facilityId) : null;
        QueryVariableRefInstance numeratorVariableValue = payload.getNumeratorVariable() != null ?
                this.queryValueResolverService.resolveInstanceValue(payload.getNumeratorVariable(), file, facilityId) : null;
        QueryVariableInstanceHolder holder = new QueryVariableInstanceHolder();
        holder.setDenominator(denominatorVariableValue);
        holder.setNumerator(numeratorVariableValue);
        return holder;
    }

    @Override
    public QueryIssues getQueryIssues(ADFReader file, Set<String> detections) {
        Set<String> adfInactiveDetections = file.getMetadata().getInactiveDetections();
        QueryIssues issues = new QueryIssues();

        // Check for inactive detections
        if(adfInactiveDetections != null && adfInactiveDetections.size() > 0) {
            if(detections != null) {
                issues.setInactiveDetections(detections.stream().filter(adfInactiveDetections::contains).collect(Collectors.toSet()));
                if(issues.getInactiveDetections() != null && issues.getInactiveDetections().size() > 0) {
                    return issues;
                }
            }
        }
        return null;
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
