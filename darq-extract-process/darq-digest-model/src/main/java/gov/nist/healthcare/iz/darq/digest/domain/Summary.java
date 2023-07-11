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

	public Summary(ADChunk chunk, ConfigurationPayload payload){
		super();
		List<Range> groups = payload.getAgeGroups();
		this.asOfDate = payload.getAsOf();
		this.issues = chunk.issueList();
		this.countByAgeGroup = new ArrayList<>();
		Map<String, Integer> ageCounts = new HashMap<>();
		for(String ageGroup : chunk.getGeneralPatientPayload().keySet()){
			ageCounts.put(ageGroup, chunk.getGeneralPatientPayload().get(ageGroup).getCount());
		}
		extract = new HashMap<>();
		for(String e : chunk.getExtraction().keySet()){
			extract.put(e, new ExtractPercent(chunk.getExtraction().get(e)));
		}
		int i = 0;
		for(Range range : groups.stream().sorted().collect(Collectors.toList())){
			this.countByAgeGroup.add(new AgeGroupCount(range, ageCounts.getOrDefault(i+"g", 0)));
			i++;
		}
		this.outOfRange = ageCounts.getOrDefault(groups.size()+"g", 0);
		this.countByAgeGroup.sort(null);
		counts = new SummaryCounts();
		counts.totalReadPatientRecords = chunk.getNbPatients();
		counts.totalReadVaccinations = chunk.getNbVaccinations();
		counts.totalSkippedPatientRecords = chunk.getUnreadPatients();
		counts.totalSkippedVaccinationRecords = chunk.getUnreadVaccinations();
		counts.maxVaccinationsPerRecord = chunk.getMaxVaccination();
		counts.avgVaccinationsPerRecord = counts.totalReadPatientRecords > 0 ? counts.totalReadVaccinations / counts.totalReadPatientRecords : 0;
		counts.numberOfProviders = chunk.getProviders().size();
		counts.minVaccinationsPerRecord = chunk.getMinVaccination();
		counts.historical = chunk.getHistorical();
		counts.administered = chunk.getAdministered();
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
