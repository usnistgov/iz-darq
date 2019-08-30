package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.detection.ValidationReport;
import org.immregistries.mqe.validator.domain.TargetType;
import org.immregistries.mqe.validator.engine.MessageValidator;
import org.immregistries.mqe.validator.engine.ValidationRuleResult;
import org.immregistries.mqe.vxu.MqeMessageReceived;
import org.immregistries.mqe.vxu.MqeVaccination;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.domain.TransformResult;
import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;
import gov.nist.healthcare.iz.darq.digest.service.DetectionFilter;
import gov.nist.healthcare.iz.darq.digest.service.VaxGroupMapper;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;
import gov.nist.healthcare.iz.darq.parser.model.VaccineRecord;


public class DQAValidator {

	public static class Issue {
		String code;
		boolean vaccine;
		String provider;
		String age;
		boolean positive;
		public Issue(String code, boolean vaccine, String provider, String age, boolean positive) {
			super();
			this.code = code;
			this.vaccine = vaccine;
			this.provider = provider;
			this.age = age;
			this.positive = positive;
		}
		public String getCode() {
			return code;
		}
		public boolean isVaccine() {
			return vaccine;
		}
		public String getProvider() {
			return provider;
		}
		public String getAgeGroup() {
			return age;
		}
		public boolean isPositive() {
			return positive;
		}
		public void setPositive(boolean positive) {
			this.positive = positive;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((code == null) ? 0 : code.hashCode());
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
			Issue other = (Issue) obj;
			if (code == null) {
				if (other.code != null)
					return false;
			} else if (!code.equals(other.code))
				return false;
			return true;
		}
	}
	
	public class VxInfo {
		String provider;
		String age;
		String year;
		String code;
		String gender;
		String source;
		public VxInfo(String provider, String age, String code, String gender, String year, String source) {
			super();
			this.provider = provider;
			this.age = age;
			this.code = code;
			this.year = year;
			this.gender = gender;
			this.source = source;
		}
		public String getProvider() {
			return provider;
		}
		public String getAgeGroup() {
			return age;
		}
		public String getCode() {
			return code;
		}
		public String getGender() {
			return gender;
		}
		public String getSource() {
			return source;
		}
		public String getYear() {
			return year;
		}
		
	}
	
	private MessageValidator validator = MessageValidator.INSTANCE;
	public MQETransformService transformer;
	public List<Issue> issues;
	public List<VxInfo> vx;
	public Map<String, String> providers;
	public AgeGroupService ageGroupCalculator;
	private DetectionFilter filter;
	private VaxGroupMapper vaxMapper;
	public int nbVx;

	public DQAValidator(AgeGroupService ageGroupCalculator, DetectionFilter filter, VaxGroupMapper vaxMapper) {
		super();
		transformer = new MQETransformService();
		this.issues = new ArrayList<>();
		this.providers = new HashMap<>();
		this.vx = new ArrayList<>();
		this.nbVx = 0;
		this.ageGroupCalculator = ageGroupCalculator;
		this.filter = filter;
		this.vaxMapper = vaxMapper;
	}
	private DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
	
