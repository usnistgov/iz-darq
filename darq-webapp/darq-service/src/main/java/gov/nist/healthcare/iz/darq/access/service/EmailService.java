package gov.nist.healthcare.iz.darq.access.service;

import freemarker.template.TemplateException;
import gov.nist.healthcare.iz.darq.model.EmailTemplate;
import gov.nist.healthcare.iz.darq.model.EmailType;
import gov.nist.healthcare.iz.darq.service.exception.NotFoundException;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface EmailService {
    List<EmailTemplate> findAll();
    EmailTemplate setEmailTemplate(EmailTemplate emailTemplate);
    EmailTemplate setEmailTemplateIfAbsent(EmailTemplate emailTemplate);
    EmailTemplate getTemplate(EmailType emailType) throws NotFoundException;
    void setEmailServerProperties(Properties properties) throws MessagingException;
    boolean isEnabled(EmailType emailType) throws NotFoundException;
    void send(EmailType emailType, String to, Map<String, String> params) throws NotFoundException, TemplateException, IOException, MessagingException;
    boolean sendIfEnabled(EmailType emailType, String to, Map<String, String> params) throws NotFoundException, TemplateException, IOException, MessagingException;
}
