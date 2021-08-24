package gov.nist.healthcare.iz.darq.analyzer.service;

import java.util.List;

import gov.nist.healthcare.iz.darq.analyzer.model.analysis.Tray;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.template.DataViewQuery;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryResultFilter;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryThreshold;

public interface DataTableService {

	DataTable createTable(List<Tray> trays, QueryPayload payload);
	DataTable applyThreshold(DataTable table, QueryThreshold threshold);
	DataTable applyFilters(DataTable table, QueryResultFilter filter);

}
