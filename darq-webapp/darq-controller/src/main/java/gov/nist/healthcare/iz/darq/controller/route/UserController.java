package gov.nist.healthcare.iz.darq.controller.route;

import com.google.common.base.Strings;
import gov.nist.healthcare.auth.domain.Account;
import gov.nist.healthcare.auth.service.AccountService;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.controller.domain.UserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AccountService accountService;

    // Get All Users
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<Account> all(@RequestBody UserRegistration account) {
        // Validate Username
        Account existing = accountService.getAccountByUsername(account.getUsername());
        if(existing != null) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Username already taken", null, "registration");
        }

        if(!account.getUsername().matches("[a-zA-Z0-9_]{5,10}")) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Your username must be 8-10 characters long, might contain letters and numbers and underscore, and must not contain spaces, special characters", null, "registration");
        }

        // Validate FullName
        if(Strings.isNullOrEmpty(account.getFullName())) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Full name is required", null, "registration");
        }

        // Validate Organization
        if(Strings.isNullOrEmpty(account.getOrganization())) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Organization is required", null, "registration");
        }

        // Validate Terms & Conditions
        if(!account.getSignedConfidentialityAgreement()) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Must accept terms & conditions", null, "registration");
        }

        // Validate Password
        if(!account.getPassword().matches("[a-zA-Z0-9@*#!'&]{8,20}")) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Your password must be 8-20 characters long, might contain upper and lower case letters and numbers and a special character (@*#!'&).", null, "registration");
        }

        // Validate Email
        if(!account.getEmail().matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$")) {
            return new OpAck<>(OpAck.AckStatus.FAILED, "Invalid Email", null, "registration");
        }

        this.accountService.createTester(account.toAccount());

        return new OpAck<>(OpAck.AckStatus.SUCCESS, "Account Created Successfully", null, "registration");
    }
}
