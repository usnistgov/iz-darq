package gov.nist.healthcare.iz.darq.users.service;

import freemarker.template.TemplateException;
import gov.nist.healthcare.iz.darq.access.service.ConfigurableService;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;
import gov.nist.healthcare.iz.darq.service.exception.OperationFailureException;
import gov.nist.healthcare.iz.darq.users.domain.*;
import gov.nist.healthcare.iz.darq.users.exception.FieldValidationException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface UserTokenizedEditService extends ConfigurableService {
    void requestToken(User user, UserEditTokenType tokenType) throws MessagingException, TemplateException, NotFoundException, IOException;
    UserAccount verifyEmail(VerifyEmailRequest verifyEmailRequest) throws NotFoundException, OperationFailureException;
    UserAccount resetPassword(ResetPasswordRequest resetPasswordRequest) throws NotFoundException, FieldValidationException;
    UserEditToken validateToken(String token, UserEditTokenType tokenType) throws NotFoundException;
}
