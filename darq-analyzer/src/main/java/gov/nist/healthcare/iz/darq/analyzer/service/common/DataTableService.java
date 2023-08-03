package gov.nist.healthcare.iz.darq.analyzer.service.common;

import gov.nist.healthcare.iz.darq.adf.module.api.ADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.QueryIssues;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryResultFilter;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryThreshold;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryVariableInstanceHolder;

import java.util.Set;

public interface DataTableService {

    DataTable applyThreshold(DataTable table, QueryThreshold threshold);
	DataTable applyFilters(DataTable table, QueryResultFilter filter);
	QueryVariableInstanceHolder getVariableInstanceHolder(QueryPayload payload, ADFReader file, String facilityId) throws Exception;
	QueryIssues getQueryIssues(ADFReader file, Set<String> detections);

}
