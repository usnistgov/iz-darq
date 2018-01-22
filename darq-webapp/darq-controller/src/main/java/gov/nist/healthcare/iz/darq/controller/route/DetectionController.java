package gov.nist.healthcare.iz.darq.controller.route;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.immregistries.dqa.validator.issue.MessageAttribute;
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
import gov.nist.healthcare.iz.darq.controller.domain.Issue;
import gov.nist.healthcare.iz.darq.controller.domain.JobCreation;
import gov.nist.healthcare.iz.darq.controller.domain.OpResult;
import gov.nist.lightdb.domain.EntityTypeRegistry.EntityType;


@RestController
@RequestMapping("/api")
public class DetectionController {
	
	
	@RequestMapping(value="/detections", method=RequestMethod.GET)
	@ResponseBody
	public List<Issue> jobs(){
		List<Issue> issues = new ArrayList<>();
		for(MessageAttribute ma : MessageAttribute.values()){
			issues.add(new Issue(ma.getDqaErrorCode(), ma.getDisplayText(), ma.getHl7Locations().toString()));
		}
		return issues;
	}
	
}
