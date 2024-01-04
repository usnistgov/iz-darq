package gov.nist.healthcare.iz.darq.adf.module.sqlite;

import gov.nist.healthcare.iz.darq.adf.model.Metadata;
import gov.nist.healthcare.iz.darq.adf.module.api.ADFWriter;
import gov.nist.healthcare.iz.darq.adf.module.sqlite.model.ProcessingCount;
import gov.nist.healthcare.iz.darq.digest.domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleADFWriter extends ADFWriter {
	protected ProcessingCount counts = new ProcessingCount();
	protected Map<String, String> providers = new HashMap<>();
	protected Map<String, Integer> ageGroupCount = new HashMap<>();
	protected Map<String, ExtractFraction> extraction = new HashMap<>();
	protected Summary summary;
	protected IssueList issueList = new IssueList(MAX_ISSUES);
	protected Metadata metadata;
	protected ConfigurationPayload configurationPayload;

	@Override
	public void addIssue(String issue) {
		this.issueList.add(issue);
	}

	@Override
	public void addIssues(List<String> issues) {
		this.issueList.addAllPossible(issues);
	}

	@Override
	public ProcessingCount getCounts() {
		return counts;
	}

	@Override
	public List<String> getIssues() {
		return issueList.toList();
	}

	@Override
	public Map<String, String> getProviders() {
		return providers;
	}

	@Override
	public Map<String, ExtractFraction> getExtractPercent() {
		return extraction;
	}

	public void setExtractPercent(Map<String, ExtractFraction> extract) {
		this.extraction = extract;
	}

	@Override
	public Map<String, Integer> getAgeGroupCount() {
		return ageGroupCount;
	}

	@Override
	public SummaryCounts getSummaryCounts() {
		return new SummaryCounts(
				counts.nbVaccinations,
				counts.nbPatients,
				counts.unreadPatients,
				counts.unreadVaccinations,
				counts.maxVaccination,
				counts.minVaccination,
				counts.nbProviders,
				counts.nbPatients > 0 ? counts.nbVaccinations / counts.nbPatients : 0,
				0,
				0,
				0,
				counts.historical,
				counts.administered
		);
	}

	@Override
	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	@Override
	public Summary getSummary() {
		return summary;
	}

	@Override
	public void write_patient_age_group(String ageGroupId, int nb) {
		this.ageGroupCount.compute(ageGroupId, (k, v) -> {
			if(v != null) {
				return v + nb;
			} else {
				return nb;
			}
		});
	}


	protected void write_chunk(ADChunk chunk) {
		counts.addUnreadPatients(chunk.getUnreadPatients());
		counts.addUnreadVaccinations(chunk.getUnreadVaccinations());
		counts.addNbPatients(chunk.getNbPatients());
		counts.addNbVaccinations(chunk.getNbVaccinations());
		counts.setMaxVaccination(Math.max(this.counts.getMaxVaccination(), chunk.getNbVaccinations()));
		counts.setMinVaccination(Math.min(this.counts.getMinVaccination(), chunk.getNbVaccinations()));
		counts.addAdministered(chunk.getAdministered());
		counts.addHistorical(chunk.getHistorical());
		chunk.getExtraction().forEach((key, chunkValue) -> {
			this.extraction.compute(key, (k, value) -> {
				if(value != null) {
					return ExtractFraction.merge(value, chunkValue);
				} else {
					return chunkValue;
				}
			});
		});
		this.providers.putAll(chunk.getProviders());
		counts.setNbProviders(this.providers.size());
	}

	@Override
	public void write_metadata(Metadata metadata, ConfigurationPayload payload) {
		this.metadata = metadata;
		this.configurationPayload = payload;
	}

}
