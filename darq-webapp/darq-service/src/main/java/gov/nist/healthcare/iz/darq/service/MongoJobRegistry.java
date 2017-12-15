package gov.nist.healthcare.iz.darq.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.healthcare.iz.darq.batch.domain.JobData;
import gov.nist.healthcare.iz.darq.batch.service.JobRegistry;
import gov.nist.healthcare.iz.darq.repository.JobDataRepository;

@Component
public class MongoJobRegistry implements JobRegistry {

	@Autowired
	private JobDataRepository repository;

	@Override
	public JobData save(JobData data) {
		return repository.save(data);
	}

	@Override
	public JobData get(String id) {
		return repository.findOne(id);
	}

	@Override
	public List<JobData> getJobs(String user) {
		return repository.findByUser(user);
	}

	@Override
	public JobData delete(String id) {
		repository.delete(id);
		return null;
	}
	
	
}
