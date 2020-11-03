package gov.nist.healthcare.iz.darq.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/aart/error")
public class AARTConnectErrorController {

    @RequestMapping(value = "/email-exists")
    public String emailExists() {
        return ModelAn"email-exists-error.html";
    }

    @RequestMapping(value = "/")
    public String error() {
        return "error.html";
    }

}
