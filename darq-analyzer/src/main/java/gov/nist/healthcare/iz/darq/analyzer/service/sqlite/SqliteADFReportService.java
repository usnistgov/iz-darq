package gov.nist.healthcare.iz.darq.analyzer.service.sqlite;

import com.google.common.base.Strings;
import gov.nist.healthcare.iz.darq.adf.model.ADFVersion;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.SqliteADFReader;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTable;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.DataTableRow;
import gov.nist.healthcare.iz.darq.analyzer.model.analysis.QueryIssues;
import gov.nist.healthcare.iz.darq.analyzer.model.template.DataSelector;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryPayload;
import gov.nist.healthcare.iz.darq.analyzer.model.template.QueryVariableInstanceHolder;
import gov.nist.healthcare.iz.darq.analyzer.model.template.ValueContainer;
import gov.nist.healthcare.iz.darq.analyzer.service.ADFReportService;
import gov.nist.healthcare.iz.darq.analyzer.service.common.DataTableService;
import gov.nist.healthcare.iz.darq.analyzer.service.common.QueryValueResolverService;
import gov.nist.healthcare.iz.darq.digest.domain.AnalysisType;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SqliteADFReportService extends ADFReportService<SqliteADFReader> {

	@Autowired
	private DataTableService tableService;
	@Autowired
	QueryValueResolverService queryValueResolverService;

	@Override
	public DataTable singleQuery(SqliteADFReader file, QueryPayload payload, String facilityId) throws Exception {
		StringBuilder query = new StringBuilder();
		Set<String> detections = payload.getFilterFields().stream().filter((selector) -> selector.getField().equals(Field.DETECTION))
				.flatMap((selector) -> selector.getValues().stream())
				.map(ValueContainer::getValue)
				.filter((value) -> !Strings.isNullOrEmpty(value))
				.collect(Collectors.toSet());
		QueryIssues issues = tableService.getQueryIssues(file, detections);
		QueryVariableInstanceHolder holder = this.tableService.getVariableInstanceHolder(payload, file, facilityId);

		boolean isDetections = isDetectionsAnalysis(payload.getType());
		Set<Field> allFields = Stream.concat(payload.getDenominatorFields().stream(), payload.getNominatorFields().stream()).collect(Collectors.toSet());
		query
				.append(select(allFields)).append(", ")
				.append(denominator(payload.getDenominatorFields(), isDetections))
				.append(", ")
				.append(numerator(payload.getDenominatorFields(), payload.getNominatorFields()))
				.append(", ")
				.append(groupId(payload.getDenominatorFields()))
				.append("\n")
				.append(from(payload.getType()))
				.append("\n")
				.append(where(file, payload.getFilterFields()));
		try (Statement statement = file.getConnection().createStatement()) {
			ResultSet resultSet = statement.executeQuery(query.toString());
			DataTable table = createDataTable(file, resultSet, allFields, payload, holder);
			table.setIssues(issues);
			return table;
		}
	}

	private String groupId(Set<Field> fields) {
		StringBuilder clause = new StringBuilder();
		clause.append("dense_rank() OVER (");
		if(fields != null && fields.size() > 0) {
			clause.append("ORDER BY ");
			clause.append(
					String.join(
							", ",
							fields.stream()
									.map(this::translate)
									.collect(Collectors.toSet())
					)
			);
		}
		clause.append(") AS GROUP_ID");
		return clause.toString();
	}

	@Override
	public ADFVersion getVersion() {
		return ADFVersion.ADFSQLITE0001;
	}

	String select(Set<Field> fields) {
		StringBuilder select = new StringBuilder();
		select.append("SELECT DISTINCT").append(" ");
		if(fields != null && fields.size() > 0) {
			select.append(
					String.join(
							", ",
							fields.stream()
							.map((field) -> translate(field) + " AS " + field)
							.collect(Collectors.toSet())
					)
			);
		} else {
			select.append("*");
		}
		return select.toString();
	}

	String over(Set<Field> fields) {
		StringBuilder clause = new StringBuilder();
		clause.append(" OVER (");
		if(fields != null && fields.size() > 0) {
			clause.append("PARTITION BY ");
			clause.append(
					String.join(
							", ",
							fields.stream()
									.map(this::translate)
									.collect(Collectors.toSet())
					)
			);
		}
		clause.append(")");
		return clause.toString();
	}

	String denominator(Set<Field> fields, boolean isDetectionTable) {
		StringBuilder clause = new StringBuilder();
		clause.append("SUM").append("(");
		if(isDetectionTable) {
			clause.append("N + P");
		} else {
			clause.append("N");
		}
		clause.append(")");

		clause.append(over(fields));
		clause.append(" AS DENOMINATOR");
		return clause.toString();
	}

	String numerator(Set<Field> numeratorFields, Set<Field> denominatorFields) {
		StringBuilder clause = new StringBuilder();
		clause.append("SUM(N)");
		Set<Field> fields = Stream.concat(numeratorFields.stream(), denominatorFields.stream()).collect(Collectors.toSet());
		clause.append(over(fields));
		clause.append(" AS NUMERATOR");
		return clause.toString();
	}

	String where(SqliteADFReader file, Set<DataSelector> selectors) {
		StringBuilder where = new StringBuilder();
		Set<String> whereByField = selectors
				.stream()
				.map((selector) -> whereByField(file, selector))
				.filter((value) -> !Strings.isNullOrEmpty(value))
				.collect(Collectors.toSet());
		if(whereByField.size() > 0) {
			where.append("WHERE")
					.append(" ")
					.append(String.join(" AND ", whereByField));
		}
		return where.toString();
	}

	String from(AnalysisType type) {
		return "FROM " + translate(type);
	}

	String translate(AnalysisType type) {
		switch (type) {
			case V:
				return "V_EVENTS";
			case VD:
				return "V_DETECTIONS";
			case VT:
				return "V_VOCAB";
			case PD:
				return "P_DETECTIONS";
			case PT:
				return "P_VOCAB";
			case PD_RG:
				return "P_PROVIDER_DETECTIONS";
			case PT_RG:
				return "P_PROVIDER_VOCAB";
			default:
				return type.name();
		}
	}

	String whereByField(SqliteADFReader file, DataSelector selector) {
		StringBuilder whereByField = new StringBuilder();
		Set<Integer> values = selector
				.getValues()
				.stream()
				.filter(v -> v != null && !Strings.isNullOrEmpty(v.getValue()))
				.map((v) -> translate(file, selector.getField(), v.getValue()))
//				.filter((i) -> i != -1)
				.collect(Collectors.toSet());
		if(values.size() > 1) {
			whereByField
					.append(translate(selector.getField()))
					.append(" ")
					.append("IN")
					.append(" ")
					.append("(")
					.append(String.join(",", values.stream().map(Object::toString).collect(Collectors.toSet())))
					.append(")");
		} else if(values.size() == 1) {
			whereByField
					.append(translate(selector.getField()))
					.append(" ")
					.append("=")
					.append(" ")
					.append(String.join(",", values.stream().map(Object::toString).collect(Collectors.toSet())));
		}
		return whereByField.toString();
	}

	private int translate(SqliteADFReader file, Field field, String value) {
		return file.getDictionaries().findId(field, value);
	}

	private String translate(SqliteADFReader file, Field field, int value) {
		return file.getDictionaries().findValue(field, value);
	}

	private String translate(Field field) {
		switch (field) {
			case PROVIDER:
				return "PROVIDER_ID";
			case AGE_GROUP:
				return "AGE_GROUP";
			case TABLE:
				return "VS";
			case CODE:
			case VACCINE_CODE:
				return "CODE";
			case DETECTION:
				return "DETECTION_CODE";
			case EVENT:
				return "SOURCE";
			case GENDER:
				return "GENDER";
			case VACCINATION_YEAR:
				return "YEAR";
			default:
				return field.name();
		}
	}

	private boolean isDetectionsAnalysis(AnalysisType type) {
		switch (type) {
			case VD:
			case PD:
			case PD_RG:
				return true;
			case V:
			case VT:
			case PT:
			case PT_RG:
			default:
				return false;
		}
	}

	DataTable createDataTable(SqliteADFReader file, ResultSet resultSet, Set<Field> fields, QueryPayload payload, QueryVariableInstanceHolder variables) throws SQLException {
		DataTable table = new DataTable();
		table.fromQuery(payload);
		int i = 1;

		while (resultSet.next()) {
			DataTableRow row = new DataTableRow();
			int NUMERATOR = resultSet.getInt("NUMERATOR");
			int DENOMINATOR = resultSet.getInt("DENOMINATOR");
			Fraction rowFraction = new Fraction(NUMERATOR, DENOMINATOR);
			row.setGroupId(resultSet.getInt("GROUP_ID"));
			row.setValues(getRowFields(file, resultSet, fields));
			row.setResult(rowFraction);
			row.setAdjustedFraction(variables.getAdjustedFraction(rowFraction));
			table.getValues().add(row);
		}

		if(table.getValues().size() == 0 && variables != null && variables.hasNumerator() && variables.hasDenominator()) {
			DataTableRow row = new DataTableRow();
			Fraction rowFraction = new Fraction((int) variables.getNumerator().getValue(), (int) variables.getDenominator().getValue());
			row.setGroupId(1);
			row.setValues(new HashMap<>());
			row.setResult(rowFraction);
			row.setAdjustedFraction(variables.getAdjustedFraction(rowFraction));
			table.getValues().add(row);
		}

		tableService.applyThreshold(table, payload.getQueryThreshold());
		tableService.applyFilters(table, payload.getFilter());
		return table;
	}

	Map<Field, String> getRowFields(SqliteADFReader file, ResultSet resultSet,  Set<Field> fields) throws SQLException {
		Map<Field, String> values = new HashMap<>();
		for(Field field : fields) {
			values.put(field, translate(file, field, resultSet.getInt(field.toString())));
		}
		return values;
	}

}
