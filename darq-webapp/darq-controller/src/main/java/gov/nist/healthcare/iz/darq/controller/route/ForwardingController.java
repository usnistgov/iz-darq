package gov.nist.healthcare.iz.darq.controller.route;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class ForwardingController { 

    @RequestMapping(value = "/**/{path:[^.]*}")
    public String redirect() {
        return "forward:/";
    }

    @RequestMapping(value = "/")
    public String slash() {
        return "index.html";
    }

}