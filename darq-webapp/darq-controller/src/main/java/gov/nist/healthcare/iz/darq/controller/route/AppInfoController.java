package gov.nist.healthcare.iz.darq.controller.route;

import gov.nist.healthcare.iz.darq.model.HomePage;
import gov.nist.healthcare.iz.darq.service.impl.WebContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gov.nist.healthcare.iz.darq.controller.domain.ServerInfo;


@RestController
@RequestMapping("/public")
public class AppInfoController {

	@Autowired
	private ServerInfo info;
	@Autowired
    private WebContentService webContentService;
	
    @RequestMapping(value = "/serverInfo", method = RequestMethod.GET)
    @ResponseBody
    public ServerInfo index() {
        return this.info;
    }

    @RequestMapping(value="/home", method=RequestMethod.GET)
    @ResponseBody
    public HomePage home() {
        return this.webContentService.getWebContent().getHomePage();
    }

    @RequestMapping(value="/registerTermsAndConditions", method=RequestMethod.GET)
    @ResponseBody
    public String registerTermsAndConditions() {
        return this.webContentService.getWebContent().getRegisterTermsAndConditions();
    }

    @RequestMapping(value="/uploadTermsAndConditions", method=RequestMethod.GET)
    @ResponseBody
    public String uploadTermsAndConditions() {
        return this.webContentService.getWebContent().getUploadTermsAndConditions();
    }


}