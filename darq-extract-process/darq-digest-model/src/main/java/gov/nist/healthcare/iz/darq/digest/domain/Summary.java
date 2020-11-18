package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Summary {

	public static class ExtractPercent {
		public double valued;
		public double excluded;
		public double notCollected;
		public double notExtracted;
		public double valuePresent;
		public double valueNotPresent;
		public double valueLength;
		public double empty;
		public double total;

		public ExtractPercent(ExtractFraction fraction) {
			this.valued = ((double)fraction.getValued() / fraction.getTotal()) * 100;
			this.excluded = ((double) fraction.getExcluded() / fraction.getTotal()) * 100;
			this.notCollected = ((double) fraction.getNotCollected() / fraction.getTotal()) * 100;
			this.notExtracted = ((double) fraction.getNotExtracted() / fraction.getTotal()) * 100;
			this.valuePresent = ((double) fraction.getValuePresent() / fraction.getTotal()) * 100;
			this.valueNotPresent = ((double) fraction.getValueNotPresent() / fraction.getTotal()) * 100;
			this.valueLength = ((double) fraction.getValueLength() /fraction.getTotal()) * 100;
			this.empty = ((double) fraction.getEmpty() / fraction.getTotal()) * 100;
			this.total = fraction.getTotal();
		}

		public static ExtractPercent merge(ExtractPercent source, ExtractPercent target) {
			ExtractPercent result = new ExtractPercent();
			result.valued = (source.valued + target.valued) / 2.0;
			result.excluded = (source.excluded + target.excluded) / 2.0;
			result.notCollected = (source.notCollected + target.notCollected) / 2.0;
			result.notExtracted = (source.notExtracted + target.notExtracted) / 2.0;
			result.valuePresent = (source.valuePresent + target.valuePresent) / 2.0;
			result.valueNotPresent = (source.valueNotPresent + target.valueNotPresent) / 2.0;
			result.valueLength = (source.valueLength + target.valueLength) / 2.0;
			result.empty = (source.empty + target.empty) / 2.0;
			result.total = source.total + target.total;
			return result;
		}

		public ExtractPercent() {
			super();
		}

		public double getValued() {
			return valued;
		}

		public void setValued(double valued) {
			this.valued = valued;
		}

		public double getExcluded() {
			return excluded;
		}

		public void setExcluded(double excluded) {
			this.excluded = excluded;
		}

		public double getNotCollected() {
			return notCollected;
		}

		public void setNotCollected(double notCollected) {
			this.notCollected = notCollected;
		}

		public double getNotExtracted() {
			return notExtracted;
		}

		public void setNotExtracted(double notExtracted) {
			this.notExtracted = notExtracted;
		}

		public double getValuePresent() {
			return valuePresent;
		}

		public void setValuePresent(double valuePresent) {
			this.valuePresent = valuePresent;
		}

		public double getValueNotPresent() {
			return valueNotPresent;
		}

		public void setValueNotPresent(double valueNotPresent) {
			this.valueNotPresent = valueNotPresent;
		}

		public double getValueLength() {
			return valueLength;
		}

		public void setValueLength(double valueLength) {
			this.valueLength = valueLength;
		}

		public double getEmpty() {
			return empty;
		}

		public void setEmpty(double empty) {
			this.empty = empty;
		}

		public double getTotal() {
			return total;
		}

		public void setTotal(double total) {
			this.total = total;
		}
	}

	private List<String> issues;
	private List<AgeGroupCount> countByAgeGroup;
	private int outOfRange = 0; 
	private SummaryCounts counts;
	private String asOfDate;
	private Map<String, ExtractPercent> extract;
	private Map<String, String> cvxAbstraction;
	
	public Summary(ADChunk chunk, ConfigurationPayload payload){
		super();
		Map<String, String> cvxAbstraction = payload.getVaxCodeAbstraction();
		List<Range> groups = payload.getAgeGroups();
		this.asOfDate = payload.getAsOf();
		this.issues = chunk.issueList();
		this.countByAgeGroup = new ArrayList<>();
		Map<String, Integer> ageCounts = new HashMap<>();
		for(String ageGroup : chunk.getPatientSection().keySet()){
			ageCounts.put(ageGroup, chunk.getPatientSection().get(ageGroup).getCount());
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
		this.cvxAbstraction = cvxAbstraction;
		counts.totalReadPatientRecords = chunk.getNbPatients();
		counts.totalReadVaccinations = chunk.getNbVaccinations();
		counts.totalSkippedPatientRecords = chunk.getUnreadPatients();
		counts.totalSkippedVaccinationRecords = chunk.getUnreadVaccinations();
		counts.maxVaccinationsPerRecord = chunk.getMaxVaccination();
		counts.avgVaccinationsPerRecord = counts.totalReadPatientRecords > 0 ? counts.totalReadVaccinations / counts.totalReadPatientRecords : 0;
		counts.numberOfProviders = chunk.getProviders().size();
		counts.minVaccinationsPerRecord = chunk.getMinVaccination();
	}

	public static Summary merge(Summary source, Summary target) {
		Summary result = new Summary();
		result.setCounts(SummaryCounts.merge(source.getCounts(), target.getCounts()));
		result.setOutOfRange(source.outOfRange + target.outOfRange);
		result.setCountByAgeGroup(source.countByAgeGroup.stream().map((countByAgeGroup) -> {
			AgeGroupCount count = new AgeGroupCount();
			count.setRange(countByAgeGroup.range);
			count.setNb(count.nb + target.countByAgeGroup.stream().filter((t) -> t.getRange().equals(count.range)).findFirst().map(AgeGroupCount::getNb).get());
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
	public Map<String, String> getCvxAbstraction() {
		return cvxAbstraction != null ? cvxAbstraction : new HashMap<>();
	}
	public void setCvxAbstraction(Map<String, String> cvxAbstraction) {
		this.cvxAbstraction = cvxAbstraction;
	}
	public SummaryCounts getCounts() {
		return counts;
	}
	public void setCounts(SummaryCounts counts) {
		this.counts = counts;
	}
}
