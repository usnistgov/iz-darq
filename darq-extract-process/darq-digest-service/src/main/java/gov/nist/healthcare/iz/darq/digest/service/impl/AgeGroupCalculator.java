package gov.nist.healthcare.iz.darq.digest.service.impl;

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

	List<Range> ranges;
	private final Map<String, Range> ageGroupsById;
	private final String overflowGroupId;

	public AgeGroupCalculator(List<Range> ranges){
		this.ranges = ranges;
		this.ranges.sort(null);
		this.ageGroupsById = new HashMap<>();
		for(int i = 0; i < this.ranges.size(); i++){
			ageGroupsById.put(i+"g", this.ranges.get(i));
		}
		this.overflowGroupId = this.ranges.size() + "g";
	}

	@Override
	public List<String> getGroups(){
		return Stream.iterate(0, i -> i + 1).limit(this.ranges.size() + 1)
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
