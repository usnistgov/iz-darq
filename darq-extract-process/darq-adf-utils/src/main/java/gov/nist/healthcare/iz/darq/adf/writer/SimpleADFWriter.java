package gov.nist.healthcare.iz.darq.adf.writer;

import gov.nist.healthcare.iz.darq.digest.domain.ADChunk;
import gov.nist.healthcare.iz.darq.digest.domain.ExtractFraction;
import gov.nist.healthcare.iz.darq.digest.domain.IssueList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleADFWriter extends ADFWriter {
	protected ProcessingCount counts = new ProcessingCount();
	protected Map<String, String> providers = new HashMap<>();
	protected Map<String, ExtractFraction> extraction = new HashMap<>();
	protected IssueList issueList = new IssueList(MAX_ISSUES);

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

	protected int getId(Dictionary dictionary, String value) {
		return dictionary.getId(value);
	}

	protected void write_metadata(ADChunk chunk) {
		counts.addUnreadPatients(chunk.getUnreadPatients());
		counts.addUnreadVaccinations(chunk.getUnreadVaccinations());
		counts.addNbPatients(chunk.getNbPatients());
		counts.addNbVaccinations(chunk.getNbVaccinations());
		counts.setMaxVaccination(Math.max(this.counts.getMaxVaccination(), chunk.getMaxVaccination()));
		counts.setMinVaccination(Math.min(this.counts.getMinVaccination(),chunk.getMinVaccination()));
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
	}
}
