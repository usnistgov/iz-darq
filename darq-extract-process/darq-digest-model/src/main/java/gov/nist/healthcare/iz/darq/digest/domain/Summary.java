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
	private Map<String, Double> extract;
	
	public Summary(ADChunk chunk, List<Range> groups){
		super();
		this.issues = chunk.getIssues();
		this.countByAgeGroup = new ArrayList<>();
		Map<String, Integer> ageCounts = new HashMap<>();
		for(String ageGroup : chunk.getPatientSection().keySet()){
			ageCounts.put(ageGroup, chunk.getPatientSection().get(ageGroup).getCount());
		}
		extract = new HashMap<>();
		for(String e : chunk.getExtraction().keySet()){
			extract.put(e, chunk.getExtraction().get(e).percent());
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
		counts.totalSkippedPatientRecords = chunk.getUnread();
		counts.maxVaccinationsPerRecord = chunk.getMaxVaccination();
		counts.avgVaccinationsPerRecord = counts.totalReadVaccinations / counts.totalReadPatientRecords;
		counts.numberOfProviders = chunk.getProviders().size();
		counts.minVaccinationsPerRecord = chunk.getMinVaccination();
	}
	
	public Summary() {
		super();
	}

	
	public Map<String, Double> getExtract() {
		return extract;
	}

	public void setExtract(Map<String, Double> extract) {
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
