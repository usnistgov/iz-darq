import gov.nist.healthcare.iz.darq.analyzer.service.common.DataTableService;
import gov.nist.healthcare.iz.darq.analyzer.service.common.QueryValueResolverService;
import gov.nist.healthcare.iz.darq.analyzer.service.common.impl.SimpleDataTableService;
import gov.nist.healthcare.iz.darq.analyzer.service.sqlite.SqliteADFReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

	@Bean
	public SqliteADFReportService reportService(QueryValueResolverService queryValueResolverService, DataTableService dataTableService) {
		return new SqliteADFReportService(dataTableService, queryValueResolverService);
	}

	@Bean
	public DataTableService tableService(QueryValueResolverService queryValueResolverService) {
		return new SimpleDataTableService(queryValueResolverService);
	}

	@Bean
	public QueryValueResolverService queryValueResolverService() {
		return new MockQueryValueResolverService();
	}

}
