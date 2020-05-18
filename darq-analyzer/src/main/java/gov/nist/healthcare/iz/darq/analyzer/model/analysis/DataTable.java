package gov.nist.healthcare.iz.darq.analyzer.model.analysis;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTableRow;
import gov.nist.healthcare.iz.darq.analyzer.model.template.DataViewQuery;
import gov.nist.healthcare.iz.darq.digest.domain.Field;

import java.util.ArrayList;
import java.util.List;

public class DataTable extends DataViewQuery {
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
        this.headers = new ArrayList<>(query.getGroupBy());
        this.values = new ArrayList<>();
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

    public void setValues(List<DataTableRow> values) {
        this.values = values;
    }
}
