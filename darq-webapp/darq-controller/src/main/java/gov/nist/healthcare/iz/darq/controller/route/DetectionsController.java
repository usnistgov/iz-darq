package gov.nist.healthcare.iz.darq.controller.route;

import java.util.HashMap;
import java.util.Map;

import org.immregistries.dqa.validator.issue.Detection;
import org.immregistries.dqa.validator.issue.IssueObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.iz.darq.controller.domain.DetectionDescriptor;
import gov.nist.healthcare.iz.darq.controller.domain.ServerInfo;

@RestController
@RequestMapping("/public")
public class DetectionsController {
	
    @RequestMapping(value = "/detections", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, DetectionDescriptor> index() {
    	Map<String, DetectionDescriptor> detections = new HashMap<>();
    	for(Detection d : Detection.values()) {
    		if(d.getTargetObject().equals(IssueObject.PATIENT) || d.getTargetObject().equals(IssueObject.NEXT_OF_KIN) || d.getTargetObject().equals(IssueObject.VACCINATION)){
    			detections.put(d.getDqaErrorCode(), new DetectionDescriptor(d.getDisplayText(),d.getTargetObject().toString()));
    		}
    	}
        return detections;
    }

}