	public void validateRecord(AggregatePatientRecord apr, LocalDate date){
		
		// Transform APR
		TransformResult<VaccineRecord, MqeVaccination, MqeMessageReceived> tresult = this.transformer.transform(apr);
		MqeMessageReceived msg = tresult.getPayload();
		nbVx = msg.getVaccinations().size();
		
		// DOB and Patient AgeGroup
		LocalDate DOB = LocalDate.parse(msg.getPatient().getBirthDateString(), dateFormat);
		String currentAgeGroup = this.ageGroupCalculator.getGroup(DOB, date);

		// For each Vaccination
		for(MqeVaccination vx : msg.getVaccinations()){
			this.providers.put(
					vx.getID(),
					tresult.getAFromB(vx.getID()).reporting_group.getValue());
			
			// Age Group relative to Administered date 
			String ageGroup = this.ageGroupCalculator.getGroup(DOB, LocalDate.parse(vx.getAdminDateString(), dateFormat));
			
			// Adding vaccination to vaccine list
			this.vx.add(new VxInfo(tresult.getAFromB(vx.getID()).reporting_group.getValue(), ageGroup, this.vaxMapper.translate(vx.getAdminCvxCode()), msg.getPatient().getSex(), vx.getAdminDateString().substring(0,4), vx.getInformationSourceCode()));
		}
		
		List<ValidationRuleResult> results = new ArrayList<>();

		try {
			results = this.validator.validateMessageNIST(msg);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Set<Issue> patientIssues = new HashSet<>();
		Map<String, Set<Issue>> dedup = new HashMap<>();

		// For each Result
		for(ValidationRuleResult r : results){
			
			// Get Possible Detections for Rule
			Set<Detection> possible = r.getPossible().stream().filter(x -> x != null).collect(Collectors.toSet());
			
			// Rule target is vaccination
			boolean vaccine = r.getTargetType() == TargetType.Vaccination;
			
			// Get targeted vaccination
			MqeVaccination vx = byId(msg, r.getTargetId());
			
			// If the rule targets a vaccination set age group relative to admin date else use the age group relative to today
			String ageGroup = vaccine ? this.ageGroupCalculator.getGroup(DOB, LocalDate.parse(vx.getAdminDateString(), dateFormat)) : currentAgeGroup;
			
			for(ValidationReport vi : r.getIssues()){
				
				// Check if detection is is configuration
				if(this.filter.in(vi.getDetection().getMqeMqeCode())){
				
					// Remove from affirmatives
					possible.remove(vi.getDetection());
					
					// Create an issue
					Issue issue = new Issue(vi.getDetection().getMqeMqeCode(), vaccine, vaccine ? this.providers.get(vx.getID()) : null, ageGroup, false);
	
					if(vaccine) {
						if(dedup.containsKey(r.getTargetId())) {
							dedup.get(r.getTargetId()).add(issue);
						} else {
							dedup.put(r.getTargetId(), new HashSet<Issue>(Arrays.asList(issue)));
						}
					} else {
						
						// Add issue to list of issues as negative
						patientIssues.add(issue);
					}
				}
			}
			
			
			for(Detection d : possible){
				
				// Check if detection is is configuration
				if(this.filter.in(d.getMqeMqeCode())){
					
					// Create an issue
					Issue issue = new Issue(d.getMqeMqeCode(), vaccine, vaccine ? this.providers.get(vx.getID()) : null, ageGroup, true);
					
					if(vaccine) {
						if(dedup.containsKey(r.getTargetId())) {
							dedup.get(r.getTargetId()).add(issue);
						} else {
							dedup.put(r.getTargetId(),  new HashSet<Issue>(Arrays.asList(issue)));
						}
					} else {
						
						// Add issue to list of issues as positive
						patientIssues.add(issue);
					}
				} 
			}
		}
		
		this.issues.addAll(dedup.values().stream().flatMap((Set<Issue> x) ->{
			return x.stream();
		}).collect(Collectors.toList()));
		this.issues.addAll(patientIssues);
		
	}
	
	public MqeVaccination byId(MqeMessageReceived msg, String id){
		if(id == null || id.isEmpty()){
			return null;
		}
		
		for(MqeVaccination vx : msg.getVaccinations()){
			if(vx.getID().equals(id)){
				return vx;
			}
		}
		return null;
	}
	
	//------ PROVIDER ----- AGE ------ CODE ----- PAYLOAD
	public Map<String, Map<String, Map<String, DetectionSum>>> vaccinationDetections(){
		return this.issues.stream().filter(c -> c.vaccine)
				.collect(
						Collectors.groupingBy(
								Issue::getProvider,
								Collectors.groupingBy(
										Issue::getAgeGroup,
										Collectors.groupingBy(
												Issue::getCode,
												Collectors.collectingAndThen(
														Collectors.groupingBy(Issue::isPositive),
														(x) -> {
															return new DetectionSum(x.containsKey(true) ? x.get(true).size() : 0, x.containsKey(false) ? x.get(false).size() : 0);
														})
												)
										)
								)
						);
	}
	
	//--------- AGE ------ CODE ----- PAYLOAD
	public Map<String, Map<String, DetectionSum>> patientDetections(){
		return this.issues.stream().filter(c -> !c.vaccine)
				.collect(
						Collectors.groupingBy(
								Issue::getAgeGroup,
								Collectors.groupingBy(
										Issue::getCode,
										Collectors.collectingAndThen(
												Collectors.groupingBy(Issue::isPositive),
												(x) -> {
													return new DetectionSum(x.containsKey(true) ? x.get(true).size() : 0, x.containsKey(false) ? x.get(false).size() : 0);
												})
										)
								)
						);
	}
	
	//------- PROVIDER ---- AGE ------ CODE------- YEAR ------ GENDER ---- SOURCE -- NB
	public Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, Integer>>>>>> getVxInfo() {
		return this.vx.stream()
				.collect(
						Collectors.groupingBy(
								VxInfo::getProvider,
								Collectors.groupingBy(
										VxInfo::getAgeGroup,
										Collectors.groupingBy(
												VxInfo::getCode,
												Collectors.groupingBy(
													VxInfo::getYear,
													Collectors.groupingBy(
															VxInfo::getGender,
															Collectors.groupingBy(
																	VxInfo::getSource,
																	Collectors.collectingAndThen(
																			Collectors.counting(),
																			(x) -> {
																				return Math.toIntExact(x);
																			})
																	)
															)
													)
												)
										)
								)
						);
	}
	

	public Map<Field, Set<String>> vocabulary(){
		Map<Field, Set<String>> vocabulary = new HashMap<>();
		Set<String> year = this.keySet(VxInfo::getYear);
		Set<String> source = this.keySet(VxInfo::getSource);
		Set<String> vaxCode  = this.keySet(VxInfo::getCode);
		Set<String> detections = this.issues.stream()
		.collect(
				Collectors.groupingBy(
						Issue::getCode
				)
		).keySet();
		
		vocabulary.put(Field.VACCINATION_YEAR, year);
		vocabulary.put(Field.EVENT, source);
		vocabulary.put(Field.VACCINE_CODE, vaxCode);
		vocabulary.put(Field.DETECTION, detections);
		
		return vocabulary;
	}
	
	public Set<String> keySet(Function<? super VxInfo, String> func){
		return this.vx.stream()
				.collect(
						Collectors.groupingBy(
								func
						)
				).keySet();
	}
	
	
	
	
	
	
}
