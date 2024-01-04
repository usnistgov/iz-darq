package gov.nist.healthcare.iz.darq.controller.route;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import gov.nist.healthcare.iz.darq.detections.AvailableDetectionEngines;
import gov.nist.healthcare.iz.darq.model.FileDescriptorWrapper;
import gov.nist.healthcare.iz.darq.model.qDARJarFile;
import gov.nist.healthcare.iz.darq.patient.matching.model.PatientMatchingDetection;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.impl.SimpleDownloadService;
import gov.nist.healthcare.iz.darq.service.utils.CodeSetService;
import org.apache.commons.io.IOUtils;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.engine.rules.ValidationRuleEntityLists;
import org.immregistries.mqe.vxu.TargetType;
import org.immregistries.mqe.vxu.VxuObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.iz.darq.detections.DetectionDescriptor;
import gov.nist.healthcare.iz.darq.model.CVXCode;
import gov.nist.healthcare.iz.darq.model.FileDescriptor;
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
		AvailableDetectionEngines.ALL_DETECTION_DESCRIPTORS.forEach((descriptor) -> {
			this.detectionsMap.put(descriptor.getCode(), descriptor);
		});

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
    
    @RequestMapping(value = "/download/file/{id}", method = RequestMethod.GET)
    public void download(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
    	InputStream io = this.download.getFile(id);
    	FileDescriptor file = this.download.getInfo(id);
    	if(io != null){
    		response.setContentType(file.getType());
    		response.setHeader("Content-disposition", "attachment;filename="+file.getName());
    		response.getOutputStream().write(IOUtils.toByteArray(io));
    	}
    	else {
    		response.sendError(404);
    	}
    }

	@RequestMapping(value = "/download/cli", method = RequestMethod.GET)
	public void downloadCLI(HttpServletResponse response) throws Exception {
		qDARJarFile file = this.download.getJarFileInfo();
		response.setContentType("application/java-archive");
		response.setHeader("Content-disposition", "attachment;filename="+ SimpleDownloadService.RESOURCES_JAR_FILE);
		response.getOutputStream().write(IOUtils.toByteArray(new FileInputStream(file.getLocation().toFile())));
	}

	@RequestMapping(value = "/download/cli/info", method = RequestMethod.GET)
	@ResponseBody
	public qDARJarFile cliInfo(HttpServletResponse response) throws NotFoundException {
		qDARJarFile file = this.download.getJarFileInfo();
		if(file == null) {
			throw  new NotFoundException("Jar file not found");
		} else {
			return file;
		}
	}
    
    @RequestMapping(value = "/download/files", method = RequestMethod.GET)
    @ResponseBody
    public List<FileDescriptorWrapper> downloads(HttpServletResponse response) throws IOException {
    	return this.download.catalog();
    }

}