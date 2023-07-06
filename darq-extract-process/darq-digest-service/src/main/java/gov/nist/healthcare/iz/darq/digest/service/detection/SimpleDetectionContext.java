package gov.nist.healthcare.iz.darq.digest.service.detection;

import gov.nist.healthcare.iz.darq.detections.DetectionContext;
import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;
import gov.nist.healthcare.iz.darq.digest.service.DetectionFilter;
import gov.nist.healthcare.iz.darq.digest.service.VaxGroupMapper;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.DigestUtils;

public class SimpleDetectionContext implements DetectionContext {

	private final AgeGroupService ageGroupService;
	private final DetectionFilter detectionFilter;
	private final VaxGroupMapper vaxGroupMapper;
	private final LocalDate evaluationDate;
	private final DateTimeFormatter dateTimeFormatter;

	public SimpleDetectionContext(AgeGroupService ageGroupService, DetectionFilter detectionFilter, VaxGroupMapper vaxGroupMapper, LocalDate evaluationDate, DateTimeFormatter dateTimeFormatter) {
		this.ageGroupService = ageGroupService;
		this.detectionFilter = detectionFilter;
		this.vaxGroupMapper = vaxGroupMapper;
		this.evaluationDate = evaluationDate;
		this.dateTimeFormatter = dateTimeFormatter;
	}

	@Override
	public LocalDate getEvaluationDate() {
		return evaluationDate;
	}

	@Override
	public String calculateAgeGroup(LocalDate dob, LocalDate evaluation) {
		return ageGroupService.getGroup(dob, evaluation);
	}

	@Override
	public String calculateAgeGroupAsOfEvaluationDate(LocalDate dob) {
		return ageGroupService.getGroup(dob, evaluationDate);
	}

	@Override
	public boolean keepDetection(String detection) {
		return this.detectionFilter.in(detection);
	}

	@Override
	public String obfuscateReportingGroup(String value) {
		return DigestUtils.md5DigestAsHex(value.getBytes());
	}

	@Override
	public String getVaccineGroupValue(String code) {
		return this.vaxGroupMapper.translate(code);
	}

	@Override
	public DateTimeFormatter getDateFormatter() {
		return dateTimeFormatter;
	}
}
