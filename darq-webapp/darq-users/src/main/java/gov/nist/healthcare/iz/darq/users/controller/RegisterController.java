package gov.nist.healthcare.iz.darq.users.controller;

import com.google.common.base.Strings;
import freemarker.template.TemplateException;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.service.exception.OperationPartialFailureException;
import gov.nist.healthcare.iz.darq.users.domain.*;
import gov.nist.healthcare.iz.darq.users.exception.RequestValidationException;
import gov.nist.healthcare.iz.darq.users.service.AccountNotificationService;
import gov.nist.healthcare.iz.darq.users.service.UserTokenizedEditService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class RegisterController {

    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private AccountNotificationService accountNotificationService;
    @Autowired
    private UserTokenizedEditService userTokenizedEditService;


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> register(@RequestBody RegistrationRequest request) throws OperationFailureException, OperationPartialFailureException {
        try {
            User user = this.userManagementService.registerAccount(request);
            this.accountNotificationService.notifyAdminAccountCreated(user);
            this.userTokenizedEditService.requestToken(user, UserEditTokenType.EMAIL_VERIFICATION);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Account Created Successfully", null, "REGISTER");
        } catch (RequestValidationException e) {
            throw new OperationFailureException(e.getMessage());
        } catch (MessagingException | IOException | TemplateException | NotFoundException e) {
            throw new OperationPartialFailureException("Failed to send email verification link, please contact admin to verify your email address");
        }
    }

    @RequestMapping(value = "/reset-password-request", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> resetPasswordRequest(@RequestBody PasswordForgotten passwordForgotten) throws OperationFailureException {
        try {
            if(!Strings.isNullOrEmpty(passwordForgotten.getEmail())) {
                UserAccount userByEmail = this.userManagementService.findAccountByEmail(passwordForgotten.getEmail());
                if(userByEmail != null) {
                    User user = this.userManagementService.fromAccount(userByEmail);
                    this.userTokenizedEditService.requestToken(user, UserEditTokenType.PASSWORD_CHANGE);
                }
                return new OpAck<>(OpAck.AckStatus.INFO, "A password reset link will be sent to your email address.", null, "RESET_PASSWORD_REQUEST");
            } else{
                throw new OperationFailureException("Email can't be empty");
            }
        } catch (NotFoundException | IOException | MessagingException | TemplateException e) {
            throw new OperationFailureException("Error happened while processing the request, try again later or contact admin.");
        }
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws OperationFailureException {
        try {
            this.userTokenizedEditService.resetPassword(resetPasswordRequest);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Password Changed Successfully", null, "CHANGE_PASSWORD");
        } catch (Exception e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

    @RequestMapping(value = "/verify-email", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> verifyEmail(@RequestBody VerifyEmailRequest verifyEmailRequest) throws OperationFailureException {
        try {
            this.userTokenizedEditService.verifyEmail(verifyEmailRequest);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Email verified Successfully", null, "VERIFY_EMAIL");
        } catch (Exception e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

    @RequestMapping(value = "/validate-token", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<Void> validateToken(@RequestBody ValidateTokenRequest validateTokenRequest) throws OperationFailureException {
        try {
            this.userTokenizedEditService.validateToken(validateTokenRequest.getToken(), validateTokenRequest.getContext());
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Valid link", null, "CHECK_TOKEN");
        } catch (NotFoundException e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

}
