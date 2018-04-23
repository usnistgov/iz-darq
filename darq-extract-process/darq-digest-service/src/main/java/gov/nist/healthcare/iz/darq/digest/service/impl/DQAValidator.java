package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.immregistries.dqa.validator.domain.TargetType;
import org.immregistries.dqa.validator.engine.MessageValidator;
import org.immregistries.dqa.validator.engine.ValidationRuleResult;
import org.immregistries.dqa.validator.issue.Detection;
import org.immregistries.dqa.validator.issue.ValidationIssue;
import org.immregistries.dqa.vxu.DqaMessageReceived;
import org.immregistries.dqa.vxu.DqaVaccination;
import org.joda.time.LocalDate;

import gov.nist.healthcare.iz.darq.digest.domain.DetectionSum;
import gov.nist.healthcare.iz.darq.digest.domain.Field;
import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;
import gov.nist.healthcare.iz.darq.digest.service.DetectionFilter;
import gov.nist.healthcare.iz.darq.digest.service.VaxGroupMapper;
import gov.nist.healthcare.iz.darq.parser.model.AggregatePatientRecord;

public class DQAValidator {

	public class Issue {
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
	public DQATransformService transformer;
	public List<Issue> issues;
	public List<VxInfo> vx;
	public Map<String, String> providers;
	public AgeGroupService ageGroupCalculator;
	private DetectionFilter filter;
	private VaxGroupMapper vaxMapper;
	public int nbVx;

	public DQAValidator(AgeGroupService ageGroupCalculator, DetectionFilter filter, VaxGroupMapper vaxMapper) {
		super();
		this.transformer = new DQATransformService();
		this.issues = new ArrayList<>();
		this.providers = new HashMap<>();
		this.vx = new ArrayList<>();
		this.nbVx = 0;
		this.ageGroupCalculator = ageGroupCalculator;
		this.filter = filter;
		this.vaxMapper = vaxMapper;
	}
	
	public void validateRecord(AggregatePatientRecord apr, LocalDate date){
		DqaMessageReceived msg = this.transformer.transform(apr);
		nbVx = msg.getVaccinations().size();
		String currentAgeGroup = this.ageGroupCalculator.getGroup(msg.getPatient().getBirthDateString(), date.toString("yyyyMMdd"));

		for(DqaVaccination vx : msg.getVaccinations()){
			this.providers.put(vx.getID(), vx.getFacilityName());
			String ageGroup = this.ageGroupCalculator.getGroup(msg.getPatient().getBirthDateString(), vx.getAdminDateString());
			this.vx.add(new VxInfo(vx.getFacilityName(), ageGroup, this.vaxMapper.translate(vx.getAdminCvxCode()), msg.getPatient().getSex(), vx.getAdminDateString().substring(0,4), vx.getInformationSourceCode()));
		}
		List<ValidationRuleResult> results = new ArrayList<>();

		try {
			results = this.validator.validateMessageNIST(msg);
		}
		catch(Exception e){
			e.printStackTrace();
		}

		for(ValidationRuleResult r : results){
			List<Detection> possible = r.getPossible().stream().filter(x -> x != null).collect(Collectors.toList());
			boolean vaccine = r.getTargetType() == TargetType.Vaccination;
			DqaVaccination vx = byId(msg, r.getTargetId());
			String ageGroup = vaccine ? this.ageGroupCalculator.getGroup(msg.getPatient().getBirthDateString(), vx.getAdminDateString()) : currentAgeGroup;
			for(ValidationIssue vi : r.getIssues()){
				if(this.filter.in(vi.getIssue().getDqaErrorCode())){
					possible.remove(vi.getIssue());
					this.issues.add(new Issue(vi.getIssue().getDqaErrorCode(), vaccine, vaccine ? this.providers.get(vx.getID()) : null, ageGroup, false));
				}
			}
			for(Detection d : possible){
				if(this.filter.in(d.getDqaErrorCode())){
					this.issues.add(new Issue(d.getDqaErrorCode(), vaccine, vaccine ? this.providers.get(vx.getID()) : null, ageGroup, true));
				}
			}
		}
	}
	
	public DqaVaccination byId(DqaMessageReceived msg, String id){
		if(id == null || id.isEmpty()){
			return null;
		}
		
		for(DqaVaccination vx : msg.getVaccinations()){
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
