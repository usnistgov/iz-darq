package gov.nist.healthcare.iz.darq.controller.route;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.iz.darq.service.utils.CodeSetService;
import org.apache.commons.io.IOUtils;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.engine.MessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.iz.darq.controller.domain.DetectionDescriptor;
import gov.nist.healthcare.iz.darq.model.CVXCode;
import gov.nist.healthcare.iz.darq.model.FileDownload;
import gov.nist.healthcare.iz.darq.repository.CVXRepository;
import gov.nist.healthcare.iz.darq.service.utils.DownloadService;

@RestController
@RequestMapping("/public")
public class DataController {
	
	@Autowired
	private CVXRepository cvx;
	@Autowired
	private DownloadService download;
	@Autowired
	private CodeSetService codeSet;

	private Map<String, DetectionDescriptor> detectionsMap;
	private List<String> patientCodeSet;
	private List<String> vaccinationCodeSet;

	private List<CVXCode> cvxCodes;

	@PostConstruct
	private void init() throws IllegalAccessException {
		// Detections
		this.detectionsMap = new HashMap<>();
		Set<Detection> all = new HashSet<>(Arrays.asList(Detection.values()));
		//Set<Detection> active = MessageValidator.activeDetections();
		//for(Detection d : all) {
		//	this.detectionsMap.put(d.getMqeMqeCode(), new DetectionDescriptor(d.getDisplayText(),d.getTargetObject().toString(), active.contains(d)));
		//}

		//CodeSets
		this.patientCodeSet = codeSet.patientCodes();
		this.vaccinationCodeSet = codeSet.vaccinationCodes();

		//CVX
		this.cvxCodes =  this.cvx.findAll();
	}

	@RequestMapping(value = "/detections", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, DetectionDescriptor> detections() {
        return this.detectionsMap;
    }

	// Get Code Set
	@RequestMapping(value="/codesets/{id}", method=RequestMethod.GET)
	@ResponseBody
	public List<String> codeset(@PathVariable("id") String id) {
		if(id.equals("patient")){
			return this.patientCodeSet;
		}
		else {
			return this.vaccinationCodeSet;
		}
	}

	@RequestMapping(value = "/cvx", method = RequestMethod.GET)
    @ResponseBody
    public List<CVXCode> cvx() {
    	return this.cvxCodes;
    }
    
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public void download(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
    	InputStream io = this.download.getFile(id);
    	FileDownload file = this.download.getInfo(id);
    	if(io != null){
    		response.setContentType(file.getType());
    		response.setHeader("Content-disposition", "attachment;filename="+file.getName());
    		response.getOutputStream().write(IOUtils.toByteArray(io));
    	}
    	else {
    		response.sendError(404);
    	}
    }
    
    @RequestMapping(value = "/downloads", method = RequestMethod.GET)
    @ResponseBody
    public List<FileDownload> downloads(HttpServletResponse response) throws IOException {
    	return this.download.catalog();
    }

}
