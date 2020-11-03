package gov.nist.healthcare.iz.darq.users.controller;

import gov.nist.healthcare.auth.domain.PasswordChangeRequest;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.ProfileUpdateRequest;
import gov.nist.healthcare.iz.darq.users.domain.User;
import gov.nist.healthcare.iz.darq.users.exception.FieldValidationException;
import gov.nist.healthcare.iz.darq.users.service.impl.AuthenticationService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    @ResponseBody
    User myProfile(@AuthenticationPrincipal User user) {
        return user;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    OpAck<User> updateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestBody() ProfileUpdateRequest updateRequest) throws OperationFailureException {
        try {
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Profile Updated Successfully", this.userManagementService.updateProfile(updateRequest, user), "PROFILE_UPDATE");
        } catch (FieldValidationException e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

    @RequestMapping(value="/logout", method=RequestMethod.GET)
    @ResponseBody
    public void logout(HttpServletResponse res){
        this.authenticationService.clearLoginCookie(res);
    }
}
