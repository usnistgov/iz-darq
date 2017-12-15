package gov.nist.healthcare.iz.darq.batch.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import gov.nist.healthcare.iz.darq.batch.domain.JobData;

public class InMemoryJobRepository implements JobRegistry {
	
	private List<JobData> repo = new ArrayList<JobData>();

	@Override
	public JobData save(JobData data) {
		if(data.getId() == null){
			data.setId(UUID.randomUUID().toString());
			this.repo.add(data);
		}
		else {
			Optional<JobData> jobData = this.repo.stream().filter(x -> x.getId().equals(data.getId())).findFirst();
			if(jobData.isPresent()){
				this.repo.remove(jobData.get());
				this.repo.add(jobData.get());
			}
			else {
				this.repo.add(data);
			}
		}
		return data;
	}

	@Override
	public JobData get(String id) {
		Optional<JobData> jobData = this.repo.stream().filter(x -> x.getId().equals(id)).findFirst();
		if(jobData.isPresent()){
			return jobData.get();
		}
		else {
			return null;
		}
	}

	@Override
	public List<JobData> getJobs(String user) {
		return this.repo.stream().filter(x -> user.equals(x.getUser())).collect(Collectors.toList());
	}

	@Override
	public JobData delete(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
