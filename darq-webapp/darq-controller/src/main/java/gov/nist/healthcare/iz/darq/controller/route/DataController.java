package gov.nist.healthcare.iz.darq.controller.route;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.engine.MessageValidator;
import org.immregistries.mqe.validator.engine.ValidationRule;
import org.immregistries.mqe.validator.engine.rules.ValidationRuleEntityLists;
import org.immregistries.mqe.vxu.VxuObject;
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
	
    @RequestMapping(value = "/detections", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, DetectionDescriptor> detections() {
    	Map<String, DetectionDescriptor> detections = new HashMap<>();
    	for(Detection d : MessageValidator.activeDetections()) {
    		detections.put(d.getMqeMqeCode(), new DetectionDescriptor(d.getDisplayText(),d.getTargetObject().toString()));
    	}
        return detections;
    }
    
    @RequestMapping(value = "/cvx", method = RequestMethod.GET)
    @ResponseBody
    public List<CVXCode> cvx() {
    	return this.cvx.findAll();
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