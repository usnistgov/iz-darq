package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.digest.domain.TablePayload;
import gov.nist.healthcare.iz.darq.digest.service.model.Issue;
import gov.nist.healthcare.iz.darq.digest.service.model.VxInfo;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.detection.ValidationReport;
import org.immregistries.mqe.validator.engine.MessageValidator;
import org.immregistries.mqe.validator.engine.ValidationRuleResult;
import org.immregistries.mqe.vxu.MqeMessageReceived;
import org.immregistries.mqe.vxu.MqeVaccination;
import org.immregistries.mqe.vxu.TargetType;
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

	private final MessageValidator validator = MessageValidator.INSTANCE;
	private final MQETransformService transformer;
	private final List<Issue> issues;
	private final List<VxInfo> vx;
	private final Map<String, String> providers;
	private final AgeGroupService ageGroupCalculator;
	private final DetectionFilter filter;
	private final VaxGroupMapper vaxMapper;
	private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
	private int historical;
	private int administered;

	public DQAValidator(AgeGroupService ageGroupCalculator, DetectionFilter filter, VaxGroupMapper vaxMapper) {
		super();
		transformer = new MQETransformService();
		this.issues = new ArrayList<>();
		this.providers = new HashMap<>();
		this.vx = new ArrayList<>();
		this.ageGroupCalculator = ageGroupCalculator;
		this.filter = filter;
		this.vaxMapper = vaxMapper;
	}

	void validateRecord(AggregatePatientRecord apr, LocalDate date){
		this.historical = 0;
		this.administered = 0;

		// Transform APR
		TransformResult<VaccineRecord, MqeVaccination, MqeMessageReceived> tresult = this.transformer.transform(apr);
		MqeMessageReceived msg = tresult.getPayload();

		// DOB and Patient AgeGroup
		LocalDate DOB = apr.patient.date_of_birth.getValue();
		String currentAgeGroup = this.ageGroupCalculator.getGroup(DOB, date);

		// For each Vaccination
		for(MqeVaccination vx : msg.getVaccinations()){
			this.providers.put(
					vx.getPositionId() + "",
					tresult.getAFromB(vx.getPositionId()+"").reporting_group.getValue());
			
			// Age Group relative to Administered date 
			String vaccinationAtAgeGroup = this.ageGroupCalculator.getGroup(DOB, LocalDate.parse(vx.getAdminDateString(), dateFormat));
			
			// Adding vaccination to vaccine list
			this.vx.add(new VxInfo(tresult.getAFromB(vx.getPositionId()+"").reporting_group.getValue(), vaccinationAtAgeGroup, this.vaxMapper.translate(vx.getAdminCvxCode()), msg.getPatient().getSex(), vx.getAdminDateString().substring(0,4), vx.getInformationSourceCode()));

			if(vx.isAdministered()) {
				this.administered++;
			} else {
				this.historical++;
			}
		}
		
		List<ValidationRuleResult> results = new ArrayList<>();

		try {
			results = this.validator.validateMessageNIST(msg);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Set<Issue> patientIssues = new HashSet<>();
		Map<String, Set<Issue>> deduplicate = new HashMap<>();

		// For each Result
		for(ValidationRuleResult r : results){
			
			// Get Possible Detections for Rule
			Set<Detection> possible = r.getPossible().stream().filter(Objects::nonNull).collect(Collectors.toSet());
			
			// Rule target is vaccination
			boolean vaccine = r.getTargetType() == TargetType.Vaccination;
			
			// Get targeted vaccination
			MqeVaccination vx = byId(msg, r.getPositionId());
			
			// If the rule targets a vaccination set age group relative to admin date else use the age group relative to today
			String ageGroup = vaccine ? this.ageGroupCalculator.getGroup(DOB, LocalDate.parse(vx.getAdminDateString(), dateFormat)) : currentAgeGroup;

			for(ValidationReport vi : r.getIssues()){
				
				// Check if detection is is configuration
				if(this.filter.in(vi.getDetection().getMqeMqeCode())){
				
					// Remove from affirmatives
					possible.remove(vi.getDetection());
					
					// Create an issue
					Issue issue = new Issue(vi.getDetection().getMqeMqeCode(), vaccine, vaccine ? this.providers.get(vx.getPositionId() + "") : null, ageGroup, false);
	
					if(vaccine) {
						if(deduplicate.containsKey(r.getPositionId() + "")) {
							deduplicate.get(r.getPositionId() + "").add(issue);
						} else {
							deduplicate.put(r.getPositionId() + "", new HashSet<Issue>(Collections.singletonList(issue)));
						}
					} else {
						
						// Add issue to list of issues as negative
						patientIssues.add(issue);
					}
				}
			}
			
			
			for(Detection d : possible){
				
				// Check if detection is configuration
				if(this.filter.in(d.getMqeMqeCode())){
					
					// Create an issue
					Issue issue = new Issue(d.getMqeMqeCode(), vaccine, vaccine ? this.providers.get(vx.getPositionId() + "") : null, ageGroup, true);
					
					if(vaccine) {
						if(deduplicate.containsKey(r.getPositionId() + "")) {
							deduplicate.get(r.getPositionId() + "").add(issue);
						} else {
							deduplicate.put(r.getPositionId() + "",  new HashSet<Issue>(Collections.singletonList(issue)));
						}
					} else {
						
						// Add issue to list of issues as positive
						patientIssues.add(issue);
					}
				} 
			}
		}
		
		this.issues.addAll(deduplicate.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
		this.issues.addAll(patientIssues);
		
	}
	
	private MqeVaccination byId(MqeMessageReceived msg, int id){
		for(MqeVaccination vx : msg.getVaccinations()){
			if(vx.getPositionId() == id){
				return vx;
			}
		}
		return null;
	}
	
	//------ PROVIDER ----- AGE ------ CODE ----- PAYLOAD
	Map<String, Map<String, Map<String, DetectionSum>>> vaccinationDetections(){
		return this.issues.stream().filter(Issue::isVaccine)
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
	Map<String, Map<String, DetectionSum>> patientDetections(){
		return this.issues.stream().filter(c -> !c.isVaccine())
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
	
	//------- PROVIDER ---- AGE ------- YEAR ------ GENDER ---- SOURCE -- NB
	Map<String, Map<String, Map<String, Map<String, Map<String, TablePayload>>>>> getVxInfoTable() {
		return this.vx.stream()
				.collect(
						Collectors.groupingBy(
								VxInfo::getProvider,
								Collectors.groupingBy(
										VxInfo::getAgeGroup,
										Collectors.groupingBy(
												VxInfo::getYear,
												Collectors.groupingBy(
														VxInfo::getGender,
														Collectors.groupingBy(
																VxInfo::getSource,
																Collectors.collectingAndThen(
																		Collectors.toList(),
																		(x) -> {
																			TablePayload t = new TablePayload();
																			t.setTotal(x.size());
																			t.setCodes(
																					x.stream()
																							.collect(
																									Collectors.groupingBy(
																											VxInfo::getCode,
																											Collectors.collectingAndThen(
																													Collectors.toList(),
																													List::size
																											)
																									)
																							)
																			);
																			return t;
																		}
																)
														)
												)
										)
								)
						)
				);
	}
	

	Map<Field, Set<String>> vocabulary(){
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
	
	private Set<String> keySet(Function<? super VxInfo, String> func){
		return this.vx.stream()
				.collect(
						Collectors.groupingBy(
								func
						)
				).keySet();
	}

	public int getHistorical() {
		return historical;
	}

	public int getAdministered() {
		return administered;
	}
}
