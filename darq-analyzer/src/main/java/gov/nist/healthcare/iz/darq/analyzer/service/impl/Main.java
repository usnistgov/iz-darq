package gov.nist.healthcare.iz.darq.analyzer.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.iz.darq.adf.service.ADFStore;
import gov.nist.healthcare.iz.darq.adf.utils.crypto.CryptoUtils;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisPayload.FieldValue;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery.QueryField;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisReport;
import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisResult;
import gov.nist.healthcare.iz.darq.analyzer.domain.Field;
import gov.nist.healthcare.iz.darq.analyzer.domain.Field._CG;
import gov.nist.healthcare.iz.darq.analyzer.domain.ReportSection;
import gov.nist.healthcare.iz.darq.analyzer.domain.ReportTemplate;
import gov.nist.healthcare.iz.darq.analyzer.service.Analyzer;
import gov.nist.healthcare.iz.darq.digest.domain.ADFMetaData;
import gov.nist.healthcare.iz.darq.digest.domain.ADFile;

@Configuration
@ComponentScan("gov.nist.healthcare")
@PropertySource("classpath:/key.properties")
public class Main {

	
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
		CryptoUtils utils = (CryptoUtils) context.getBean(CryptoUtils.class);
		SimpleReportService analyzer = (SimpleReportService) context.getBean(SimpleReportService.class);
		byte[] bytes = Files.readAllBytes(Paths.get("/Users/hnt5/SharedProjects/iz-darq/darq-extract-process/darq-cli-app/target/darq-analysis/ADF.data"));
		ADFile file = utils.decrypt(bytes);
//		Set<QueryField> fields = new HashSet<>();
//		fields.add(new QueryField(Field.VACCINATION_YEAR, "2008"));
//		fields.add(new QueryField(Field.GENDER, "M"));
//		fields.add(new QueryField(Field.VACCINE_CODE));
//		AnalysisQuery query = new AnalysisQuery(fields, _CG.V);
//		AnalysisResult r = analyzer.analyze(file, query);
		AnalysisReport ar = analyzer.analyse(file, template());
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ar));
//		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(template()));

//		Set<C> set = new HashSet<>();
//		set.add(new C("A",1));
//		set.add(new C("A",2));
//		System.out.println(set);
	}
//	MQE0141
//	@Bean
//	public ADFStore store(){
//		return new ADFStore() {
//			
//			@Override
//			public String store(ADFMetaData metadata) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public ADFile getFile(String id, String owner) throws Exception {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public ADFMetaData get(String id, String owner) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//			
//			@Override
//			public boolean delete(String id, String owner) throws IOException {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		};
//	}
//	
//	static ReportTemplate template(){
//		ReportTemplate template = new ReportTemplate();
//		template.setName("TEST");
//		
//		ReportSection section = new ReportSection();
//		section.setTitle("PCV17 by Year");
//		
//		AnalysisPayload payload = new AnalysisPayload();
//		payload.setType(_CG.PT);
//		List<FieldValue> filters = new ArrayList<>();
//		filters.add(new FieldValue(Field.TABLE, "GENDER"));
////		filters.add(new FieldValue(Field.C, "M"));
//		payload.setFilters(filters);
//		payload.setGroupBy(Arrays.asList(Field.CODE));
//		
//		section.setPayloads(Arrays.asList(payload));
//		
//		template.setSections(Arrays.asList(section));
//		
//		
//		return template;
//	}
	
//	static ReportTemplate template(){
//		ReportTemplate template = new ReportTemplate();
//		template.setName("TEST");
//		
//		ReportSection section = new ReportSection();
//		section.setTitle("PCV17 by Year");
//		
//		AnalysisPayload payload = new AnalysisPayload();
//		payload.setType(_CG.PD);
//		List<FieldValue> filters = new ArrayList<>();
//		filters.add(new FieldValue(Field.DETECTION, "MQE0141"));
////		filters.add(new FieldValue(Field.C, "M"));
//		payload.setFilters(filters);
////		payload.setGroupBy(Arrays.asList(Field.CODE));
//		
//		section.setPayloads(Arrays.asList(payload));
//		
//		template.setSections(Arrays.asList(section));
//		
//		
//		return template;
//	}
	
	static ReportTemplate template(){
		ReportTemplate template = new ReportTemplate();
		template.setName("TEST");
		
		ReportSection section = new ReportSection();
		section.setTitle("PCV17 by Year");
		
		AnalysisPayload payload = new AnalysisPayload();
		payload.setType(_CG.V);
		List<FieldValue> filters = new ArrayList<>();
//		filters.add(new FieldValue(Field.DETECTION, "MQE0141"));
//		filters.add(new FieldValue(Field.C, "M"));
		payload.setFilters(filters);
		payload.setGroupBy(Arrays.asList(Field.VACCINE_CODE));
		
		section.setPayloads(Arrays.asList(payload));
		
		template.setSections(Arrays.asList(section));
		
		
		return template;
	}
	
	public static class C {
		public String a;
		public int b;
		
		public C(String a, int b) {
			super();
			this.a = a;
			this.b = b;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((a == null) ? 0 : a.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			C other = (C) obj;
			if (a == null) {
				if (other.a != null)
					return false;
			} else if (!a.equals(other.a))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "C [a=" + a + ", b=" + b + "]";
		}
		
	}
}
