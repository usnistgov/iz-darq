package gov.nist.healthcare.iz.darq.digest.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import gov.nist.healthcare.iz.darq.digest.domain.Barket;
import gov.nist.healthcare.iz.darq.digest.domain.Range;
import gov.nist.healthcare.iz.darq.digest.service.AgeGroupService;

public class AgeGroupCalculator implements AgeGroupService {

	List<Range> ranges;
	
	public Map<String, Range> ageGroups() {
		Map<String, Range> groups = new HashMap<>();
		int i = 0;
		for(Range r : ranges){
			groups.put((i++)+"g", r);
		}
		return groups;
	}
	
	public AgeGroupCalculator(Map<String, String> groups){
		this.ranges = new ArrayList<>();
		
		for(Entry<String, String> entry : groups.entrySet()){
			this.ranges.add(new Range(parse(entry.getKey()), parse(entry.getValue())));
		}
	}
	
	public AgeGroupCalculator(List<Range> ranges){
		this.ranges = ranges;
		this.ranges.sort(null);
	}
	

	@Override
	public List<String> getGroups(){
		return Stream.iterate(0, i -> i + 1).limit(this.ranges.size() + 1)
			   .map(i -> {
				   return i + "g";
			   })
			   .collect(Collectors.toList());
	}
	

	@Override
	public String getGroup(LocalDate from, LocalDate to) {         
		Period period = new Period(from, to, PeriodType.yearMonthDay());

		for(int i = 0; i < this.ranges.size(); i++){
			if(inside(period, this.ranges.get(i))){
				return i+"g";
			}
		}
		
		return this.ranges.size()+"g";
	}
	

	@Override
	public boolean inside(Period period, Range range){
		return this.afterOrEqual(period, range) && this.before(period, range);
	}

	private boolean afterOrEqual(Period period, Range range) {
		if(period.getYears() > range.min.year) {
			return true;
		} else if(period.getYears() == range.min.year) {
			if(period.getMonths() > range.min.month) {
				return true;
			} else if(period.getMonths() == range.min.month) {
				if(period.getDays() > range.min.day) {
					return true;
				} else if(period.getDays() == range.min.day) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean before(Period period, Range range) {
		if(period.getYears() < range.max.year) {
			return true;
		} else if(period.getYears() == range.max.year) {
			if(period.getMonths() < range.max.month) {
				return true;
			} else if(period.getMonths() == range.max.month) {
				if(period.getDays() < range.max.day) {
					return true;
				} else if(period.getDays() == range.max.day) {
					return false;
				}
			}
		}
		return false;
	}
	
	public Barket parse(String str){
		String[] parts = str.split(" ");
		Barket b = new Barket(0,0,0);
		
		for(String part : parts){
			if(part.contains("y")){
				int y = Integer.parseInt(part.replace("y", ""));
				b.year = y;
			}
			
			if(part.contains("m")){
				int m = Integer.parseInt(part.replace("m", ""));
				b.month = m;
			}
			
			if(part.contains("d")){
				int d = Integer.parseInt(part.replace("d", ""));
				b.day = d;
			}
		}
		
		return b;
	}

}
