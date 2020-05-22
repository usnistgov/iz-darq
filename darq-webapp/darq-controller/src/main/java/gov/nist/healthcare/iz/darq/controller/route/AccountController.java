package gov.nist.healthcare.iz.darq.controller.route;

import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.iz.darq.model.FacilityDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Get All Users
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<Account> all() {
        Account current = this.accountService.getCurrentUser();
        if(current.getPrivileges().stream().anyMatch((p) -> p.getRole().equals("ADMIN"))) {
            return this.accountService.getAllUsersExceptAdmins();
        }
        throw new InsufficientAuthenticationException("Unauthorized action");
    }
}
