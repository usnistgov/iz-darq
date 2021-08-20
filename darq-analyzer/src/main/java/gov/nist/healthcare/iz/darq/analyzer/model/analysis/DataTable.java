package gov.nist.healthcare.iz.darq.analyzer.model.analysis;
import gov.nist.healthcare.iz.darq.analyzer.model.template.DataViewQuery;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataTable extends DataViewQuery {
    Map<Field, Set<String>> vocabulary;
    QueryIssues issues;
    boolean thresholdViolation;
    List<Field> headers;
    List<DataTableRow> values;

    public void fromQuery(DataViewQuery query) {
        this.setType(query.getType());
        this.setCaption(query.getCaption());
        this.setPaginate(query.isPaginate());
        this.setRows(query.getRows());
        this.setSelectors(query.getSelectors());
        this.setGroupBy(query.getGroupBy());
        this.setFilter(query.getFilter());
        this.setThreshold(query.getThreshold());
        this.headers = Stream.concat(
                query.getGroupBy().stream(),
                query.getOccurrences().stream()
        ).collect(Collectors.toList());
        this.values = new ArrayList<>();
        this.vocabulary = new HashMap<>();
    }

    public QueryIssues getIssues() {
        return issues;
    }

    public void setIssues(QueryIssues issues) {
        this.issues = issues;
    }

    public void putFieldValue(Field f, String value) {
        if(!this.vocabulary.containsKey(f)) {
            this.vocabulary.put(f, new HashSet<>());
        }
        this.vocabulary.get(f).add(value);
    }

    public Map<Field, Set<String>> getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Map<Field, Set<String>> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public List<Field> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Field> headers) {
        this.headers = headers;
    }

    public List<DataTableRow> getValues() {
        return values;
    }

    public boolean isThresholdViolation() {
        return thresholdViolation;
    }

    public void setThresholdViolation(boolean thresholdViolation) {
        this.thresholdViolation = thresholdViolation;
    }

    public void addThreshold(boolean b) {
        if(!b) {
            this.setThresholdViolation(true);
        }
    }

    public void setValues(List<DataTableRow> values) {
        this.values = values;
    }
}
