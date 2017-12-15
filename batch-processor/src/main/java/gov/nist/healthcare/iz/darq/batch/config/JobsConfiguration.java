package gov.nist.healthcare.iz.darq.batch.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import gov.nist.healthcare.iz.darq.analysis.service.StatisticKind;
import gov.nist.healthcare.iz.darq.analysis.stats.NoValueKind;
import gov.nist.healthcare.iz.darq.analysis.stats.NotExtractedKind;
import gov.nist.healthcare.iz.darq.analysis.stats.StatisticsProcessor;
import gov.nist.healthcare.iz.darq.batch.service.SimpleJobExecutor;

@Configuration
public class JobsConfiguration {

    @Bean()
    public SimpleJobExecutor jobExecutor() {
    	SimpleJobExecutor executor = new SimpleJobExecutor(1);
    	return executor;
    }
    
	@Bean
	public StatisticsProcessor statProcessor(){
		List<StatisticKind> x = new ArrayList<>();
		x.add(new NotExtractedKind());
		x.add(new NoValueKind());
		return new StatisticsProcessor(x);
	}
    
}
