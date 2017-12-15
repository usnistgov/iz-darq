package gov.nist.healthcare.iz.darq.controller.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.nist.healthcare.iz.darq.controller.domain.ServerInfo;

@RestController
@RequestMapping("/public")
public class AppInfoController {

	@Autowired
	private ServerInfo info;
	
    @RequestMapping(value = "/serverInfo", method = RequestMethod.GET)
    @ResponseBody
    public ServerInfo index() {
        return this.info;
    }

}