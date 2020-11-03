package gov.nist.healthcare.iz.darq.users.controller;

import com.google.common.base.Strings;
import freemarker.template.TemplateException;
import gov.nist.healthcare.domain.OpAck;
import gov.nist.healthcare.iz.darq.access.domain.CreateCredentialsRequest;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.*;
import gov.nist.healthcare.iz.darq.users.exception.FieldValidationException;
import gov.nist.healthcare.iz.darq.users.exception.RequestValidationException;
import gov.nist.healthcare.iz.darq.users.service.AccountNotificationService;
import gov.nist.healthcare.iz.darq.users.service.UserTokenizedEditService;
import gov.nist.healthcare.iz.darq.users.service.impl.AuthenticationService;
import gov.nist.healthcare.iz.darq.users.service.impl.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/user")
public class RegisterController {

    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private AccountNotificationService accountNotificationService;
    @Autowired
    private UserTokenizedEditService userTokenizedEditService;
    @Autowired
    private AuthenticationService authenticationService;


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> register(@RequestBody RegistrationRequest request) throws OperationFailureException {
        try {
            User user = this.userManagementService.registerAccount(request);
            this.accountNotificationService.notifyAdminAccountCreated(user);
            this.userTokenizedEditService.requestToken(user, UserEditTokenType.EMAIL_VERIFICATION);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Account Created Successfully", user, "REGISTER");
        } catch (RequestValidationException | MessagingException | TemplateException | NotFoundException | IOException e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

    @RequestMapping(value = "/reset-password-request", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> resetPasswordRequest(@RequestBody PasswordForgotten passwordForgotten) throws OperationFailureException {
        try {
            if(!Strings.isNullOrEmpty(passwordForgotten.getEmail())) {
                UserAccount userByEmail = this.userManagementService.findAccountByEmail(passwordForgotten.getEmail());
                if(userByEmail == null) {
                    throw new NotFoundException("No match for this email address was found");
                } else {
                    User user = this.userManagementService.fromAccount(userByEmail);
                    this.userTokenizedEditService.requestToken(user, UserEditTokenType.PASSWORD_CHANGE);
                    return new OpAck<>(OpAck.AckStatus.INFO, "Password reset link was sent to your email address", user, "RESET_PASSWORD_REQUEST");
                }
            } else{
                throw new OperationFailureException("Email can't be empty");
            }
        } catch (NotFoundException | IOException | MessagingException | TemplateException e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletResponse response, HttpServletRequest request) throws OperationFailureException {
        try {
            UserAccount account = this.userTokenizedEditService.resetPassword(resetPasswordRequest);
            User user = this.authenticationService.login(request, response, account);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Password Changed Successfully", user, "CHANGE_PASSWORD");
        } catch (NotFoundException | NoSuchAlgorithmException | IOException | InvalidKeySpecException | FieldValidationException e) {
            throw new OperationFailureException(e.getMessage());
        }
    }

    @RequestMapping(value = "/verify-email", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> verifyEmail(@RequestBody VerifyEmailRequest verifyEmailRequest, HttpServletResponse response, HttpServletRequest request) throws OperationFailureException {
        try {
            UserAccount account = this.userTokenizedEditService.verifyEmail(verifyEmailRequest);
            User user = this.authenticationService.login(request, response, account);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Email verified Successfully", user, "VERIFY_EMAIL");
        } catch (NotFoundException | OperationFailureException | NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
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

    @RequestMapping(value = "/create-credentials", method = RequestMethod.POST)
    @ResponseBody
    public OpAck<User> credentials(@RequestBody CreateCredentialsRequest request) throws OperationFailureException {
        try {
            User user = this.userManagementService.createCredentials(request);
            this.accountNotificationService.notifyAdminAccountCreated(user);
            return new OpAck<>(OpAck.AckStatus.SUCCESS, "Account Created Successfully", user, "CREATE_CREDENTIALS");
        } catch (RequestValidationException e) {
            throw new OperationFailureException(e.getMessage());
        }
    }
}
