package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import gov.nist.healthcare.iz.darq.digest.domain.Range;
import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;

public class AgeGroupCalculator implements AgeGroupService {

	private final int numberOfAgeGroups;
	private final Map<String, Range> ageGroupsById;
	private final String overflowGroupId;

	public AgeGroupCalculator(List<Range> ranges){
		if(ranges == null) {
			ranges = Collections.emptyList();
		}
		ranges.sort(null);
		this.ageGroupsById = new HashMap<>();
		for(int i = 0; i < ranges.size(); i++){
			ageGroupsById.put(i+"g", ranges.get(i));
		}
		this.numberOfAgeGroups = ranges.size();
		this.overflowGroupId = this.numberOfAgeGroups + "g";
	}

	@Override
	public List<String> getGroups(){
		return Stream.iterate(0, i -> i + 1).limit(this.numberOfAgeGroups + 1)
			   .map(i -> i + "g")
			   .collect(Collectors.toList());
	}

	@Override
	public String getGroup(LocalDate from, LocalDate to) {         
		Period period = new Period(from, to, PeriodType.months());
		return this.ageGroupsById.entrySet()
				.stream()
				.filter((group) -> this.inside(period, group.getValue()))
				.findFirst()
				.map(Map.Entry::getKey)
				.orElse(overflowGroupId);
	}

	@Override
	public boolean inside(Period period, Range range){
		return this.afterOrEqual(period, range) && this.before(period, range);
	}

	private boolean afterOrEqual(Period period, Range range) {
		return period.getMonths() >= range.getMin().getMonthsValue();
	}

	private boolean before(Period period, Range range) {
		return period.getMonths() < range.getMax().getMonthsValue();
	}

}
