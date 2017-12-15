package gov.nist.healthcare.iz.darq.controller.route;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.analysis.AnalysisRawResult;
import gov.nist.healthcare.iz.darq.batch.domain.Configuration;
import gov.nist.healthcare.iz.darq.batch.domain.JobData;
import gov.nist.healthcare.iz.darq.batch.service.JobManager;
import gov.nist.healthcare.iz.darq.controller.domain.JobCreation;
import gov.nist.healthcare.iz.darq.controller.domain.OpResult;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;


@RestController
@RequestMapping("/api")
public class JobsController {
	
	@Autowired
	@Qualifier("vType")
	private EntityType vType;
	
	@Autowired
	@Qualifier("pType")
	private EntityType pType;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private AccountService accountService;
	@Autowired
	private JobManager manager;
	
	@RequestMapping(value="/jobs", method=RequestMethod.GET)
	@ResponseBody
	public List<JobData> jobs(){
		Account a = this.accountService.getCurrentUser();
		return this.manager.getJobs(a.getUsername());
	}
	
	@RequestMapping(value="/jobs/create", method=RequestMethod.POST)
	@ResponseBody
	public JobCreation create(final HttpServletRequest request) throws Exception{
		Account a = this.accountService.getCurrentUser();
		JobData jobData = new JobData();
		InputStream patients = null;
		InputStream vaccinations = null;
		for(Part p : request.getParts()){
			if(p.getName().equals("name")){
				String name = IOUtils.toString(p.getInputStream(), Charset.forName("UTF-8")); 
				jobData.setName(name);
			}
			if(p.getName().equals("configuration")){
				String conf = IOUtils.toString(p.getInputStream(), Charset.forName("UTF-8")); 
				jobData.setConfiguration(mapper.readValue(conf, Configuration.class));
			}
			if(p.getName().equals("patient")){
				patients = p.getInputStream();
			}
			if(p.getName().equals("vaccination")){
				vaccinations = p.getInputStream();
			}
		}

		this.manager.createJob(jobData, patients, vaccinations, a.getUsername());
		return new JobCreation(true, "");
	}
	

	@RequestMapping(value="/job/{id}/result", method=RequestMethod.GET)
	@ResponseBody
	public AnalysisRawResult result(@PathVariable String id, final HttpServletRequest request) throws Exception{
		Account a = this.accountService.getCurrentUser();
		return this.manager.resultsFor(id, a.getUsername());
	}
	
	@RequestMapping(value="/job/{id}/stop", method=RequestMethod.GET)
	@ResponseBody
	public OpResult stop(@PathVariable String id, final HttpServletRequest request) throws Exception{
		Account a = this.accountService.getCurrentUser();
		boolean status = this.manager.stop(id);
		return new OpResult(status, "", "stop job");
	}
	
	@RequestMapping(value="/job/{id}", method=RequestMethod.DELETE)
	@ResponseBody
	public OpResult delete(@PathVariable String id, final HttpServletRequest request) throws Exception{
		Account a = this.accountService.getCurrentUser();
		boolean status = this.manager.delete(id, a.getUsername());
		return new OpResult(status, "", "delete job");
		
	}
	
}
