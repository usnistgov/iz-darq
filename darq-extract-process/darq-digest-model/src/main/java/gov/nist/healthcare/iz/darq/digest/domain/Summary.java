package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Summary {

	private List<String> issues;
	private List<AgeGroupCount> countByAgeGroup;
	private int outOfRange = 0; 
	private SummaryCounts counts;
	private String asOfDate;
	private Map<String, ExtractPercent> extract;

	public Summary( List<String> issues,
	                Map<String, Integer> countByAgeGroup,
	                SummaryCounts counts,
	                Map<String, ExtractFraction> extract,
	                ConfigurationPayload payload
	) {
		// -- Set Extract Percentages
		this.extract = new HashMap<>();
		for(String e : extract.keySet()){
			this.extract.put(e, new ExtractPercent(extract.get(e)));
		}

		// -- Set Age Group Counts
		int i = 0;
		this.countByAgeGroup = new ArrayList<>();
		List<Range> groups = payload.getAgeGroups();
		for(Range range : groups.stream().sorted().collect(Collectors.toList())){
			this.countByAgeGroup.add(new AgeGroupCount(range, countByAgeGroup.getOrDefault(i+"g", 0)));
			i++;
		}
		this.outOfRange = countByAgeGroup.getOrDefault(groups.size()+"g", 0);
		this.countByAgeGroup.sort(null);

		this.counts = counts;
		this.issues = issues;
		this.asOfDate = payload.getAsOf();
	}

	public static Summary merge(Summary source, Summary target) {
		Summary result = new Summary();
		result.setCounts(SummaryCounts.merge(source.getCounts(), target.getCounts()));
		result.setOutOfRange(source.outOfRange + target.outOfRange);
		result.setCountByAgeGroup(source.countByAgeGroup.stream().map((countByAgeGroup) -> {
			AgeGroupCount count = new AgeGroupCount();
			count.setRange(countByAgeGroup.range);
			count.setNb(countByAgeGroup.nb + target.countByAgeGroup.stream().filter((t) -> t.getRange().equals(count.range)).findFirst().map(AgeGroupCount::getNb).get());
			return count;
		}).collect(Collectors.toList()));
		Map<String, ExtractPercent> extractPercentMap = new HashMap<>();
		source.getExtract().forEach((k, v) -> {
			extractPercentMap.put(k, ExtractPercent.merge(v, target.getExtract().get(k)));
		});
		result.setExtract(extractPercentMap);
		return result;
	}
	
	public Summary() {
		super();
	}

	public String getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}

	public Map<String, ExtractPercent> getExtract() {
		return extract;
	}

	public void setExtract(Map<String, ExtractPercent> extract) {
		this.extract = extract;
	}

	public List<String> getIssues() {
		return issues;
	}

	public void setIssues(List<String> issues) {
		this.issues = issues;
	}

	public List<AgeGroupCount> getCountByAgeGroup() {
		return countByAgeGroup;
	}

	public void setCountByAgeGroup(List<AgeGroupCount> countByAgeGroup) {
		this.countByAgeGroup = countByAgeGroup;
	}

	public int getOutOfRange() {
		return outOfRange;
	}

	public void setOutOfRange(int outOfRange) {
		this.outOfRange = outOfRange;
	}

	public SummaryCounts getCounts() {
		return counts;
	}

	public void setCounts(SummaryCounts counts) {
		this.counts = counts;
	}
}
